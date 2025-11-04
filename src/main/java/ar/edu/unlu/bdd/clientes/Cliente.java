package ar.edu.unlu.bdd.clientes;

import ar.edu.unlu.bdd.helper.Constants;
import ar.edu.unlu.bdd.utils.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        int puertoSwitch = Constants.PORT_SERVER_SWITCH;
        Menu menu = new Menu();
        menu.solicitarEntradas();

        String tabla = menu.getTabla();
        String query = menu.getQuery();

        String xml = """
                <query>
                    <database>%s</database>
                    <sql>%s</sql>
                </query>
                """.formatted(tabla, query);

        System.out.println("Enviando al switch:\n" + xml);
        try (var socket = new Socket("localhost", puertoSwitch);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(xml);
            out.flush();
            socket.shutdownOutput();

            String respuesta = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("Respuesta recibida:\n" + respuesta);
        }
    }
}
