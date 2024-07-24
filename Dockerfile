FROM eclipse-temurin:21
LABEL maintainer="ramin.k92@gmail.com"
WORKDIR /app
COPY target/wallet-app-0.0.1-SNAPSHOT.jar /app/wallet-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wallet-app.jar"]