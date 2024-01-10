# Use an official Maven image to build the application
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory to /app
WORKDIR /app

# Copy only the pom.xml file first to leverage Docker layer caching
COPY pom.xml .

# Copy the entire project
COPY . .

# Build the application
RUN mvn clean install

# Use an official OpenJDK image for the runtime
FROM openjdk:11-jre-slim

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the Maven build stage
COPY --from=build /app/target/mavenproject2-1.0-SNAPSHOT.jar /app/app.jar

# Run the application when the container launches
ENTRYPOINT ["java", "-jar", "app.jar"]
