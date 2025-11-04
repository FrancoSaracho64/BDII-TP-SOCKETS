package ar.edu.unlu.bdd.servidores;

import ar.edu.unlu.bdd.helper.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// Direcciones IP
//  - Proceso Cliente -> 192.168.3.1
//  - Proceso Servidor Switch -> 192.168.3.2
//  - Proceso Servidor Firebird (FACTURACIÓN) -> 192.168.3.3
//  - Proceso Servidor PostgreSQL (PERSONAL) -> 192.168.3.4

// Tareas del servidor switch
//  - (on start) Escuchar en algún puerto para que los clientes puedan abrir sockets hacia nosotros.
//  - Se deben poder atender múltiples clientes concurrentemente
//  - Leer la queries de los sockets clientes.
//  - Interpretar la query dado el formato solicitado.
//  - Abrir un socket contra el SGBD adecuado (personal/facturacion).
//  - Enviar la query que pidió el usuario escribiendo en el socket servidor.
//  - Leer la respuesta del SGBD del socket servidor.
//  - Formatear la respuesta en el formato solicitado.
//  - Enviar la query que pidió el usuario escribiendo en el socket cliente.

public class ServidorSwitch {
    public static void main(String[] args) throws IOException {
        // Escuchar en algún puerto para que los clientes puedan abrir sockets hacia nosotros.
        int puertoSwitch = Constants.PORT_SERVER_SWITCH;
        System.out.println("Switch escuchando en puerto " + puertoSwitch);

        try (ServerSocket serverSocket = new ServerSocket(puertoSwitch)) {
            while (true) {
                var cliente = serverSocket.accept();
                // Maneja el servicio a clientes de forma
                new Thread(() -> manejarCliente(cliente)).start();
            }
        }
    }

    private static void manejarCliente(Socket cliente) {
        try (var in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
             var out = new PrintWriter(cliente.getOutputStream(), true)) {

            // Leer la queries de los sockets clientes
            String xml = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("[Switch] Recibido del cliente:\n" + xml);

            String database = extraerEntre(xml, "<database>", "</database>");
            int puertoDestino = seleccionarDestino(database);

            String query = extraerEntre(xml, "<sql>", "</sql>");
            String respuestaSGBD = reenviar(query, puertoDestino);

            String respuesta = formatearRespuesta(respuestaSGBD);

            // Enviar la query que pidió el usuario escribiendo en el socket cliente.
            out.println(respuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Formatear la respuesta en el formato solicitado.
    private static String formatearRespuesta(String respuestaSGBD) {
        StringBuilder xml = new StringBuilder();
        xml.append("<query>\n");

        // Ejemplo de formato esperado:
        String[] lineas = respuestaSGBD.split("\n");
        if (lineas.length == 0) return "<error>Respuesta vacía</error>";

        String[] columnas = lineas[0].split(",");
        xml.append("  <cols>\n");
        for (int i = 0; i < columnas.length; i++) {
            xml.append("    <colname").append(i + 1).append(">")
                    .append(columnas[i].trim())
                    .append("</colname").append(i + 1).append(">\n");
        }
        xml.append("  </cols>\n");

        xml.append("  <rows>\n");
        for (int fila = 1; fila < lineas.length; fila++) {
            String[] valores = lineas[fila].split(",");
            xml.append("    <row").append(fila).append(">\n");
            for (int i = 0; i < valores.length; i++) {
                xml.append("      <col").append(i + 1).append(">")
                        .append(valores[i].trim())
                        .append("</col").append(i + 1).append(">\n");
            }
            xml.append("    </row").append(fila).append(">\n");
        }
        xml.append("  </rows>\n");
        xml.append("</query>");
        return xml.toString();
    }

    private static int seleccionarDestino(String database) {
        return switch (database.toUpperCase()) {
            case "FACTURACION" -> Constants.PORT_SERVER_FIREBIRD;
            case "PERSONAL" -> Constants.PORT_SERVER_POSTGRESQL;
            default -> Constants.PORT_SERVER_FIREBIRD;
        };
    }

    // Abrir un socket contra el SGBD adecuado
    private static String reenviar(String xml, int puerto) {
        try (var socket = new Socket("localhost", puerto);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Enviar la query que pidió el usuario escribiendo en el socket servidor.
            out.println(xml);
            String respuesta = in.lines().reduce("", (a, b) -> a + b + "\n");

            // Leer la respuesta del SGBD del socket servidor.
            System.out.println("[Switch] Respuesta desde puerto " + puerto + ":\n" + respuesta);
            return respuesta;

        } catch (IOException e) {
            return "<error>No se pudo conectar al servidor destino</error>";
        }
    }

    // Interpretar la query dado el formato solicitado.
    private static String extraerEntre(String texto, String start, String end) {
        int i = texto.indexOf(start);
        int j = texto.indexOf(end);
        if (i == -1 || j == -1) return "";
        return texto.substring(i + start.length(), j).trim();
    }
}