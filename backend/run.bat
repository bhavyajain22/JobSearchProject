@echo off
echo 🚀 Starting JobFlow Spring Boot Backend...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed or not in PATH
    echo Please install Maven 3.6 or higher
    pause
    exit /b 1
)

echo ✅ Java and Maven found
echo 📡 Starting Spring Boot application...
echo 🌐 API will be available at: http://localhost:3001
echo 📊 Health check: http://localhost:3001/api/health
echo.

mvn spring-boot:run

pause 