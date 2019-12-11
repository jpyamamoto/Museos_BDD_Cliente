package com.jpyamamoto.basededatosmuseos.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jpyamamoto.basededatosmuseos.R;

public class MainActivity extends AppCompatActivity {

    private String servidor;
    private int puerto;

    private Button boton;

    private EntradaVerificable entradaServidor;
    private EntradaVerificable entradaPuerto;


    private class VerificadorEntrada implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean result = verificaConexion();
            boton.setEnabled(result);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boton = (Button) findViewById(R.id.botonAceptar);

        entradaServidor = (EntradaVerificable) findViewById(R.id.servidor);
        TextView tooltipServidor = (TextView) findViewById(R.id.tooltipServidor);
        entradaServidor.setVerificador(s -> verificaServidor(s));
        entradaServidor.setTextoError(tooltipServidor);
        entradaServidor.addTextChangedListener(new VerificadorEntrada());

        entradaPuerto = (EntradaVerificable) findViewById(R.id.puerto);
        TextView tooltipPuerto = (TextView) findViewById(R.id.tooltipPuerto);
        entradaPuerto.setVerificador(p -> verificaPuerto(p));
        entradaPuerto.setTextoError(tooltipPuerto);
        entradaPuerto.addTextChangedListener(new VerificadorEntrada());
    }

    private boolean verificaConexion() {
        boolean s = entradaServidor.esValida();
        boolean p = entradaPuerto.esValida();

        return s && p;
    }

    private boolean verificaServidor(String servidor) {
        if (servidor == null || servidor.isEmpty())
            return false;

        this.servidor = servidor;
        return true;
    }

    private boolean verificaPuerto(String puerto) {
        if (puerto == null || puerto.isEmpty())
            return false;

        try {
            this.puerto = Integer.parseInt(puerto);
        } catch (NumberFormatException nfe) {
            return false;
        }

        if (this.puerto < 1025 || this.puerto > 65535)
            return false;

        return true;
    }

    public void abrirTabla(View view) {
        Intent intent = new Intent(getApplicationContext(), ActividadTabla.class);
        intent.putExtra("servidor", servidor);
        intent.putExtra("puerto", puerto);

        startActivity(intent);
    }
}
