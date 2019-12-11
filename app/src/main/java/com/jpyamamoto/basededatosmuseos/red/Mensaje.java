package com.jpyamamoto.basededatosmuseos.red;

public enum Mensaje {
    BASE_DE_DATOS,
    REGISTRO_AGREGADO,
    REGISTRO_ELIMINADO,
    REGISTRO_MODIFICADO,
    DESCONECTAR,
    DETENER_SERVICIO,
    ECO,
    INVALIDO;

    private static final String PREFIJO = "|=MENSAJE:";

    public static Mensaje getMensaje(String mensaje) {
        if (!mensaje.startsWith(PREFIJO))
            return INVALIDO;

        String temp = mensaje.replace(PREFIJO, "");

        switch (temp) {
            case "BASE_DE_DATOS":       return BASE_DE_DATOS;
            case "REGISTRO_AGREGADO":   return REGISTRO_AGREGADO;
            case "REGISTRO_ELIMINADO":  return REGISTRO_ELIMINADO;
            case "REGISTRO_MODIFICADO": return REGISTRO_MODIFICADO;
            case "DESCONECTAR":         return DESCONECTAR;
            case "ECO":                 return ECO;
            case "INVALIDO":
            default:                    return INVALIDO;
        }
    }

    public String toString() {
        switch (this) {
            case BASE_DE_DATOS:       return PREFIJO + "BASE_DE_DATOS";
            case REGISTRO_AGREGADO:   return PREFIJO + "REGISTRO_AGREGADO";
            case REGISTRO_ELIMINADO:  return PREFIJO + "REGISTRO_ELIMINADO";
            case REGISTRO_MODIFICADO: return PREFIJO + "REGISTRO_MODIFICADO";
            case DESCONECTAR:         return PREFIJO + "DESCONECTAR";
            case DETENER_SERVICIO:    return PREFIJO + "DETENER_SERVICIO";
            case ECO:                 return PREFIJO + "ECO";
            case INVALIDO:
            default:                  return PREFIJO + "INVALIDO";
        }
    }
}
