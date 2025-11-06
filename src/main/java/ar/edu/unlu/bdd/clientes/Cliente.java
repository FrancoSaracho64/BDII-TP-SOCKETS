package ar.edu.unlu.bdd.clientes;

import ar.edu.unlu.bdd.helper.Constants;
import ar.edu.unlu.bdd.utils.CFZValidatorUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciar();
    }

    public void iniciar() {
        boolean continuar = true;

        while (continuar) {
            int opcion = mostrarMenuPrincipal();

            switch (opcion) {
                case 1 -> procesarConsulta(Constants.TABLE_FACTURACION);
                case 2 -> procesarConsulta(Constants.TABLE_PERSONAL);
                case 3 -> {
                    System.out.println("Saliendo del programa...");
                    continuar = false;
                }
                default -> System.out.println("Opción incorrecta. Intente nuevamente.");
            }
        }
    }

    private int mostrarMenuPrincipal() {
        System.out.println("\n----------------------------------------------");
        System.out.println("Aplicación >> Cliente <<");
        System.out.println("Seleccione la tabla con la que desea operar:");
        System.out.println("1: FACTURACION");
        System.out.println("2: PERSONAL");
        System.out.println("3: Salir del programa");
        return CFZValidatorUtils.solicitarNumeroPorTeclado("Ingrese su opción: ");
    }

    private void procesarConsulta(String tabla) {
        String query = solicitarQuery();

        String xml = """
                <query>
                    <database>%s</database>
                    <sql>%s</sql>
                </query>
                """.formatted(tabla, query);

        System.out.println("\nEnviando al switch:\n" + xml);

        try {
            enviarQueryAlServidor(xml);
        } catch (IOException e) {
            System.err.println("Error al comunicarse con el servidor: " + e.getMessage());
        }
    }

    private String solicitarQuery() {
        System.out.println("\n----------------------------------------------");
        System.out.println("Introduzca la query a ejecutar:");
        return CFZValidatorUtils.solicitarEntradaPorTeclado(">>> ");
    }

    private void enviarQueryAlServidor(String xml) throws IOException {
        try (Socket socket = new Socket(Constants.HOST_SWITCH, Constants.PORT_SERVER_SWITCH);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(xml);
            out.flush();
            socket.shutdownOutput();

            System.out.println("\nEsperando respuesta del servidor...\n");
            String respuesta = in.lines().reduce("", (a, b) -> a + b + "\n");
            System.out.println("Respuesta recibida:\n" + respuesta);
        }
    }
}
