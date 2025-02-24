FROM openjdk:17-jdk
WORKDIR /app
COPY target/producttracking-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dmanagement.metrics.binders.processor.enabled=false", "-Dmanagement.metrics.binders.tomcat.enabled=false", "-jar", "app.jar"]