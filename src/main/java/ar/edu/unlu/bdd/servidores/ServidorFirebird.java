package ar.edu.unlu.bdd.servidores;

import ar.edu.unlu.bdd.helper.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

import static java.lang.System.out;

public class ServidorFirebird {
    public static void main(String[] args) throws IOException {
        int puertoServerFirebird = Constants.PORT_SERVER_FIREBIRD;
        out.println("SERVER FIREBIRD escuchando en puerto " + puertoServerFirebird);

        try (ServerSocket serverSocket = new ServerSocket(puertoServerFirebird)) {
            while (true) {
                var cliente = serverSocket.accept();
                new Thread(() -> manejarCliente(cliente)).start();
            }
        }
    }

    private static void manejarCliente(Socket cliente) {
        try (var in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             var out = new PrintWriter(cliente.getOutputStream(), true)) {

            // Extraer SQL
            String sql = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("[ServidorFirebird] SQL recibido:\n" + sql);

            if (sql.isEmpty()) {
                out.println("<error>No se ha recibido una Query</error>");
                return;
            }

            // Ejecutar SQL y generar respuesta
            String respuestaXML = ejecutarConsultaYGenerarXML(sql.trim());
            out.println(respuestaXML);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<error>" + e.getMessage() + "</error>");
        }
    }

    private static String ejecutarConsultaYGenerarXML(String sql) {
        StringBuilder resultado = new StringBuilder();
        try {
            // Registrar explícitamente el driver de Firebird para asegurar que esté disponible
            Class.forName("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de Firebird: " + e.getMessage());
        }

        System.out.println("[ServidorFirebird] Conectando a: " + Constants.FIREBIRD_URL);
        try (Connection conn = DriverManager.getConnection(
                Constants.FIREBIRD_URL, Constants.FIREBIRD_USER, Constants.FIREBIRD_PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            //int columnCount = meta.getColumnCount();
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

        /*
            // Armar XML de columnas
            StringBuilder xml = new StringBuilder();
            xml.append("<query>\n");

            xml.append("<cols>\n");
            for (int i = 1; i <= columnCount; i++) {
                xml.append("<colname").append(i).append(">")
                        .append(meta.getColumnName(i))
                        .append("</colname").append(i).append(">\n");
            }
            xml.append("</cols>\n");

            // Armar XML de filas
            xml.append("<rows>\n");
            int rowNum = 1;
            while (rs.next()) {
                xml.append("<row").append(rowNum).append(">\n");
                for (int i = 1; i <= columnCount; i++) {
                    Object valor = rs.getObject(i);
                    xml.append("<col").append(i).append(">")
                            .append(valor != null ? escapeXml(valor.toString()) : "")
                            .append("</col").append(i).append(">\n");
                }
                xml.append("</row").append(rowNum).append(">\n");
                rowNum++;
            }
            xml.append("</rows>\n");
            xml.append("</query>");

            return xml.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "<error>" + e.getMessage() + "</error>";
        }*/
    }

    /*private static String escapeXml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }*/
}
