FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} potato.jar
ENTRYPOINT ["java","-jar","/potato.jar"]