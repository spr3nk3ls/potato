FROM adoptopenjdk/openjdk11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} potato.jar
RUN mkdir videos
ENTRYPOINT ["java","-jar","/potato.jar"]
