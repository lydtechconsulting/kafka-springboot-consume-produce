# Kafka Spring Boot Consume & Produce Demo Project

Spring Boot application demonstrating usage of Kafka consumers and producers.

![Spring Boot application with Kafka](resources/kafka-consume-and-produce.png)

This repo accompanies the following series of articles on Kafka Consume & Produce: 

- [Kafka Consume & Produce: Spring Boot Demo](https://www.lydtechconsulting.com/blog-kafka-consume-produce-demo.html) 
- [Kafka Consume & Produce: Testing](https://www.lydtechconsulting.com/blog-kafka-consume-produce-testing.html)
- [Kafka Consume & Produce: At-Least-Once Delivery](https://www.lydtechconsulting.com/blog-kafka-consume-produce-at-least-once.html)

## Build

With Java version 17:

```
mvn clean install
```

## Run Spring Boot Application

### Run docker containers

From root dir run the following to start dockerised Kafka, Zookeeper, and Confluent Control Center:
```
docker-compose up -d
```

### Start demo spring boot application
```
java -jar target/kafka-springboot-consume-produce-2.4.0.jar
```
(Check latest version in pom.xml).

### Produce an inbound event:

Jump onto the Kafka docker container and produce a demo-inbound message:
```
docker exec -ti kafka kafka-console-producer \
--topic demo-inbound-topic \
--broker-list kafka:29092 \
--property "key.separator=:" \
--property parse.key=true
```
Now enter the message (with key prefix):
```
"my-key":{"id": "123-abc", "data": "my-data"}
```
The demo-inbound message is consumed by the application, which emits a resulting demo-outbound message.

Check for the emitted demo-outbound message:
```
docker exec -ti kafka kafka-console-consumer \
--topic demo-outbound-topic \
--bootstrap-server kafka:29092 \
--from-beginning
```
Output:
```
payload: {"id":"a210c3f0-a2e9-4d0d-8928-9c20549bbbd8","data":"my-data"}
```

### Kafka Confluent Control Center

Confluent Control Center is a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.

Navigate to the Control Center:
```
http://localhost:9021
```

### Conduktor Console

Conduktor is a UI over the Kafka cluster, providing a view of the configuration, data and information on the brokers, topics and messages.

Navigate to Conduktor:
```
http://localhost:8088
```

Login with `admin@conduktor.io` / `password`

### Command Line Tools

#### View topics

List topics:
```
docker exec -ti kafka kafka-topics --list --bootstrap-server localhost:9092
```

## Integration Tests

Run integration tests with `mvn clean test`

The tests demonstrate sending events to an embedded in-memory Kafka that are consumed by the application, resulting in outbound events being published.

## Component Tests

The tests demonstrate sending events to a dockerised Kafka that are consumed by the dockerised application, resulting in outbound events being published.

Confluent Control Center and/or Conduktor can be enabled in the `pom.xml` to run during the component tests.  Leave the containers up between test runs to inspect the topics and messages created by the tests, by navigating to the URLs above.

To test the application running against the Apache Kafka native instance, set `kafka.enabled` to `false` and `kafka.native.enabled` to `true` in the `pom.xml`.  Change the `bootstrap-servers` to use `kafka:9093` in `application-component-test.yml`.

For more on the component tests see: https://github.com/lydtechconsulting/component-test-framework

Build Spring Boot application jar:
```
mvn clean install
```

Build Docker container:
```
docker build -t ct/kafka-springboot-consume-produce:latest .
```

Run tests:
```
mvn test -Pcomponent
```

Run tests leaving containers up:
```
mvn test -Pcomponent -Dcontainers.stayup
```

Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

Further docker clean up if network/other issues:
```
docker system prune
docker volume prune
```
