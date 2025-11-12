# Use official Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files to the container
COPY . .

# Build the project using Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the generated JAR file
CMD ["java", "-jar", "target/*.jar"]
