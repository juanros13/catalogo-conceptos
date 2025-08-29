# Script para levantar solo la infraestructura base (Discovery + Config)
# Uso: .\start-infrastructure.ps1

param(
    [switch]$Stop
)

Write-Host "Gestion de Infraestructura Base para Desarrollo" -ForegroundColor Cyan

# Cambiar al directorio raíz del proyecto
Set-Location "../.."

if ($Stop) {
    Write-Host "Deteniendo servicios de infraestructura..." -ForegroundColor Yellow
    docker-compose stop discovery-service config-server
    docker-compose rm -f discovery-service config-server
    Write-Host "Infraestructura detenida" -ForegroundColor Green
} else {
    # Verificar que existe el archivo .env
    if (!(Test-Path ".env")) {
        Write-Host "Error: No se encontro el archivo .env" -ForegroundColor Red
        Write-Host "Copia .env.example como .env y configura tus valores" -ForegroundColor Yellow
        exit 1
    }

    Write-Host "Iniciando servicios de infraestructura..." -ForegroundColor Green
    
    # Levantar solo Config Server y Discovery Service (en orden correcto)
    docker-compose up --build -d config-server discovery-service
    
    Write-Host "Esperando que los servicios esten listos..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    # Verificar servicios
    Write-Host "Verificando servicios de infraestructura..." -ForegroundColor Cyan
    try {
        $discoveryStatus = (Invoke-WebRequest -Uri "http://localhost:8761" -UseBasicParsing).StatusCode
        Write-Host "Discovery Service (8761): $discoveryStatus" -ForegroundColor Green
    } catch {
        Write-Host "Discovery Service (8761): Error" -ForegroundColor Red
    }

    try {
        $configStatus = (Invoke-WebRequest -Uri "http://localhost:8888/actuator/health" -UseBasicParsing).StatusCode
        Write-Host "Config Server (8888): $configStatus" -ForegroundColor Green
    } catch {
        Write-Host "Config Server (8888): Error" -ForegroundColor Red
    }

    Write-Host "" 
    Write-Host "Infraestructura lista para desarrollo!" -ForegroundColor Green
    Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Cyan
    Write-Host "Config Server Health: http://localhost:8888/actuator/health" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Ahora ejecuta los servicios desde IntelliJ IDEA" -ForegroundColor Yellow
    Write-Host "Para detener: .\start-infrastructure.ps1 -Stop" -ForegroundColor Yellow
}