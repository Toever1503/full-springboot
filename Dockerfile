FROM openjdk:11.0.15-jdk-buster
WORKDIR app
COPY build/libs/SpringbootMarketplaceCy-0.0.1-SNAPSHOT.jar /app/SpringbootMarketplaceCy-0.0.1-SNAPSHOT.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/SpringbootMarketplaceCy-0.0.1-SNAPSHOT.jar"]