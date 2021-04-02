# Start with a base image containing Java runtime
FROM java:8

# Add a volume to /tmp
VOLUME /tmp

# Make port 8081 available to the world outside this container
EXPOSE 8084

# The application's jar file
ARG JAR_FILE=/*.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dopentracing.jaeger.enable-b3-propagation=true","-jar","/app.jar"]
