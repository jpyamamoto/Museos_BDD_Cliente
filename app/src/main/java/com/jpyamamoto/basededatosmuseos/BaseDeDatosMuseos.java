package com.jpyamamoto.basededatosmuseos;

public class BaseDeDatosMuseos
    extends BaseDeDatos<Museo, CampoMuseo> {

    @Override public Museo creaRegistro() {
        return new Museo(null, 0, 0.0, 0.0, 0, false);
    }
}

