package ar.edu.unlu.bdd.clientes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteXML {
    public static void main(String[] args) throws IOException {
        int puertoSwitch = 5000;

        String xmlQuery = """
                <query>
                    <database>facturacion</database>
                    <sql>SELECT * FROM factura WHERE fecha > '2025-01-01'</sql>
                </query>
                """;

        System.out.println("Enviando al switch:\n" + xmlQuery);
        try (var socket = new Socket("localhost", puertoSwitch);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(xmlQuery);
            out.flush();
            socket.shutdownOutput();

            String respuesta = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("Respuesta recibida:\n" + respuesta);
        }
    }
}
