FROM openjdk:11-jre-slim-bullseye
RUN adduser --system --group springdocker
USER springdocker:springdocker
ARG JAR_FILE=app/build/libs/authenticate-gateway.jar
COPY ${JAR_FILE} authenticate-gateway.jar
ENTRYPOINT ["java","-jar", \
#"-DPORT=9999", \
#"-DTZ=America/Denver", \
#"-DSPRING_PROFILES_ACTIVE=docker", \
#"-DAPIKEY=some_api_key", \
#"-DAPPID=some_app_id", \
#"-DDATASOURCE=some_datasource", \
#"-DHDT_PWD=some_passowrd", \
#"-DHDT_USR=some_username", \
#"-DPD_PWD=another_password", \
#"-DPD_USR=another_username", \
#"-DPS_PWD=another_password", \
#"-DPS_USR=another_username", \
#"-DSECRET_KEY=some_secret_key", \
"/authenticate-gateway.jar"]
# ENV variables to add in docker-compose.yml
