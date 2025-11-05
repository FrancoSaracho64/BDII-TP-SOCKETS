#!/bin/bash
# Script que se ejecuta después de que Firebird inicia para inicializar la base de datos si es necesario

# Esperar un momento para que Firebird esté completamente listo
sleep 5

# Ejecutar el script de inicialización (ya verifica internamente si necesita inicializar)
if [ -f "/docker-entrypoint-initdb.d/init-db.sh" ]; then
  VERBOSE=0 /docker-entrypoint-initdb.d/init-db.sh || true
fi

