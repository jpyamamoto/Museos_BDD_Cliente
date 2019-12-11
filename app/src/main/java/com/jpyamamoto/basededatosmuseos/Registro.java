package com.jpyamamoto.basededatosmuseos;

public interface Registro<R, C extends Enum> {
    public String aLinea();

    public void deLinea(String linea);

    public void actualiza(R registro);

    public boolean caza(C campo, Object valor);
}
