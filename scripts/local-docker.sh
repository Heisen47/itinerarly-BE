#!/bin/bash

# Local Docker Build and Run Script for OrbStack
# This script builds and runs the Itinerarly backend locally using Docker

set -e

echo "🏗️  Building Itinerarly Backend Docker Image..."

# Check if OrbStack is running
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not available. Please make sure OrbStack is running."
    exit 1
fi

# Build the Docker image
echo "📦 Building Docker image..."
docker build -t itinerarly-backend:latest .

echo "✅ Docker image built successfully!"

# Ask user which environment to run
echo ""
echo "🚀 Choose environment to run:"
echo "1) Development (with local MySQL)"
echo "2) Production (with remote MySQL)"
echo "3) Just build (don't run)"

read -p "Enter your choice (1-3): " choice

case $choice in
    1)
        echo "🔧 Starting development environment..."
        docker-compose -f docker-compose.dev.yml up -d
        echo "✅ Development environment started!"
        echo "🌐 Backend available at: http://localhost:8081"
        echo "🗄️  MySQL available at: localhost:3307"
        echo ""
        echo "📊 To view logs: docker-compose -f docker-compose.dev.yml logs -f"
        echo "🛑 To stop: docker-compose -f docker-compose.dev.yml down"
        ;;
    2)
        echo "🚀 Starting production environment..."
        echo "⚠️  Make sure your environment variables are set!"
        docker-compose -f docker-compose.prod.yml up -d
        echo "✅ Production environment started!"
        echo "🌐 Backend available at: http://localhost:8080"
        echo ""
        echo "📊 To view logs: docker-compose -f docker-compose.prod.yml logs -f"
        echo "🛑 To stop: docker-compose -f docker-compose.prod.yml down"
        ;;
    3)
        echo "✅ Build complete! Image ready: itinerarly-backend:latest"
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""
echo "🎉 Script completed successfully!"
