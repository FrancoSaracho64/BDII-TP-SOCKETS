#!/bin/bash
set -e

DB_PATH="/firebird/data/db_facturacion.fdb"
DB_USER="SYSDBA"
DB_PASSWORD="masterkey"
SCRIPT_DIR="/docker-entrypoint-initdb.d"
SQL_SCRIPT="$SCRIPT_DIR/create_db.sql"
ISQL="/usr/local/firebird/bin/isql-fb"

# Si isql-fb no existe, usar isql
if [ ! -f "$ISQL" ]; then
    ISQL="/usr/local/firebird/bin/isql"
fi

# Solo mostrar mensajes si no estamos en modo silencioso
VERBOSE=${VERBOSE:-1}

if [ "$VERBOSE" = "1" ]; then
  echo "=== Script de inicialización de Firebird ==="
fi

# Esperar a que Firebird esté listo
echo "Esperando a que Firebird esté listo..."
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
  if $ISQL -z > /dev/null 2>&1; then
    break
  fi
  attempt=$((attempt + 1))
  sleep 2
done

if [ $attempt -eq $max_attempts ]; then
  echo "Error: Firebird no está disponible después de $max_attempts intentos"
  exit 1
fi

if [ "$VERBOSE" = "1" ]; then
  echo "Firebird está listo. Esperando a que la base de datos esté creada..."
fi

# Esperar a que la base de datos exista
max_attempts=30
attempt=0
while [ $attempt -lt $max_attempts ]; do
  if [ -f "$DB_PATH" ]; then
    break
  fi
  attempt=$((attempt + 1))
  sleep 2
done

if [ $attempt -eq $max_attempts ]; then
  if [ "$VERBOSE" = "1" ]; then
    echo "Error: La base de datos no se creó después de $max_attempts intentos"
  fi
  exit 1
fi

if [ "$VERBOSE" = "1" ]; then
  echo "Base de datos encontrada. Esperando un momento adicional para asegurar que esté lista..."
fi
sleep 3

if [ "$VERBOSE" = "1" ]; then
  echo "Verificando si necesita inicialización..."
fi

if [ -f "$SQL_SCRIPT" ]; then
  # Verificar si la tabla PRODUCTO existe consultando RDB$RELATIONS
  TABLE_EXISTS=$($ISQL -user "$DB_USER" -password "$DB_PASSWORD" -database "localhost:$DB_PATH" -q <<'EOF' 2>&1 | grep -c "PRODUCTO" || echo "0"
SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$RELATION_NAME = 'PRODUCTO';
EOF
)
  
  if [ "$TABLE_EXISTS" = "0" ]; then
    echo "Tablas no encontradas. Ejecutando script de inicialización..."
    echo "Ejecutando: $ISQL -user $DB_USER -password $DB_PASSWORD -database localhost:$DB_PATH -i $SQL_SCRIPT"
    
    # Ejecutar el script y capturar la salida
    OUTPUT=$($ISQL -user "$DB_USER" -password "$DB_PASSWORD" -database "localhost:$DB_PATH" -i "$SQL_SCRIPT" 2>&1)
    EXIT_CODE=$?
    
    # Mostrar solo errores críticos (ignorar errores de "ya existe" si las tablas se crearon)
    echo "$OUTPUT" | grep -v "already exists\|Sequence.*already exists" || true
    
    # Verificar que las tablas se crearon correctamente
    TABLE_EXISTS_AFTER=$($ISQL -user "$DB_USER" -password "$DB_PASSWORD" -database "localhost:$DB_PATH" -q <<'EOF' 2>&1 | grep -c "PRODUCTO" || echo "0"
SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$RELATION_NAME = 'PRODUCTO';
EOF
)
    
    if [ "$TABLE_EXISTS_AFTER" != "0" ]; then
      echo "✓ Tablas creadas exitosamente."
      
      # Verificar que los datos se insertaron correctamente
      echo "Verificando inserción de datos..."
      PRODUCTO_COUNT=$($ISQL -user "$DB_USER" -password "$DB_PASSWORD" -database "localhost:$DB_PATH" -q <<'EOF' 2>&1 | tail -1 | tr -d ' '
SELECT COUNT(*) FROM PRODUCTO;
EOF
)
      echo "✓ Registros en PRODUCTO: $PRODUCTO_COUNT"
      
      if [ "$PRODUCTO_COUNT" -gt "0" ]; then
        echo "✓ Script de inicialización completado exitosamente."
      else
        echo "⚠ Advertencia: No se encontraron registros después de la inicialización."
      fi
    else
      echo "✗ Error: Las tablas no se crearon correctamente después de ejecutar el script."
      exit 1
    fi
  else
    echo "✓ Las tablas ya existen. Saltando inicialización."
  fi
else
  echo "✗ Error: No se encontró el archivo SQL en $SQL_SCRIPT"
  exit 1
fi

echo "=== Inicialización completada ==="
