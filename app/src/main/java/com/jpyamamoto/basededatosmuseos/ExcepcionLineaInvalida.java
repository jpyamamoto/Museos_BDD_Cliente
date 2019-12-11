package com.jpyamamoto.basededatosmuseos;

public class ExcepcionLineaInvalida extends IllegalArgumentException {

    public ExcepcionLineaInvalida() {}

    public ExcepcionLineaInvalida(String mensaje) {
        super(mensaje);
    }
}
