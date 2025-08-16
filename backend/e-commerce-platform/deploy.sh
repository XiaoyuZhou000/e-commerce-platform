#!/bin/bash

# E-commerce Platform Lambda Deployment Script

set -e

echo "Starting deployment process..."
echo "Building project with Maven..."
mvn clean package
copy target\ecommerce-lambda-1.0.0.jar ecommerce-lambda.jar
if [ ! -f "target/ecommerce-lambda-1.0.0.jar" ]; then
    echo "Build failed! JAR file not found."
    exit 1
fi
echo "Build successful!"
echo "Deploying to AWS Lambda using SAM..."
sam deploy
echo "Deployment completed successfully"


