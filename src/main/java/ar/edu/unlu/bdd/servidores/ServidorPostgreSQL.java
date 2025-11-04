package ar.edu.unlu.bdd.servidores;

import ar.edu.unlu.bdd.helper.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.System.out;

public class ServidorPostgreSQL {

    public static void main(String[] args) throws IOException {
        int puertoServerPostgreSQL = Constants.PORT_SERVER_POSTGRESQL;
        System.out.println("SERVER POSTGRESQL escuchando en puerto " + puertoServerPostgreSQL);

        // ✅ Crear conexión una sola vez
        Connection conn = conectar();

        try (ServerSocket serverSocket = new ServerSocket(puertoServerPostgreSQL)) {
            while (true) {
                var cliente = serverSocket.accept();
                new Thread(() -> manejarSwtServer(cliente, conn)).start();
            }
        }
    }

    private static void manejarSwtServer(Socket cliente, Connection conn) {
        try (var in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             var out = new PrintWriter(cliente.getOutputStream(), true)) {

            // Leer SQL correctamente (no usar in.lines())
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = in.readLine()) != null && !linea.isEmpty()) {
                sb.append(linea).append("\n");
            }
            String sql = sb.toString();

            System.out.println("[ServidorPostgreSQL] SQL recibido:\n" + sql);

            if (sql.isEmpty()) {
                out.println("<error>No se ha recibido una Query</error>");
                return;
            }

            String respuesta = consultar(sql, conn);

            out.println(respuesta);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String consultar(String query, Connection conn) {
        StringBuilder resultado = new StringBuilder();

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {

            var meta = rs.getMetaData();
            int columnas = meta.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnas; i++) {
                    resultado.append(meta.getColumnName(i))
                            .append("=")
                            .append(rs.getString(i));
                    if (i < columnas) resultado.append(", ");
                }
                resultado.append("\n");
            }

        } catch (Exception e) {
            return "<error>" + e.getMessage() + "</error>";
        }

        return resultado.toString();
    }




    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/personal", "postgres", "1967");
            System.out.println("Conexión exitosa a PostgreSQL.");
        } catch (SQLException e) {
            System.out.println("Error conectando a la base de datos.");
            e.printStackTrace();
        }
        return conn;
    }

}
