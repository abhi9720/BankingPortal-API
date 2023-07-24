FROM eclipse-temurin:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar bankingportal.jar
ENTRYPOINT ["java", "-jar", "/bankingportal.jar"]
EXPOSE 8080