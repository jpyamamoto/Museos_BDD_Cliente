package com.jpyamamoto.basededatosmuseos;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Lista<T> implements Iterable<T> {

    private class Nodo {
        private T elemento;
        private Nodo anterior;
        private Nodo siguiente;

        private Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    private class Iterador implements IteradorLista<T> {
        private Nodo anterior;
        private Nodo siguiente;

        private Iterador() {
            anterior = null;
            siguiente = cabeza;
        }

        @Override public boolean hasNext() {
            return siguiente != null;
        }

        @Override public T next() {
            if (siguiente == null)
                throw new NoSuchElementException("No hay elemento siguiente.");

            T temp = siguiente.elemento;
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return temp;
        }

        @Override public boolean hasPrevious() {
            return anterior != null;
        }

        @Override public T previous() {
            if (anterior == null)
                throw new NoSuchElementException("No hay elemento anterior.");

            T temp = anterior.elemento;
            siguiente = anterior;
            anterior = anterior.anterior;
            return temp;
        }

        @Override public void start() {
            anterior = null;
            siguiente = cabeza;
        }

        @Override public void end() {
            anterior = rabo;
            siguiente = null;
        }
    }

    private Nodo cabeza;
    private Nodo rabo;
    private int longitud;

    public int getLongitud() {
        return longitud;
    }

    public boolean esVacia() {
        return longitud == 0;
    }

    public void agregaFinal(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento es inválido.");

        Nodo nodo = new Nodo(elemento);

        if (rabo == null) {
            cabeza = rabo = nodo;
        } else {
            rabo.siguiente = nodo;
            nodo.anterior = rabo;
            rabo = nodo;
        }

        longitud++;
    }

    public void agregaInicio(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento es inválido.");

        Nodo nodo = new Nodo(elemento);

        if (cabeza == null) {
            cabeza = rabo = nodo;
        } else {
            cabeza.anterior = nodo;
            nodo.siguiente = cabeza;
            cabeza = nodo;
        }

        longitud++;
    }

    public void inserta(int i, T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El elemento es inválido.");

        if (i <= 0) {
            agregaInicio(elemento);
            return;
        }

        if (i >= longitud) {
            agregaFinal(elemento);
            return;
        }

        Nodo nodo = new Nodo(elemento);
        Nodo nodoTemp = getNodo(i);

        nodo.anterior = nodoTemp.anterior;
        nodo.siguiente = nodoTemp;
        nodoTemp.anterior.siguiente = nodo;
        nodoTemp.anterior = nodo;

        longitud++;
    }

    public void elimina(T elemento) {
        Nodo nodo = cabeza;

        while (nodo != null) {
            if (nodo.elemento.equals(elemento))
                break;

            nodo = nodo.siguiente;
        }

        if (nodo == null)
            return;

        if (nodo.anterior == null)
            cabeza = nodo.siguiente;
        else
            nodo.anterior.siguiente = nodo.siguiente;

        if (nodo.siguiente == null)
            rabo = nodo.anterior;
        else
            nodo.siguiente.anterior = nodo.anterior;

        longitud--;
    }

    public T eliminaPrimero() {
        if (longitud == 0)
            throw new NoSuchElementException("No hay primer elemento para eliminar.");

        T temp = cabeza.elemento;
        cabeza = cabeza.siguiente;

        if (cabeza != null)
            cabeza.anterior = null;
        else
            rabo = null;

        longitud--;
        return temp;
    }

    public T eliminaUltimo() {
        if (longitud == 0)
            throw new NoSuchElementException("No hay último elemento para eliminar.");

        T temp = rabo.elemento;
        rabo = rabo.anterior;

        if (rabo != null)
            rabo.siguiente = null;
        else
            cabeza = null;

        longitud--;
        return temp;
    }

    public boolean contiene(T elemento) {
        return indiceDe(elemento) != -1;
    }

    public Lista<T> reversa() {
        Nodo nodo = cabeza;
        Lista<T> nuevaLista = new Lista<T>();

        while (nodo != null) {
            nuevaLista.agregaInicio(nodo.elemento);
            nodo = nodo.siguiente;
        }

        return nuevaLista;
    }

    public Lista<T> copia() {
        Nodo nodo = cabeza;
        Lista<T> nuevaLista = new Lista<T>();

        while (nodo != null) {
            nuevaLista.agregaFinal(nodo.elemento);
            nodo = nodo.siguiente;
        }

        return nuevaLista;
    }

    public void limpia() {
        cabeza = rabo = null;
        longitud = 0;
    }

    public T getPrimero() {
        if (longitud == 0)
            throw new NoSuchElementException("No hay primer elemento.");

        return cabeza.elemento;
    }

    public T getUltimo() {
        if (longitud == 0)
            throw new NoSuchElementException("No hay último elemento.");

        return rabo.elemento;
    }

    public T get(int i) {
        if (i < 0 || i >= longitud)
            throw new ExcepcionIndiceInvalido("El índice es inválido.");

        return getNodo(i).elemento;
    }

    private Nodo getNodo(int i) {
        int indice = 0;
        Nodo nodo = cabeza;

        while (indice++ != i)
            nodo = nodo.siguiente;

        return nodo;
    }

    public int indiceDe(T elemento) {
        int indice = 0;
        Nodo nodo = cabeza;

        while (nodo != null) {
            if (nodo.elemento.equals(elemento))
                return indice;

            indice++;
            nodo = nodo.siguiente;
        }

        return -1;
    }

    @Override public String toString() {
        Nodo nodo = cabeza;
        String texto = "[";

        if (nodo != null) {
            while (nodo.siguiente != null) {
                texto += String.format("%s, ", nodo.elemento);
                nodo = nodo.siguiente;
            }

            texto += String.format("%s", nodo.elemento);
        }

        texto += "]";
        return texto;
    }

    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Lista<T> lista = (Lista<T>)objeto;

        if (longitud != lista.longitud)
            return false;

        Nodo nodo1 = cabeza;
        Nodo nodo2 = lista.cabeza;

        while (nodo1 != null) {
            if (!nodo1.elemento.equals(nodo2.elemento))
                return false;

            nodo1 = nodo1.siguiente;
            nodo2 = nodo2.siguiente;
        }

        return true;
    }

    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    public Lista<T> mergeSort(Comparator<T> comparador) {
        if (longitud <= 1)
            return copia();

        int mitad = longitud / 2;
        Lista<T> primeraMitad = copiaSublista(0, mitad).mergeSort(comparador);
        Lista<T> segundaMitad = copiaSublista(mitad, longitud).mergeSort(comparador);

        Lista<T> resultado = new Lista<T>();
        Nodo nodo1 = primeraMitad.cabeza;
        Nodo nodo2 = segundaMitad.cabeza;

        while (nodo1 != null && nodo2 != null) {
            if (comparador.compare(nodo1.elemento, nodo2.elemento) <= 0) {
                resultado.agregaFinal(nodo1.elemento);
                nodo1 = nodo1.siguiente;
            } else {
                resultado.agregaFinal(nodo2.elemento);
                nodo2 = nodo2.siguiente;
            }
        }

        while (nodo1 != null) {
            resultado.agregaFinal(nodo1.elemento);
            nodo1 = nodo1.siguiente;
        }

        while (nodo2 != null) {
            resultado.agregaFinal(nodo2.elemento);
            nodo2 = nodo2.siguiente;
        }

        return resultado;
    }

    private Lista<T> copiaSublista(int i, int j) {
        Lista<T> nuevaLista = new Lista<T>();
        Nodo nodo = getNodo(i);

        while (nodo != null && i++ < j) {
            nuevaLista.agregaFinal(nodo.elemento);
            nodo = nodo.siguiente;
        }

        return nuevaLista;
    }

    public static <T extends Comparable<T>>
    Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        Nodo nodo = cabeza;
        int diferencia = -1;

        while (nodo != null &&
                (diferencia = comparador.compare(nodo.elemento, elemento)) < 0)
            nodo = nodo.siguiente;

        return nodo != null && diferencia == 0;
    }

    public static <T extends Comparable<T>>
    boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
