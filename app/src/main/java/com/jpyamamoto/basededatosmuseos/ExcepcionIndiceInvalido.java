package com.jpyamamoto.basededatosmuseos;

public class ExcepcionIndiceInvalido extends IndexOutOfBoundsException {

    public ExcepcionIndiceInvalido() {}

    public ExcepcionIndiceInvalido(String mensaje) {
        super(mensaje);
    }
}
