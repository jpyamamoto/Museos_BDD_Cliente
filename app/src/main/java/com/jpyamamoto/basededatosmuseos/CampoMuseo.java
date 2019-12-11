package com.jpyamamoto.basededatosmuseos;

public enum CampoMuseo {

    NOMBRE,
    SALAS,
    COSTOGENERAL,
    COSTOESTUDIANTES,
    VISITANTES,
    ESTACIONAMIENTO;

    public static CampoMuseo getCampo(String mensaje) {
        switch (mensaje) {
            case "Nombre":            return NOMBRE;
            case "Salas":             return SALAS;
            case "Costo General":     return COSTOGENERAL;
            case "Costo Estudiantes": return COSTOESTUDIANTES;
            case "Visitantes/año":    return VISITANTES;
            case "Estacionamiento":   return ESTACIONAMIENTO;
            default:                  return null;
        }
    }
    @Override public String toString() {
        switch (this) {
            case NOMBRE:           return "Nombre";
            case SALAS:            return "Salas";
            case COSTOGENERAL:     return "Costo General";
            case COSTOESTUDIANTES: return "Costo Estudiantes";
            case VISITANTES:       return "Visitantes/año";
            case ESTACIONAMIENTO:  return "Estacionamiento";
            default:               return null;
            // El caso default solo sirve para que Java compile.
            // El switch nunca alcanzará este caso.
        }
    }
}

