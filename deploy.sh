#!/usr/bin/env bash
# Deploys all services to minikube as separate Helm releases (up) and tears everything down (down).
#   ./deploy.sh up     start minikube if needed, build jars and images in parallel, install/upgrade releases in order;
#                      the gateway is then reachable on http://localhost:9400, the dashboard link is printed
#   ./deploy.sh down   uninstall releases, remove service images, stop minikube
#   ./deploy.sh dashboard   open the Kubernetes Dashboard in the browser (Ctrl+C to close)
# Detailed output of every step goes to .deploy/logs/.
# Requires GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET environment variables (gateway OAuth2 client).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$ROOT/.deploy/logs"
DASHBOARD_PID="$ROOT/.deploy/dashboard.pid"
CONTEXT=minikube
# localhost:GATEWAY_PORT -> minikube container -> gateway NodePort (must match gateway/k8s/values.yaml)
GATEWAY_PORT=9400
GATEWAY_NODE_PORT=30940
# Secret with the gateway's OAuth2 credentials (must match envFromSecret in gateway/k8s/values.yaml)
GATEWAY_SECRET=gateway-env
GATEWAY_SECRET_VARS="GOOGLE_CLIENT_ID GOOGLE_CLIENT_SECRET"
# <service directory>:<release and image name>
SERVICES="gateway:gateway service_A:service-a service_B:service-b service_C:service-c"

