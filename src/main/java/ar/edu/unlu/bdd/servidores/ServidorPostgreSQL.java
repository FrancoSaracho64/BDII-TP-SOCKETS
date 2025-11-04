package ar.edu.unlu.bdd.servidores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ServidorPostgreSQL {

    // PostgreSQL (PERSONAL) -> 192.168.3.4:5001

    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/personal", "postgres", "1967");
            System.out.println("✅ Conexión exitosa a PostgreSQL.");
        } catch (SQLException e) {
            System.out.println("❌ Error conectando a la base de datos.");
            e.printStackTrace();
        }
        return conn;
    }




    public static void main(String[] args) {
        ServidorPostgreSQL.conectar();
    }
}
