package ar.edu.unlu.bdd.helper;

public class Constants {
    private Constants() {
    }

    // HOSTS - Configurables mediante variables de entorno
    public static final String HOST_SWITCH = System.getenv().getOrDefault("HOST_SWITCH", "localhost");
    public static final String HOST_FIREBIRD = System.getenv().getOrDefault("HOST_FIREBIRD", "localhost");
    public static final String HOST_POSTGRESQL = System.getenv().getOrDefault("HOST_POSTGRESQL", "localhost");
    
    // Hosts de las bases de datos (para conexiones JDBC)
    public static final String HOST_FIREBIRD_DB = System.getenv().getOrDefault("HOST_FIREBIRD_DB", "localhost");
    public static final String HOST_POSTGRESQL_DB = System.getenv().getOrDefault("HOST_POSTGRESQL_DB", "localhost");

    // PUERTOS - Configurables mediante variables de entorno
    public static final Integer PORT_SERVER_SWITCH = Integer.parseInt(
            System.getenv().getOrDefault("PORT_SERVER_SWITCH", "5000"));
    public static final Integer PORT_SERVER_POSTGRESQL = Integer.parseInt(
            System.getenv().getOrDefault("PORT_SERVER_POSTGRESQL", "5001"));
    public static final Integer PORT_SERVER_FIREBIRD = Integer.parseInt(
            System.getenv().getOrDefault("PORT_SERVER_FIREBIRD", "5002"));
    
    // Puertos de las bases de datos (para conexiones JDBC)
    public static final Integer PORT_FIREBIRD_DB = Integer.parseInt(
            System.getenv().getOrDefault("PORT_FIREBIRD_DB", "3050"));
    public static final Integer PORT_POSTGRESQL_DB = Integer.parseInt(
            System.getenv().getOrDefault("PORT_POSTGRESQL_DB", "5432"));

    // TABLAS
    public static final String TABLE_PERSONAL = "PERSONAL";
    public static final String TABLE_FACTURACION = "FACTURACION";

    // Credenciales Firebird - Configurables mediante variables de entorno
    public static final String FIREBIRD_USER = System.getenv().getOrDefault("FIREBIRD_USER", "SYSDBA");
    public static final String FIREBIRD_PASS = System.getenv().getOrDefault("FIREBIRD_PASS", "masterkey");
    // Para conexiones remotas, usar solo el nombre del archivo o alias (ej: db_facturacion.fdb)
    // Para conexiones locales, usar la ruta completa (ej: /var/lib/firebird/3.0/data/facturacion)
    public static final String FIREBIRD_DATABASE = System.getenv().getOrDefault("FIREBIRD_DATABASE", 
            "//var/lib/firebird/3.0/data/facturacion");
    
    // URL Firebird construida dinámicamente
    // Para conexiones locales: jdbc:firebirdsql://localhost/path/to/database
    // Para conexiones remotas: jdbc:firebirdsql://host:port/database_name (usar solo el nombre o alias)
    public static final String FIREBIRD_URL = HOST_FIREBIRD_DB.equals("localhost") 
            ? String.format("jdbc:firebirdsql://%s%s", HOST_FIREBIRD_DB, FIREBIRD_DATABASE)
            : String.format("jdbc:firebirdsql://%s:%d/%s", HOST_FIREBIRD_DB, PORT_FIREBIRD_DB, 
                    FIREBIRD_DATABASE.startsWith("/") ? FIREBIRD_DATABASE.substring(FIREBIRD_DATABASE.lastIndexOf("/") + 1) : FIREBIRD_DATABASE);

    // Credenciales PostgreSQL - Configurables mediante variables de entorno
    public static final String POSTGRESQL_USER = System.getenv().getOrDefault("POSTGRESQL_USER", "postgres");
    public static final String POSTGRESQL_PASS = System.getenv().getOrDefault("POSTGRESQL_PASS", "1967");
    public static final String POSTGRESQL_DATABASE = System.getenv().getOrDefault("POSTGRESQL_DATABASE", "personal");
    
    // URL PostgreSQL construida dinámicamente
    public static final String POSTGRESQL_URL = String.format("jdbc:postgresql://%s:%d/%s",
            HOST_POSTGRESQL_DB, PORT_POSTGRESQL_DB, POSTGRESQL_DATABASE);

}
