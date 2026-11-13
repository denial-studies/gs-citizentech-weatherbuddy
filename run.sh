#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

# =========================================================================
# CONFIGURACAO DE AMBIENTE (Opcional)
# Informe o caminho do JDK 25 caso nao esteja no PATH padrao do sistema:
# =========================================================================
JDK_HOME=""

if [ -n "$JDK_HOME" ]; then
    export JAVA_HOME="$JDK_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "========================================================================="
echo "  Iniciando Weather Buddy (Time Hidro-Alerta)"
echo "========================================================================="

if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
fi

echo "[INFO] Empacotando os modulos Maven do Weather Buddy..."
./mvnw package -DskipTests

echo "[INFO] Iniciando servidor Spring Boot na porta 8080..."
java -jar presentation/target/presentation-1.0.0-SNAPSHOT.jar
