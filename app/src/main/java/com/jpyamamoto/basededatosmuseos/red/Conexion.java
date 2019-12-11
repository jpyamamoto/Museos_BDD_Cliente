package com.jpyamamoto.basededatosmuseos.red;

import com.jpyamamoto.basededatosmuseos.BaseDeDatos;
import com.jpyamamoto.basededatosmuseos.ExcepcionLineaInvalida;
import com.jpyamamoto.basededatosmuseos.Lista;
import com.jpyamamoto.basededatosmuseos.Registro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Conexion<R extends Registro<R, ?>> {

    private static int contadorSerial;

    private BufferedReader in;
    private BufferedWriter out;
    private BaseDeDatos<R, ?> bdd;
    private Lista<EscuchaConexion<R>> escuchas;
    private Socket enchufe;
    private boolean activa;
    private int serial;

    public Conexion(BaseDeDatos<R, ?> bdd, Socket enchufe) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(enchufe.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(enchufe.getOutputStream()));
        this.bdd = bdd;
        this.escuchas = new Lista<EscuchaConexion<R>>();
        this.enchufe = enchufe;
        this.activa = true;
        this.serial = ++contadorSerial;
    }

    public void recibeMensajes() {
        try {
            String linea;
            while((linea = in.readLine()) != null) {
                Mensaje mensaje = Mensaje.getMensaje(linea);
                notificaEscuchas(mensaje);
            }
            activa = false;
        } catch (IOException ioe) {
            if (activa)
                notificaEscuchas(Mensaje.INVALIDO);
        }
        notificaEscuchas(Mensaje.DESCONECTAR);
    }

    public void recibeBaseDeDatos() throws IOException {
        bdd.carga(in);
    }

    public void enviaBaseDeDatos() throws IOException {
        bdd.guarda(out);
        out.newLine();
        out.flush();
    }

    public R recibeRegistro() throws IOException {
        String linea = in.readLine();
        R registro = bdd.creaRegistro();
        try {
            registro.deLinea(linea);
        } catch (ExcepcionLineaInvalida e) {
            throw new IOException("Se recibió línea inválida.");
        }
        return registro;
    }

    public void enviaRegistro(R registro) throws IOException {
        out.write(registro.aLinea());
        //out.newLine();
        out.flush();
    }

    public void enviaMensaje(Mensaje mensaje) throws IOException {
        out.write(mensaje.toString());
        out.newLine();
        out.flush();
    }

    public int getSerial() {
        return serial;
    }

    public void desconecta() {
        activa = false;
        try {
            enchufe.close();
        } catch (IOException ioe) {
            // Ignoramos errores al cerrar enchufe.
        }
    }

    public boolean isActiva() {
        return activa;
    }

    public void agregaEscucha(EscuchaConexion<R> escucha) {
        escuchas.agregaFinal(escucha);
    }

    private void notificaEscuchas(Mensaje mensaje) {
        for (EscuchaConexion<R> escucha : escuchas)
            escucha.mensajeRecibido(this, mensaje);
    }
}
