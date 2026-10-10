#!/bin/bash

# Build all modules
echo "Building all microservices..."
mvn clean install -DskipTests

# Function to kill child processes on exit
cleanup() {
    echo "Stopping all microservices..."
    kill -9 $RESOURCE_PID $INVENTORY_PID $REQUEST_PID $FRONTEND_PID 2>/dev/null
    exit 0
}

# Trap SIGINT (Ctrl+C) and call cleanup
trap cleanup SIGINT SIGTERM

echo "Starting Resource Service (Port 9191)..."
cd resource-service
mvn spring-boot:run &
RESOURCE_PID=$!
cd ..

echo "Starting Inventory Service (Port 9192)..."
cd inventory-service
mvn spring-boot:run &
INVENTORY_PID=$!
cd ..

echo "Starting Request Service (Port 9193)..."
cd request-service
mvn spring-boot:run &
REQUEST_PID=$!
cd ..

echo "Starting Frontend UI (Port 8080)..."
cd frontend-ui
mvn spring-boot:run &
FRONTEND_PID=$!
cd ..

echo "=================================================="
echo "All services are booting up in the background!"
echo "Resource Service:  http://localhost:9191/swagger-ui.html"
echo "Inventory Service: http://localhost:9192/swagger-ui.html"
echo "Request Service:   http://localhost:9193/swagger-ui.html"
echo "Frontend UI:       http://localhost:8080"
echo "=================================================="
echo "Press [CTRL+C] to stop all services."

# Wait for all background processes
wait $RESOURCE_PID $INVENTORY_PID $REQUEST_PID $FRONTEND_PID
