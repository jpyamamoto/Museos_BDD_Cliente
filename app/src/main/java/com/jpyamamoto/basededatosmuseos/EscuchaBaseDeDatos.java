package com.jpyamamoto.basededatosmuseos;

@FunctionalInterface
public interface EscuchaBaseDeDatos<T extends Registro> {

    public void baseDeDatosModificada(EventoBaseDeDatos evento,
                                      T registro1, T registro2);
}
