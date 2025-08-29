# Script PowerShell para iniciar servicios con Docker Compose
# Uso: .\start-services.ps1 [basic|full]

param(
    [string]$Profile = "basic"
)

Write-Host "Iniciando servicios del Sistema de Acceso Tabasco" -ForegroundColor Cyan
Write-Host "Perfil: $Profile" -ForegroundColor Yellow

# Verificar que existe el archivo .env
if (!(Test-Path "../../.env")) {
    Write-Host "Error: No se encontro el archivo .env" -ForegroundColor Red
    Write-Host "Copia .env.example como .env y configura tus valores" -ForegroundColor Yellow
    exit 1
}

# Cambiar al directorio raíz del proyecto
Set-Location "../.."

# Detener servicios existentes
Write-Host "Deteniendo servicios existentes..." -ForegroundColor Yellow
docker-compose down

# Iniciar según el perfil
switch ($Profile) {
    "basic" {
        Write-Host "Iniciando servicios basicos (Config + Discovery + Gateway)..." -ForegroundColor Green
        docker-compose up --build -d config-server discovery-service gateway-service
    }
    "full" {
        Write-Host "Iniciando todos los servicios..." -ForegroundColor Green
        docker-compose --profile full up --build -d
    }
    default {
        Write-Host "Perfil no valido. Usa: basic o full" -ForegroundColor Red
        exit 1
    }
}

Write-Host "Esperando que los servicios esten listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Verificar servicios
Write-Host "Verificando servicios..." -ForegroundColor Cyan
try {
    $discoveryStatus = (Invoke-WebRequest -Uri "http://localhost:8761" -UseBasicParsing).StatusCode
    Write-Host "Discovery Service: $discoveryStatus" -ForegroundColor Green
} catch {
    Write-Host "Discovery Service: Error" -ForegroundColor Red
}

try {
    $configStatus = (Invoke-WebRequest -Uri "http://localhost:8888/actuator/health" -UseBasicParsing).StatusCode
    Write-Host "Config Server: $configStatus" -ForegroundColor Green
} catch {
    Write-Host "Config Server: Error" -ForegroundColor Red
}

try {
    $gatewayStatus = (Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing).StatusCode
    Write-Host "Gateway Service: $gatewayStatus" -ForegroundColor Green
} catch {
    Write-Host "Gateway Service: Error" -ForegroundColor Red
}

if ($Profile -eq "full") {
    try {
        $authStatus = (Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing).StatusCode
        Write-Host "Auth Service: $authStatus" -ForegroundColor Green
    } catch {
        Write-Host "Auth Service: Error" -ForegroundColor Red
    }
    
    try {
        $userStatus = (Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -UseBasicParsing).StatusCode
        Write-Host "User Management: $userStatus" -ForegroundColor Green
    } catch {
        Write-Host "User Management: Error" -ForegroundColor Red
    }
}

Write-Host "Servicios iniciados!" -ForegroundColor Green
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Cyan
Write-Host "Para ver logs: docker-compose logs -f" -ForegroundColor Yellow