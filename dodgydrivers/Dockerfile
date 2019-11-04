FROM openjdk:8-jre-alpine
ENV CONTAINER_NAME localhost
COPY target/dodgydrivers-1.0-jar-with-dependencies.jar /dodgydrivers-1.0.jar
CMD sleep 5; /usr/bin/java -cp /dodgydrivers-1.0.jar service.Receiver ${CONTAINER_NAME}