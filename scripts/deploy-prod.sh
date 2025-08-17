#!/bin/bash

# Production Deployment Script
# This script helps deploy the Itinerarly backend to production

set -e

echo "🚀 Itinerarly Backend Production Deployment"
echo "=========================================="

# Check if required environment variables are set
required_vars=(
    "SPRING_DATASOURCE_URL"
    "SPRING_DATASOURCE_USERNAME"
    "SPRING_DATASOURCE_PASSWORD"
    "JWT_SECRET"
    "FRONTEND_URL"
    "GITHUB_CLIENT_ID"
    "GITHUB_CLIENT_SECRET"
    "GOOGLE_CLIENT_ID"
    "GOOGLE_CLIENT_SECRET"
)

echo "🔍 Checking required environment variables..."
missing_vars=()

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        missing_vars+=("$var")
    fi
done

if [ ${#missing_vars[@]} -ne 0 ]; then
    echo "❌ Missing required environment variables:"
    printf '   - %s\n' "${missing_vars[@]}"
    echo ""
    echo "Please set these variables before running the deployment."
    exit 1
fi

echo "✅ All required environment variables are set!"

# Build the application
echo ""
echo "📦 Building application..."
mvn clean package -DskipTests

# Build Docker image
echo ""
echo "🏗️  Building Docker image..."
docker build -t itinerarly-backend:latest .

# Run tests
echo ""
echo "🧪 Running tests..."
mvn test

# Deploy
echo ""
echo "🚀 Deploying to production..."
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d

# Wait for health check
echo ""
echo "⏳ Waiting for application to be healthy..."
timeout=60
counter=0

while [ $counter -lt $timeout ]; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ Application is healthy!"
        break
    fi

    echo "   Waiting for health check... ($((counter + 1))/$timeout)"
    sleep 1
    counter=$((counter + 1))
done

if [ $counter -eq $timeout ]; then
    echo "❌ Application failed to become healthy within $timeout seconds"
    echo "📊 Checking logs..."
    docker-compose -f docker-compose.prod.yml logs backend
    exit 1
fi

echo ""
echo "🎉 Production deployment completed successfully!"
echo "🌐 Backend available at: http://localhost:8080"
echo "📊 Health check: http://localhost:8080/actuator/health"
echo ""
echo "📊 To view logs: docker-compose -f docker-compose.prod.yml logs -f"
echo "🛑 To stop: docker-compose -f docker-compose.prod.yml down"
