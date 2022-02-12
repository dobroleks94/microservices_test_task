# Microservices Test
___
Test task for implementation of microservices approach (SOA)

## General project architecture representation

   ![Microservices-Test](https://user-images.githubusercontent.com/45817745/153627473-742f13cf-7a84-4ee2-a58f-94168bee3498.png)

## Technologies used

1. HashiCorp Consul (as provider for _service discovery_, _health-check_, _k/v storage_)
2. OAuth2 (protocol for securing services by authorizing requests)
3. Feign Client (to simplify REST requests to third-party APIs)
4. Docker & Docker Compose (to build services into containers and provide easy deployment configuration)

## Project Flow description

As described at the picture, the project has 4 services, which Consul interacts with.

- **Gateway.** Gateway's aim is to redirect incoming requests to the responsible services in order to emit requested information. It also serves as OAuth2 Client, which interacts with OAuth2 Provider to retrieve Access Token and relay it to the services to authorize particular requests, if required.
- **Service A.** Service A task is to provide user with 'Hello message' and some other minor data, like service's _address_ and _name_. This service has 3 instances and overwritten Load Balancer implementation: for **_Service A_** it is a _'Random algorithm'_; for the rest calls the algorithm is default _Round Robin_.
- **Service B.** This is the only service, which is not registered in Consul, however the services' discovery is eventually ensured here. It provides response to the both requested _User_ and _Client_ information. The service acts as **_resource server_** and demands an authorization.
- **Service C.** It is registering in Consul and requires user's authorization either, because it acts as **_resource server_**. It handles incoming requests to the two endpoints, which provide _User Info_ and _User+Client Info_ and responses with respective values. Considering last endpoint, which returns information about user and client, the service asks the **_Service B_** (Feign client) for such data. It also implements **_request interceptor_** which hands off the Bearer Token value to the services it interacts with.

## Deployment and consuming

The application is deployed in Docker environment after execution of `docker-compose.yml` file, which is located in the root of the project. <br/>
Application consumption is conducted via direct requests to the **Gateway** service, which is accessible on port `9400` of the machine, where project runs. <br/>

The core endpoints to fetch the resource:

- `/s-a/greeting`: **Gateway** redirects to the **Service A** - the endpoint owner - to get greeting message.
- `/s-c/info/user`: The **Service C**'s endpoint which supplies the information about user.
- `/s-c/info`: The owner of the endpoint is the same as the previous one. This endpoint requests **Service B**'s endpoint `/s-b/info`, obtains the _User+Client_ information and responses with it.
