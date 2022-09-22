# Demo project for Spring Cloud.

### Requirements:

- JDK-8
- Maven 3
- Docker in Swarm mode runnable without sudo
- 4GB Free RAM

### Modules:

- Gateway: Zuul Proxy
- Oauth2 Authentication
- Oauth2, AmusementPark, Zoo and Visitor services behind Zuul
- Visitor service is called with Feign
- Config service
- Discovery service 
- Distributed Tracing Zipkin with RabbitMQ

### Run:

- Build the database with some sample data and the applications and pull the images with the buildAll.sh script.
- Run the runAll.sh script or execute all commands from it.

### Usage:

- Open localhost:8080 in a browser
- Login with admin/pass
- Refresh and click on the Amusement Park / Zoo buttons until the messages show up, continue until you see different visitor service ports.
- Config service: localhost:8888/gateway/default
- Discovery service: localhost:8761
- Zipkin: localhost:9411
- RabbitMQ: localhost:15672 guest/guest
- Pgadmin4: localhost:8079 nembence1994@gmail.com/admin db:5432 postgres/admin

#### Call hierarchy in Zipkin:

Every service validates the OAuth token by an http call to the OAuth service, this can be seen beautifully in Zipkin.

![call hierarchy in Zipkin](https://github.com/bence9410/micro/blob/master/zipkin.png?raw=true)