require_tools() {
  local missing=()
  for tool in minikube kubectl helm docker; do
    command -v "$tool" >/dev/null 2>&1 || missing+=("$tool")
  done
  if [ "${1:-}" = "--with-jdk" ]; then
    JAVA_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null)" || missing+=("JDK 17")
    export JAVA_HOME   # Gradle 7.6 requires JDK 17
  fi
  if [ ${#missing[@]} -gt 0 ]; then
    echo "Missing required tools: ${missing[*]}" >&2
    exit 1
  fi
}

require_secret_env() {
  local missing=()
  for var in $GATEWAY_SECRET_VARS; do
    [ -n "${!var:-}" ] || missing+=("$var")
  done
  if [ ${#missing[@]} -gt 0 ]; then
    echo "Missing required environment variables: ${missing[*]}" >&2
    exit 1
  fi
}

# Prints the end of a step's log and exits
fail() { # <message> <log file>
  echo "$1, last lines of $2:" >&2
  tail -n 20 "$2" >&2
  exit 1
}

minikube_running() {
  minikube status >/dev/null 2>&1
}

start_minikube() {
  if minikube_running; then
    echo "minikube: already running"
    return
  fi
  echo "minikube: starting..."
  minikube start --ports="127.0.0.1:$GATEWAY_PORT:$GATEWAY_NODE_PORT" > "$LOG_DIR/minikube.log" 2>&1 \
    || fail "minikube: start FAILED" "$LOG_DIR/minikube.log"
  echo "minikube: started"
}

# Host ports can only be published when the minikube cluster is created
require_gateway_port() {
  if ! docker port minikube "$GATEWAY_NODE_PORT/tcp" 2>/dev/null | grep -q ":$GATEWAY_PORT$"; then
    echo "minikube cluster does not publish localhost:$GATEWAY_PORT -> $GATEWAY_NODE_PORT." >&2
    echo "Recreate it once: minikube delete && $0 up" >&2
    exit 1
  fi
}

# Builds jars and images of all services in parallel; fails if any build fails
build_images() { # <tag>
  local tag=$1 jobs=() failed=()
  for s in $SERVICES; do
    local dir=${s%%:*} name=${s##*:}
    echo "build: $name started"
    (
      SECONDS=0
      if (cd "$ROOT/$dir" && ./gradlew build && minikube image build -t "$name:$tag" .) \
          > "$LOG_DIR/$name-build.log" 2>&1; then
        echo "build: $name finished (${SECONDS}s)"
      else
        echo "build: $name FAILED (${SECONDS}s)"
        exit 1
      fi
    ) &
    jobs+=("$!:$name")
  done

  for job in "${jobs[@]}"; do
    wait "${job%%:*}" || failed+=("${job##*:}")
  done
  if [ ${#failed[@]} -gt 0 ]; then
    for name in "${failed[@]}"; do
      echo >&2
      echo "build: $name log ($LOG_DIR/$name-build.log):" >&2
      tail -n 20 "$LOG_DIR/$name-build.log" >&2
    done
    exit 1
  fi
}

# Creates/updates the gateway Secret from environment variables
apply_gateway_secret() {
  local args=() log="$LOG_DIR/secret.log"
  for var in $GATEWAY_SECRET_VARS; do
    args+=(--from-literal="$var=${!var}")
  done
  kubectl --context "$CONTEXT" create secret generic "$GATEWAY_SECRET" "${args[@]}" --dry-run=client -o yaml \
    | kubectl --context "$CONTEXT" apply -f - > "$log" 2>&1 \
    || fail "secret: $GATEWAY_SECRET FAILED" "$log"
}

# Installs/upgrades releases one by one, in SERVICES order
deploy_releases() { # <tag>
  local tag=$1
  apply_gateway_secret
  for s in $SERVICES; do
    local dir=${s%%:*} name=${s##*:}
    local log="$LOG_DIR/$name-helm.log"
    # Re-vendor k8s/application every time, otherwise template changes are not picked up
    { helm dependency update "$ROOT/$dir/k8s" \
        && helm upgrade --install "$name" "$ROOT/$dir/k8s" --kube-context "$CONTEXT" \
             --set app.image.tag="$tag" --wait --timeout 5m; } > "$log" 2>&1 \
      || fail "helm: $name FAILED" "$log"
    echo "helm: $name $(helm status "$name" --kube-context "$CONTEXT" \
      | awk '/^STATUS:/ {s=$2} /^REVISION:/ {r=$2} END {print s " (revision " r ")"}')"
  done
}

# Runs the dashboard proxy in the background (once) and prints its link
dashboard_link() {
  local log="$LOG_DIR/dashboard.log" url=""
  if ! { [ -f "$DASHBOARD_PID" ] && kill -0 "$(cat "$DASHBOARD_PID")" 2>/dev/null; }; then
    nohup minikube dashboard --url > "$log" 2>&1 &
    echo $! > "$DASHBOARD_PID"
  fi
  for _ in $(seq 1 60); do
    url=$(grep -m1 -o 'http://[^ ]*' "$log" || true)
    [ -n "$url" ] && break
    sleep 2
  done
  if [ -n "$url" ]; then
    echo "Dashboard: $url"
  else
    echo "Dashboard: link not available yet, see $log"
  fi
}

stop_dashboard() {
  [ -f "$DASHBOARD_PID" ] || return 0
  local pid
  pid=$(cat "$DASHBOARD_PID")
  pkill -P "$pid" 2>/dev/null || true   # kubectl proxy started by minikube dashboard
  kill "$pid" 2>/dev/null || true
  rm -f "$DASHBOARD_PID"
}

up() {
  require_tools --with-jdk
  require_secret_env
  mkdir -p "$LOG_DIR"
  start_minikube
  require_gateway_port

  # Unique tag: a constant tag would leave the pod spec unchanged and helm upgrade would not roll pods
  local tag="${TAG:-$(git -C "$ROOT" rev-parse --short HEAD)-$(date +%Y%m%d%H%M%S)}"
  build_images "$tag"
  deploy_releases "$tag"

  echo
  echo "Gateway:   http://localhost:$GATEWAY_PORT"
  dashboard_link
}

down() {
  require_tools
  stop_dashboard
  if ! minikube_running; then
    echo "minikube is not running, nothing to tear down"
    return
  fi

  for s in $SERVICES; do
    local name=${s##*:}
    if helm status "$name" --kube-context "$CONTEXT" >/dev/null 2>&1; then
      helm uninstall "$name" --kube-context "$CONTEXT" --wait
    fi
  done
  kubectl --context "$CONTEXT" delete secret "$GATEWAY_SECRET" --ignore-not-found

  for s in $SERVICES; do
    local name=${s##*:}
    minikube image ls --format short | grep -E "(^|/)$name:" | while read -r image; do
      minikube image rm "$image"
    done || true
  done

  minikube stop
}

dashboard() {
  require_tools
  if ! minikube_running; then
    echo "minikube is not running, run: $0 up" >&2
    exit 1
  fi
  minikube dashboard
}

case "${1:-}" in
  up) up ;;
  down) down ;;
  dashboard) dashboard ;;
  *) echo "Usage: $0 up|down|dashboard" >&2; exit 1 ;;
esac
