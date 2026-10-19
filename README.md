# Microservices Test
___
Test task for implementation of microservices approach (SOA)

## General project architecture representation

   ![Microservices-Test](https://user-images.githubusercontent.com/45817745/153627473-742f13cf-7a84-4ee2-a58f-94168bee3498.png)

## Technologies used

1. Kubernetes + Spring Cloud Kubernetes (as provider for _service discovery_ and client-side load balancing)
2. OAuth2 (protocol for securing services by authorizing requests)
3. Feign Client (to simplify REST requests to third-party APIs)
4. Docker & Kubernetes/minikube (to build services into containers and deploy them)

## Project Flow description

As described at the picture, the project has 4 services, each exposed as a Kubernetes Service.

- **Gateway.** Gateway's aim is to redirect incoming requests to the responsible services in order to emit requested information. It also serves as OAuth2 Client, which interacts with OAuth2 Provider to retrieve Access Token and relay it to the services to authorize particular requests, if required.
- **Service A.** Service A task is to provide user with 'Hello message' and some other minor data, like service's _address_ and _name_. This service has 3 instances and overwritten Load Balancer implementation: for **_Service A_** it is a _'Random algorithm'_; for the rest calls the algorithm is default _Round Robin_.
- **Service B.** This service is not routed by the Gateway, however it uses service discovery to reach **_Service C_**. It provides response to the both requested _User_ and _Client_ information. The service acts as **_resource server_** and demands an authorization.
- **Service C.** It is routed by the Gateway via service discovery and requires user's authorization either, because it acts as **_resource server_**. It handles incoming requests to the two endpoints, which provide _User Info_ and _User+Client Info_ and responses with respective values. Considering last endpoint, which returns information about user and client, the service asks the **_Service B_** (Feign client) for such data. It also implements **_request interceptor_** which hands off the Bearer Token value to the services it interacts with.

## Deployment and consuming

The application is deployed to Kubernetes (minikube) with Helm, each service as a separate release:

- `k8s/application/` - shared chart with the `Deployment`, `Service` and discovery RBAC templates (the only place of common configuration);
- `<service>/k8s/` - the service chart: `Chart.yaml` (depends on `k8s/application`) and `values.yaml` with service-specific settings (`name`, `bindingPort`, `replicas`, `service.port`, `discovery.enabled`, ...).

With `discovery.enabled: true` (**Gateway** and **Service B**) the release creates its own `<name>-discovery` ServiceAccount, Role and RoleBinding, which grant read access to Services/Endpoints/Pods for Spring Cloud Kubernetes discovery.

Requirements: `minikube` (docker driver), `docker`, `kubectl`, `helm`, JDK 17 and the Google OAuth2 client credentials in environment variables. The script fails if any of them is missing.

```sh
export GOOGLE_CLIENT_ID=<client id>
export GOOGLE_CLIENT_SECRET=<client secret>
```

`deploy.sh up` stores them in the `gateway-env` Secret, which the Gateway receives as environment variables (`envFromSecret` in `gateway/k8s/values.yaml`); `deploy.sh down` deletes it. When running the Gateway locally, export the same variables.

The Gateway is a `NodePort` Service (`30940`), and `deploy.sh up` creates the minikube cluster with `--ports=127.0.0.1:9400:30940`, so after deployment the application is available on `http://localhost:9400` without port-forwarding. Ports can only be published when the cluster is created: a cluster created without it has to be recreated once (`minikube delete && ./deploy.sh up`).

```sh
./deploy.sh up     # start minikube if needed, build jars and images, install/upgrade all releases
curl localhost:9400/s-a/greeting
./deploy.sh dashboard   # open the Kubernetes Dashboard in the browser (Ctrl+C to close)
./deploy.sh down   # uninstall all releases, remove service images from minikube, stop minikube
minikube delete    # optional: remove the cluster completely
```

Working with a single service:

```sh
helm dependency update service_A/k8s                # re-vendor k8s/application after template changes
helm upgrade --install service-a service_A/k8s --set app.image.tag=<tag>
helm history service-a
helm rollback service-a <revision>
helm uninstall service-a
```

Application consumption is conducted via direct requests to the **Gateway** service, which is accessible on `http://localhost:9400`. <br/>

The core endpoints to fetch the resource:

- `/s-a/greeting`: **Gateway** redirects to the **Service A** - the endpoint owner - to get greeting message.
- `/s-c/info/user`: The **Service C**'s endpoint which supplies the information about user.
- `/s-c/info`: The owner of the endpoint is the same as the previous one. This endpoint requests **Service B**'s endpoint `/s-b/info`, obtains the _User+Client_ information and responses with it.
