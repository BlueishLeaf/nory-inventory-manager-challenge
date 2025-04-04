# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user to run the application
#RUN addgroup --system spring && adduser --system spring --ingroup spring
#USER spring:spring

# Copy the built JAR from the build stage
#COPY --from=build --chown=spring:spring /app/target/*.jar app.jar
COPY --from=build /app/target/*.jar app.jar

# Configure JVM options
ENV JAVA_OPTS="-Xms512m -Xmx512m"

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]