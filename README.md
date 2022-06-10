# Kafka Spring Boot Consume & Produce Demo Project

Spring Boot application demonstrating usage of Kafka consumers and producers.

## Integration Tests

Run integration tests with `mvn clean test`

The tests demonstrate sending events to an embedded in-memory Kafka that are consumed by the application, resulting in outbound events being published.

## Component Tests

The tests demonstrate sending events to a dockerised Kafka that are consumed by the dockerised application, resulting in outbound events being published.

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
