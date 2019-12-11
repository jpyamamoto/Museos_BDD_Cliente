package com.jpyamamoto.basededatosmuseos.vistas;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

public class EntradaVerificable extends AppCompatEditText {
    Verificador verificador;
    TextView textoError;

    public EntradaVerificable(Context context) {
        super(context);
    }

    public EntradaVerificable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntradaVerificable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVerificador(Verificador verificador) {
        this.verificador = verificador;
    }

    public void setTextoError(TextView textoError) {
        this.textoError = textoError;
    }

    public boolean esValida() {
        boolean valido = verificador.verifica(getText().toString());
        textoError.setVisibility(valido ? INVISIBLE : VISIBLE);
        return valido;
    }
}
