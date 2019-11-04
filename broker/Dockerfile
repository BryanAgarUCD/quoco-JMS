FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/broker-1.0-jar-with-dependencies.jar /broker-1.0.jar
CMD sleep 5; /usr/bin/java -cp /broker-1.0.jar service.Broker ${CONTAINER_NAME}