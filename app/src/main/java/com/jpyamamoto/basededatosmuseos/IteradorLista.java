package com.jpyamamoto.basededatosmuseos;

import java.util.Iterator;

public interface IteradorLista<T> extends Iterator<T> {
    public boolean hasPrevious();

    public T previous();

    public void start();

    public void end();
}
