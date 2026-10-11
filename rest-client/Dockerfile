FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY soap-service/pom.xml soap-service/pom.xml
COPY rest-client/pom.xml rest-client/pom.xml
COPY rest-client/src rest-client/src
RUN mvn -B -ntp -pl rest-client -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/rest-client/target/rest-client-*.jar /app/app.jar
ENV SOAP_SERVICE_URL=http://soap-service:8081/ws
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
