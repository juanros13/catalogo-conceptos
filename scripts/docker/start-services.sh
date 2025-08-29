#!/bin/bash
# Script para iniciar todos los servicios con Docker Compose
# Uso: ./start-services.sh [basic|full]

PROFILE=${1:-basic}

echo "🚀 Iniciando servicios del Sistema de Acceso Tabasco"
echo "📋 Perfil: $PROFILE"

# Verificar que existe el archivo .env
if [ ! -f ".env" ]; then
    echo "❌ Error: No se encontró el archivo .env"
    echo "💡 Copia .env.example como .env y configura tus valores"
    exit 1
fi

# Detener servicios existentes
echo "🛑 Deteniendo servicios existentes..."
docker-compose down

# Iniciar según el perfil
case $PROFILE in
    "basic")
        echo "🏗️ Iniciando servicios básicos (Config + Discovery + Gateway)..."
        docker-compose up --build -d config-server discovery-service gateway-service
        ;;
    "full")
        echo "🏗️ Iniciando todos los servicios..."
        docker-compose --profile full up --build -d
        ;;
    *)
        echo "❌ Perfil no válido. Usa: basic o full"
        exit 1
        ;;
esac

echo "⏳ Esperando que los servicios estén listos..."
sleep 30

# Verificar servicios
echo "🔍 Verificando servicios..."
echo "Discovery Service: $(curl -s http://localhost:8761 -w "%{http_code}" -o /dev/null)"
echo "Config Server: $(curl -s http://localhost:8888/actuator/health -w "%{http_code}" -o /dev/null)"
echo "Gateway Service: $(curl -s http://localhost:8080/actuator/health -w "%{http_code}" -o /dev/null)"

if [ "$PROFILE" = "full" ]; then
    echo "Auth Service: $(curl -s http://localhost:8081/actuator/health -w "%{http_code}" -o /dev/null)"
    echo "User Management: $(curl -s http://localhost:8082/actuator/health -w "%{http_code}" -o /dev/null)"
fi

echo "✅ Servicios iniciados!"
echo "🌐 Eureka Dashboard: http://localhost:8761"
echo "📊 Para ver logs: docker-compose logs -f"