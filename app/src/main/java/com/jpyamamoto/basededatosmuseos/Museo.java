package com.jpyamamoto.basededatosmuseos;

public class Museo implements Registro<Museo, CampoMuseo> {

    private String nombre;
    private int salas;
    private double costoGeneral;
    private double costoEstudiantes;
    private int visitantes;
    private boolean estacionamiento;

    public Museo(String  nombre,
                 int     salas,
                 double  costoGeneral,
                 double  costoEstudiantes,
                 int     visitantes,
                 boolean estacionamiento) {
        this.nombre           = nombre;
        this.salas            = salas;
        this.costoGeneral     = costoGeneral;
        this.costoEstudiantes = costoEstudiantes;
        this.visitantes       = visitantes;
        this.estacionamiento  = estacionamiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSalas() {
        return salas;
    }

    public void setSalas(int salas) {
        this.salas = salas;
    }

    public double getCostoGeneral() {
        return costoGeneral;
    }

    public void setCostoGeneral(double costoGeneral) {
        this.costoGeneral = costoGeneral;
    }

    public double getCostoEstudiantes() {
        return costoEstudiantes;
    }

    public void setCostoEstudiantes(double costoEstudiantes) {
        this.costoEstudiantes = costoEstudiantes;
    }

    public int getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(int visitantes) {
        this.visitantes = visitantes;
    }

    public boolean getEstacionamiento() {
        return estacionamiento;
    }

    public void setEstacionamiento(boolean estacionamiento) {
        this.estacionamiento = estacionamiento;
    }

    @Override public String toString() {
        return String.format(
            "Nombre            : %s\n" +
            "Salas             : %d\n" +
            "Costo General     : $%.2f\n" +
            "Costo Estudiantes : $%.2f\n" +
            "Visitantes/año    : %,d\n" +
            "Estacionamiento   : %b\n",
            nombre, salas, costoGeneral,
            costoEstudiantes, visitantes, estacionamiento);
    }

    @Override public boolean equals(Object objeto) {
        if (!(objeto instanceof Museo))
            return false;
        Museo museo = (Museo)objeto;

        return museo != null &&
            nombre.equals(museo.nombre) &&
            salas            == museo.salas &&
            costoGeneral     == museo.costoGeneral &&
            costoEstudiantes == museo.costoEstudiantes &&
            visitantes       == museo.visitantes &&
            estacionamiento  == museo.estacionamiento;

    }

    @Override public String aLinea() {
        return String.format("%s\t%d\t%.2f\t%.2f\t%d\t%b\n",
                             nombre, salas, costoGeneral,
                             costoEstudiantes, visitantes, estacionamiento);
    }

    @Override public void deLinea(String linea) {
        if (linea == null || linea.equals(""))
            throw new ExcepcionLineaInvalida("La línea es vacía.");

        String[] partes = linea.trim().split("\t");

        if (partes.length != 6)
            throw new ExcepcionLineaInvalida("La línea no contiene todos los campos requeridos.");

        int salasNuevo, visitantesNuevo;
        double costoGeneralNuevo, costoEstudiantesNuevo;

        try {
            salasNuevo = Integer.valueOf(partes[1]);
            costoGeneralNuevo = Double.valueOf(partes[2]);
            costoEstudiantesNuevo = Double.valueOf(partes[3]);
            visitantesNuevo = Integer.valueOf(partes[4]);
        } catch (NumberFormatException e) {
            throw new ExcepcionLineaInvalida("Los valores de la línea no son válidos.");
        }

        // Es necesario verificar que tengamos registradas las cadenas
        // "true" o "false" explicitamente. De otra manera, cualquier
        // cadena regresa false al hacer la audición a Boolean.
        if (!(partes[5].equals("true") || partes[5].equals("false")))
            throw new ExcepcionLineaInvalida("El valor de estacionamiento es inválido.");

        nombre = partes[0];
        salas = salasNuevo;
        costoGeneral = costoGeneralNuevo;
        costoEstudiantes = costoEstudiantesNuevo;
        visitantes = visitantesNuevo;

        // Boolean.valueOf() no lanza excepción.
        estacionamiento = Boolean.valueOf(partes[5]);
    }

    public void actualiza(Museo museo) {
        if (museo == null)
            throw new IllegalArgumentException("El museo es inválido.");

        nombre = museo.nombre;
        salas = museo.salas;
        costoGeneral = museo.costoGeneral;
        costoEstudiantes = museo.costoEstudiantes;
        visitantes = museo.visitantes;
        estacionamiento = museo.estacionamiento;
    }

    @Override public boolean caza(CampoMuseo campo, Object valor) {
        if (campo == null)
            throw new IllegalArgumentException("El campo es inválido.");

        switch (campo) {
            case NOMBRE:           return cazaNombre(valor);
            case SALAS:            return cazaSalas(valor);
            case COSTOGENERAL:     return cazaCostoGeneral(valor);
            case COSTOESTUDIANTES: return cazaCostoEstudiantes(valor);
            case VISITANTES:       return cazaVisitantes(valor);
            case ESTACIONAMIENTO:  return cazaEstacionamiento(valor);
            default:               return false;
        }
    }

    private boolean cazaNombre(Object valor) {
        if (!(valor instanceof String))
            return false;

        String nombreValor = (String)valor;
        return nombreValor.length() != 0 && nombre.contains(nombreValor);
    }

    private boolean cazaSalas(Object valor) {
        if (!(valor instanceof Integer))
            return false;

        int salasValor = (int)valor;
        return salasValor == salas;
    }

    private boolean cazaCostoGeneral(Object valor) {
        if (!(valor instanceof Double))
            return false;

        double costoGeneralValor = (double)valor;
        return costoGeneralValor >= costoGeneral;
    }

    private boolean cazaCostoEstudiantes(Object valor) {
        if (!(valor instanceof Double))
            return false;

        double costoEstudiantesValor = (double)valor;
        return costoEstudiantesValor >= costoEstudiantes;
    }

    private boolean cazaVisitantes(Object valor) {
        if (!(valor instanceof Integer))
            return false;

        int visitantesValor = (int)valor;
        return visitantesValor <= visitantes;
    }

    private boolean cazaEstacionamiento(Object valor) {
        if (!(valor instanceof Boolean))
            return false;

        boolean estacionamientoValor = (boolean)valor;
        return estacionamientoValor == estacionamiento;
    }
}
