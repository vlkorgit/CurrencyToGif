FROM openjdk:12-jdk-alpine
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} spring-currency-app.jar
ENTRYPOINT ["java","-jar","/spring-currency-app.jar"]
