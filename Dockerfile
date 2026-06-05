FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src/ src/
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring -u 1001 spring \
    && mkdir -p /app/uploads \
    && chown -R spring:spring /app

COPY --from=build /workspace/target/*.jar app.jar

ENV JAVA_OPTS=""

EXPOSE 8080

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
