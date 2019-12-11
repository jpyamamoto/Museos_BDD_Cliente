package com.jpyamamoto.basededatosmuseos.vistas;

import com.jpyamamoto.basededatosmuseos.Lista;
import com.jpyamamoto.basededatosmuseos.Museo;

@FunctionalInterface
public interface EscuchaSeleccion {
    public void cambioSeleccion(Lista<Museo> elementosSeleccionados);
}
