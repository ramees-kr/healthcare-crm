FROM eclipse-temurin:21-jdk-jammy

# Set a working directory inside the container
WORKDIR /app

# Copy the packaged JAR file from the target directory into the container's working directory
# Make sure the JAR filename matches the one generated by `mvn package`
COPY target/healthcarecrm-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on (from application.properties)
EXPOSE 8080

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]