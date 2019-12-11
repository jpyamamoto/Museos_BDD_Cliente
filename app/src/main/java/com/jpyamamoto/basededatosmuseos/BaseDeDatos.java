package com.jpyamamoto.basededatosmuseos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class BaseDeDatos<R extends Registro<R, C>, C extends Enum> {

    private Lista<R> registros;
    private Lista<EscuchaBaseDeDatos<R>> escuchas;

    public BaseDeDatos() {
        registros = new Lista<R>();
        escuchas  = new Lista<EscuchaBaseDeDatos<R>>();
    }

    public int getNumRegistros() {
        return registros.getLongitud();
    }

    public Lista<R> getRegistros() {
        return registros.copia();
    }

    public void agregaRegistro(R registro) {
        registros.agregaFinal(registro);
        notificaEscuchas(EventoBaseDeDatos.REGISTRO_AGREGADO, registro, null);
    }

    public void eliminaRegistro(R registro) {
        registros.elimina(registro);
        notificaEscuchas(EventoBaseDeDatos.REGISTRO_ELIMINADO, registro, null);
    }

    public void modificaRegistro(R registro1, R registro2) {
        if (registro1 == null || registro2 == null)
            throw new IllegalArgumentException();

        for (R reg : registros)
            if (reg.equals(registro1)) {
                reg.actualiza(registro2);
                notificaEscuchas(EventoBaseDeDatos.REGISTRO_MODIFICADO,
                                 registro1, registro2);
                break;
            }
    }

    public void limpia() {
        registros.limpia();
        notificaEscuchas(EventoBaseDeDatos.BASE_LIMPIADA, null, null);
    }

    public void guarda(BufferedWriter out) throws IOException {
        for (R reg : registros)
            out.write(reg.aLinea());
    }

    public void carga(BufferedReader in) throws IOException {
        limpia();

        String linea;
        while ((linea = in.readLine()) != null && !linea.trim().equals("")) {
            R reg = creaRegistro();
            try {
                reg.deLinea(linea);
            } catch (ExcepcionLineaInvalida e) {
                throw new IOException("El archivo contiene una línea inválida.");
            }
            agregaRegistro(reg);
        }
    }

    public Lista<R> buscaRegistros(C campo, Object valor) {
        if (campo == null)
            throw new IllegalArgumentException("El campo es inválido.");

        Lista<R> lista = new Lista<R>();

        for (R reg : registros) {
            if (reg.caza(campo, valor))
                lista.agregaFinal(reg);
        }

        return lista;
    }

    public abstract R creaRegistro();

    public void agregaEscucha(EscuchaBaseDeDatos<R> escucha) {
        escuchas.agregaFinal(escucha);
    }

    public void eliminaEscucha(EscuchaBaseDeDatos<R> escucha) {
        escuchas.elimina(escucha);
    }

    private void notificaEscuchas(EventoBaseDeDatos evento, R reg1, R reg2) {
        for (EscuchaBaseDeDatos<R> escucha : escuchas)
            escucha.baseDeDatosModificada(evento, reg1, reg2);
    }
}
