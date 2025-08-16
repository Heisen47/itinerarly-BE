#!/bin/bash

# Local Docker build and run script for OrbStack
# Usage: ./scripts/local-docker.sh [build|run|rebuild|logs|stop]

set -e

PROJECT_NAME="itinerarly"
IMAGE_NAME="itinerarly-backend"
CONTAINER_NAME="itinerarly-backend-local"

case "${1:-build}" in
    "build")
        echo "🔨 Building local Docker image with OrbStack..."
        docker build -t ${IMAGE_NAME}:local .
        echo "✅ Image built successfully: ${IMAGE_NAME}:local"
        docker images | grep ${IMAGE_NAME}
        ;;

    "run")
        echo "🚀 Running local container..."

        # Check if .env exists
        if [ ! -f .env ]; then
            echo "⚠️  .env file not found. Creating from template..."
            cp .env.template .env
            echo "📝 Please edit .env file with your configuration"
        fi

        # Load environment variables
        export $(cat .env | grep -v '^#' | xargs)

        # Run the container
        docker run -d \
            --name ${CONTAINER_NAME} \
            -p 8080:8080 \
            -e SPRING_PROFILES_ACTIVE=dev \
            -e SPRING_DATASOURCE_URL="jdbc:h2:mem:testdb" \
            -e SPRING_DATASOURCE_USERNAME=sa \
            -e SPRING_DATASOURCE_PASSWORD= \
            -e SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop \
            -e JWT_SECRET="${JWT_SECRET:-default-secret-for-local-testing}" \
            -e GOOGLE_CLIENT_ID="${GOOGLE_CLIENT_ID:-}" \
            -e GOOGLE_CLIENT_SECRET="${GOOGLE_CLIENT_SECRET:-}" \
            -e GITHUB_CLIENT_ID="${GITHUB_CLIENT_ID:-}" \
            -e GITHUB_CLIENT_SECRET="${GITHUB_CLIENT_SECRET:-}" \
            ${IMAGE_NAME}:local

        echo "✅ Container started: ${CONTAINER_NAME}"
        echo "🌐 Application available at: http://localhost:8080"
        echo "📊 Health check: http://localhost:8080/api/v1/start"
        echo "📚 Swagger UI: http://localhost:8080/swagger-ui/index.html"
        ;;

    "rebuild")
        echo "🔄 Rebuilding and running..."
        docker stop ${CONTAINER_NAME} 2>/dev/null || true
        docker rm ${CONTAINER_NAME} 2>/dev/null || true
        docker build -t ${IMAGE_NAME}:local .
        $0 run
        ;;

    "logs")
        echo "📋 Showing container logs..."
        docker logs -f ${CONTAINER_NAME}
        ;;

    "stop")
        echo "🛑 Stopping container..."
        docker stop ${CONTAINER_NAME} 2>/dev/null || true
        docker rm ${CONTAINER_NAME} 2>/dev/null || true
        echo "✅ Container stopped and removed"
        ;;

    "shell")
        echo "🐚 Opening shell in container..."
        docker exec -it ${CONTAINER_NAME} /bin/sh
        ;;

    *)
        echo "Usage: $0 [build|run|rebuild|logs|stop|shell]"
        echo ""
        echo "Commands:"
        echo "  build   - Build the Docker image"
        echo "  run     - Run the container locally"
        echo "  rebuild - Rebuild image and restart container"
        echo "  logs    - Show container logs"
        echo "  stop    - Stop and remove container"
        echo "  shell   - Open shell in running container"
        ;;
esac
