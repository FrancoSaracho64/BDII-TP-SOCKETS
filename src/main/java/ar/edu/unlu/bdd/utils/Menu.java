package ar.edu.unlu.bdd.utils;

import ar.edu.unlu.bdd.helper.Constants;

public class Menu {
    private String tabla;
    private String query;

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Menu() {
    }

    public void solicitarEntradas() {
        int opc;
        do {
            System.out.println("\n----------------------------------------------");
            System.out.println("Aplicación '''Cliente'''");
            System.out.println("Seleccione la tabla con la que desea operar:");
            System.out.println("1:   -->  FACTURACION");
            System.out.println("2:   -->  PERSONAL");
            System.out.println("3:   <<<  Salir del programa.");

            opc = CFZValidatorUtils.solicitarNumeroPorTeclado("Ingrese su opcion: ");

            switch (opc) {
                case 1:
                    this.tabla = Constants.TABLE_FACTURACION;
                    this.query = solicitarQuery();
                    opc = 3;
                    break;
                case 2:
                    this.tabla = Constants.TABLE_PERSONAL;
                    this.query = solicitarQuery();
                    opc = 3;
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción incorrecta.");
            }
        } while (opc != 3);
    }


    public String solicitarQuery() {
        System.out.println("\n----------------------------------------------");
        System.out.println("Introduzca la query a ejecutar");
        return CFZValidatorUtils.solicitarEntradaPorTeclado(">>>  ");
    }
}
