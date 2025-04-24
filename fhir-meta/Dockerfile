# Use the OpenJDK 17 base image
FROM maven:3.8-openjdk-17-slim as build-meta

# Set the working directory to the project root
WORKDIR /app

# Copy the project files into the image
COPY . .

# Build the project with Maven
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

ENV TZ Asia/Seoul

# Run the built project
CMD ["java", "-jar", "target/fhir-meta-0.0.1-SNAPSHOT.jar", "--spring.config.name=application-prod"]
