package com.jpyamamoto.basededatosmuseos.red;

import com.jpyamamoto.basededatosmuseos.Registro;

@FunctionalInterface
public interface EscuchaCliente<R extends Registro<R, ?>> {
    public void mensajeRecibido(Conexion<R> conexion, Mensaje mensaje);
}
