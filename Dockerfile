# Use Eclipse Temurin 17 as the base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
# Replace 'your-app-name.jar' with the actual name of your JAR file
COPY target/project-0.0.1.jar app.jar

# Expose the port that your Spring Boot application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]