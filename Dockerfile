# Use slim OpenJDK image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy all project files into the image
COPY . .

# Compile Java files using classpath for all JARs
RUN javac -cp ".:algs4.jar:stdlib.jar" Percolation.java PercolationStatsConcurrent.java

# Default command
ENTRYPOINT ["java", "-cp", ".:algs4.jar:stdlib.jar", "PercolationStatsConcurrent"]
