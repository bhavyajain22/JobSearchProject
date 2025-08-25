#!/bin/bash

echo "🚀 Starting JobFlow Spring Boot Backend..."
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    echo "Please install Maven 3.6 or higher"
    exit 1
fi

echo "✅ Java and Maven found"
echo "📡 Starting Spring Boot application..."
echo "🌐 API will be available at: http://localhost:3001"
echo "📊 Health check: http://localhost:3001/api/health"
echo

mvn spring-boot:run 