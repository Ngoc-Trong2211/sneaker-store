FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/sneaker-store-0.0.1-SNAPSHOT.jar sneaker.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "sneaker.jar"]
