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

public class ServidorPostgreSQL {

    // PostgreSQL (PERSONAL) -> 192.168.3.4:5001

    public static void main(String[] args) throws IOException {
        int puertoPostgreSQL = Constants.PORT_SERVER_POSTGRESQL;
        System.out.println("PostgreSQL escuchando en puerto " + puertoPostgreSQL);

        Connection conn = ServidorPostgreSQL.conectar();

        try (ServerSocket serverSocket = new ServerSocket(puertoPostgreSQL)) {
            while (true) {
                var swtServer = serverSocket.accept();
                // Maneja el servicio a clientes de forma
                new Thread(() -> manejarSwtServer(swtServer, conn)).start();
            }
        }
    }

    private static void manejarSwtServer(Socket swtServer, Connection conn) {
        try (var in = new BufferedReader(new InputStreamReader(swtServer.getInputStream()));
             var out = new PrintWriter(swtServer.getOutputStream(), true)) {

            // Leer las queries del switch
            String querySQL = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("[PostgreSQL] Recibido del switch:\n" + querySQL);

            String respuestaSGBD = consultar(querySQL, conn);

            // Enviar la query que pidió el usuario escribiendo en el socket cliente.
            out.println(respuestaSGBD);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String consultar(String query, Connection conn) {
        X
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
