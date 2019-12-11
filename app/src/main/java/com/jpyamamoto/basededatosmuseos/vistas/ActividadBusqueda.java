package com.jpyamamoto.basededatosmuseos.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jpyamamoto.basededatosmuseos.CampoMuseo;
import com.jpyamamoto.basededatosmuseos.R;

public class ActividadBusqueda extends AppCompatActivity {

    private EditText entradaTexto;
    private EditText entradaNumero;
    private EditText entradaNumeroDecimal;
    private CheckBox entradaCheckbox;
    private Button boton;

    private CampoMuseo campoBusqueda;

    private TextView labelTexto;
    private TextView labelNumeroSalas;
    private TextView labelNumeroVisitantes;
    private TextView labelNumeroDecimal;

    private class VerificadorEntrada implements TextWatcher {

        EditText entrada;

        VerificadorEntrada(EditText entrada) {
            this.entrada = entrada;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            cambiaBoton(entrada);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_busqueda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Búsqueda");
        actionBar.setDisplayHomeAsUpEnabled(true);

        entradaTexto = (EditText) findViewById(R.id.entradaTexto);
        entradaTexto.addTextChangedListener(new VerificadorEntrada(entradaTexto));

        entradaNumero = (EditText) findViewById(R.id.entradaNumero);
        entradaNumero.addTextChangedListener(new VerificadorEntrada(entradaNumero));

        entradaNumeroDecimal = (EditText) findViewById(R.id.entradaNumeroDecimal);
        entradaNumeroDecimal.addTextChangedListener(new VerificadorEntrada(entradaNumeroDecimal));

        entradaCheckbox = (CheckBox) findViewById(R.id.entradaCheckbox);

        boton = (Button) findViewById(R.id.botonBusqueda);

        labelTexto = (TextView) findViewById(R.id.labelTexto);
        labelNumeroSalas = (TextView) findViewById(R.id.labelNumeroSalas);
        labelNumeroVisitantes = (TextView) findViewById(R.id.labelNumeroVisitantes);
        labelNumeroDecimal = (TextView) findViewById(R.id.labelNumeroDecimal);

        creaRadioGroup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void realizaBusqueda(View view) {
        Intent intent = new Intent();
        intent.putExtra("campo", campoBusqueda.toString());

        switch (campoBusqueda) {
            case NOMBRE:
                intent.putExtra("entrada", entradaTexto.getText().toString());
                break;
            case SALAS:
            case VISITANTES:
                intent.putExtra("entrada", Integer.parseInt(entradaNumero.getText().toString()));
                break;
            case COSTOGENERAL:
            case COSTOESTUDIANTES:
                intent.putExtra("entrada", Double.parseDouble(entradaNumeroDecimal.getText().toString()));
                break;
            case ESTACIONAMIENTO:
                intent.putExtra("entrada", entradaCheckbox.isChecked());
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    private void creaRadioGroup() {
        CampoMuseo[] campos = CampoMuseo.values();
        RadioButton[] radioButtons = new RadioButton[campos.length];

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.opciones);
        radioGroup.removeAllViews();

        for (int i = 0; i < campos.length; i++) {
            radioButtons[i] = new RadioButton(this);
            radioButtons[i].setText(campos[i].toString());
            radioButtons[i].setId(i + 1);
            radioGroup.addView(radioButtons[i]);
        }

        radioGroup.setOnCheckedChangeListener((group, pos) -> despliegaEntradas(campos[pos - 1]));
    }

    private void cambiaBoton(EditText entrada) {
        boton.setEnabled(!entrada.getText().toString().isEmpty());
    }

    private void despliegaEntradas(CampoMuseo campo) {
        campoBusqueda = campo;

        entradaTexto.setVisibility(View.GONE);
        entradaNumero.setVisibility(View.GONE);
        entradaNumeroDecimal.setVisibility(View.GONE);
        entradaCheckbox.setVisibility(View.GONE);

        boton.setVisibility(View.VISIBLE);

        labelTexto.setVisibility(View.GONE);
        labelNumeroSalas.setVisibility(View.GONE);
        labelNumeroVisitantes.setVisibility(View.GONE);
        labelNumeroDecimal.setVisibility(View.GONE);

        switch (campo) {
            case NOMBRE:
                labelTexto.setVisibility(View.VISIBLE);
                entradaTexto.setVisibility(View.VISIBLE);
                cambiaBoton(entradaTexto);
                break;
            case SALAS:
                labelNumeroSalas.setVisibility(View.VISIBLE);
                entradaNumero.setVisibility(View.VISIBLE);
                cambiaBoton(entradaNumero);
                break;
            case VISITANTES:
                labelNumeroVisitantes.setVisibility(View.VISIBLE);
                entradaNumero.setVisibility(View.VISIBLE);
                cambiaBoton(entradaNumero);
                break;
            case COSTOGENERAL:
            case COSTOESTUDIANTES:
                labelNumeroDecimal.setVisibility(View.VISIBLE);
                entradaNumeroDecimal.setVisibility(View.VISIBLE);
                cambiaBoton(entradaNumeroDecimal);
                break;
            case ESTACIONAMIENTO:
                entradaCheckbox.setVisibility(View.VISIBLE);
                boton.setEnabled(true);
                break;
        }
    }
}
