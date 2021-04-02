docker build --tag springboot-demo-kafka-consumer .
docker tag springboot-demo-kafka-consumer:latest soondo21/springboot-demo-kafka-consumer:latest
docker push soondo21/springboot-demo-kafka-consumer:latest
