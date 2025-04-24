# Use the OpenJDK 11 base image
FROM gradle:7.6-jdk11-alpine

# Set the working directory to the project root
WORKDIR /app

# Copy the project files into the image
COPY . .

# Build the project with Gradle
RUN gradle clean build -x test

EXPOSE 80

ENV TZ Asia/Seoul

# Run the built project
CMD ["java", "-jar", "build/libs/oauth2-0.0.1-SNAPSHOT.jar"]