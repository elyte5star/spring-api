FROM eclipse-temurin:17-jdk-alpine
LABEL maintainer="Ogaga Uti <checkuti@gmail.com>"
LABEL version="0.0.1"
LABEL description="Spring Boot application for E-commerce"
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java","-jar","/app.jar"]