#!/bin/bash

# Production deployment script for Itinerarly Backend

set -e

echo "Starting production deployment..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please copy .env.template to .env and configure your values."
    exit 1
fi

# Load environment variables
export $(cat .env | xargs)

# Validate required environment variables
required_vars=("DB_PASSWORD" "JWT_SECRET" "GOOGLE_CLIENT_ID" "GOOGLE_CLIENT_SECRET" "GITHUB_CLIENT_ID" "GITHUB_CLIENT_SECRET")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Required environment variable $var is not set"
        exit 1
    fi
done

echo "✅ Environment variables validated"

# Build and start services
echo "🔨 Building and starting services..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
timeout 120 bash -c 'until docker-compose ps | grep -q "healthy"; do sleep 5; done'

echo "✅ Services are healthy and running"

# Show running containers
echo "📊 Running containers:"
docker-compose ps

echo "🎉 Production deployment completed successfully!"
echo "🌐 Backend is available at: http://localhost:${BACKEND_PORT:-8080}"
echo "📊 Health check: http://localhost:${BACKEND_PORT:-8080}/api/v1/start"
