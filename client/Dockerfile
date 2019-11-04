FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/client-1.0-jar-with-dependencies.jar /client-1.0.jar
CMD sleep 7; /usr/bin/java -cp /client-1.0.jar client.Main ${CONTAINER_NAME}