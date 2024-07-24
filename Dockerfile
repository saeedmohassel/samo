FROM eclipse-temurin:21
LABEL maintainer="ramin.k92@gmail.com"
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wallet-app-0.0.1-SNAPSHOT.jar"]