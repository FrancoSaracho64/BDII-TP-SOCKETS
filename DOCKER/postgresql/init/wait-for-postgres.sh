#!/bin/bash
set -e

echo "Esperando a que PostgreSQL esté disponible en $HOST_POSTGRESQL_DB:$PORT_POSTGRESQL_DB..."
until pg_isready -h "$HOST_POSTGRESQL_DB" -p "$PORT_POSTGRESQL_DB" -U "$POSTGRESQL_USER" >/dev/null 2>&1; do
  sleep 2
done

echo "PostgreSQL disponible. Iniciando aplicación Java..."
exec java -jar app.jar
