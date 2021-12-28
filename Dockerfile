FROM openjdk:11-jre-slim-bullseye
RUN adduser --system --group springdocker
USER springdocker:springdocker
ARG JAR_FILE=app/build/libs/authenticate-gateway.jar
COPY ${JAR_FILE} authenticate-gateway.jar
ENTRYPOINT ["java","-jar", \
"/authenticate-gateway.jar"]
# ENV variables to add in docker-compose.yml
