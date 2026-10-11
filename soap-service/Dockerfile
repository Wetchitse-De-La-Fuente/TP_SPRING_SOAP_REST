FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY soap-service/pom.xml soap-service/pom.xml
COPY rest-client/pom.xml rest-client/pom.xml
COPY soap-service/src soap-service/src
RUN mvn -B -ntp -pl soap-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/soap-service/target/soap-service-*.jar /app/app.jar
EXPOSE 8081
VOLUME ["/app/data"]
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
