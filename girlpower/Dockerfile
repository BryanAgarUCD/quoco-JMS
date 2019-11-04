FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/girlpower-1.0-jar-with-dependencies.jar /girlpower-1.0.jar
CMD sleep 5; /usr/bin/java -cp /girlpower-1.0.jar service.Receiver ${CONTAINER_NAME}