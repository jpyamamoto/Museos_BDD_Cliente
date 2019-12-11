package com.jpyamamoto.basededatosmuseos.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jpyamamoto.basededatosmuseos.Museo;
import com.jpyamamoto.basededatosmuseos.R;

public class ActividadFormaMuseo extends AppCompatActivity {

    private String nombre;
    private int salas;
    private double costoGeneral;
    private double costoEstudiantes;
    private int visitantes;
    private boolean estacionamiento;

    private Museo museo;
    private Museo museoOriginal;

    private EntradaVerificable entradaNombre;
    private EntradaVerificable entradaSalas;
    private EntradaVerificable entradaCostoGeneral;
    private EntradaVerificable entradaCostoEstudiantes;
    private EntradaVerificable entradaVisitantes;
    private Switch entradaEstacionamiento;

    private Button boton;
    private boolean edita;

    private class VerificadorEntrada implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean result = verificaMuseo();
            boton.setEnabled(result);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_forma_museo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Forma Museo");
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        edita = intent.getBooleanExtra("editaMuseo", false);

        boton = (Button) findViewById(R.id.botonConfirmar);

        if (edita) {
            String linea = intent.getStringExtra("infoMuseo");
            museo = new Museo(null, 0, 0, 0, 0, false);
            museoOriginal = new Museo(null, 0, 0, 0, 0, false);

            museo.deLinea(linea);
            museoOriginal.deLinea(linea);

            boton.setText("Actualizar");
        }

        estacionamiento = false;
        entradaEstacionamiento = (Switch) findViewById(R.id.estacionamientoInput);
        entradaEstacionamiento.setOnCheckedChangeListener(((buttonView, isChecked) -> estacionamiento = isChecked));

        entradaNombre = (EntradaVerificable) findViewById(R.id.nombreInput);
        TextView nombreError = (TextView) findViewById(R.id.nombreError);
        entradaNombre.setVerificador(n -> verificaNombre(n));
        entradaNombre.setTextoError(nombreError);
        entradaNombre.addTextChangedListener(new VerificadorEntrada());

        entradaSalas = (EntradaVerificable) findViewById(R.id.salasInput);
        TextView salasError = (TextView) findViewById(R.id.salasError);
        entradaSalas.setVerificador(s -> verificaSalas(s));
        entradaSalas.setTextoError(salasError);
        entradaSalas.addTextChangedListener(new VerificadorEntrada());

        entradaCostoGeneral = (EntradaVerificable) findViewById(R.id.costoGeneralInput);
        TextView costoGeneralError = (TextView) findViewById(R.id.costoGeneralError);
        entradaCostoGeneral.setVerificador(cg -> verificaCostoGeneral(cg));
        entradaCostoGeneral.setTextoError(costoGeneralError);
        entradaCostoGeneral.addTextChangedListener(new VerificadorEntrada());

        entradaCostoEstudiantes = (EntradaVerificable) findViewById(R.id.costoEstudiantesInput);
        TextView costoEstudiantesError = (TextView) findViewById(R.id.costoEstudiantesError);
        entradaCostoEstudiantes.setVerificador(ce -> verificaCostoEstudiantes(ce));
        entradaCostoEstudiantes.setTextoError(costoEstudiantesError);
        entradaCostoEstudiantes.addTextChangedListener(new VerificadorEntrada());

        entradaVisitantes = (EntradaVerificable) findViewById(R.id.visitantesInput);
        TextView visitantesError = (TextView) findViewById(R.id.visitantesError);
        entradaVisitantes.setVerificador(v -> verificaVisitantes(v));
        entradaVisitantes.setTextoError(visitantesError);
        entradaVisitantes.addTextChangedListener(new VerificadorEntrada());

        setMuseo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void confirmar(View view) {
        museo.setNombre(nombre);
        museo.setSalas(salas);
        museo.setCostoGeneral(costoGeneral);
        museo.setCostoEstudiantes(costoEstudiantes);
        museo.setVisitantes(visitantes);
        museo.setEstacionamiento(estacionamiento);

        Intent intent = new Intent();
        intent.putExtra("infoMuseo", museo.aLinea());
        intent.putExtra("editaMuseo", edita);

        if (edita)
            intent.putExtra("infoMuseoOriginal", museoOriginal.aLinea());

        setResult(RESULT_OK, intent);
        finish();
    }

    private void setMuseo() {
        if (museo == null) {
            museo = new Museo(null, 0, 0, 0, 0, false);
            return;
        }

        entradaNombre.setText(museo.getNombre());
        entradaSalas.setText(Integer.toString(museo.getSalas()));
        entradaCostoGeneral.setText(String.format("%.2f", museo.getCostoGeneral()));
        entradaCostoEstudiantes.setText(String.format("%.2f", museo.getCostoEstudiantes()));
        entradaVisitantes.setText(Integer.toString(museo.getVisitantes()));
        entradaEstacionamiento.setChecked(museo.getEstacionamiento());
    }

    private boolean verificaMuseo() {
        boolean n  = entradaNombre.esValida();
        boolean s  = entradaSalas.esValida();
        boolean cg = entradaCostoGeneral.esValida();
        boolean ce = entradaCostoEstudiantes.esValida();
        boolean v  = entradaVisitantes.esValida();

        return n && s && cg && ce && v;
    }

    private boolean verificaNombre(String n) {
        if (n == null || n.trim().isEmpty())
            return false;
        nombre = n;
        return true;
    }

    private boolean verificaSalas(String s) {
        if (s == null || s.trim().isEmpty())
            return false;
        try {
            salas = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return salas >= 0;
    }

    private boolean verificaCostoGeneral(String cg) {
        if (cg == null || cg.trim().isEmpty())
            return false;
        try {
            costoGeneral = Double.parseDouble(cg);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return costoGeneral >= 0.0;
    }

    private boolean verificaCostoEstudiantes(String ce) {
        if (ce == null || ce.trim().isEmpty())
            return false;
        try {
            costoEstudiantes = Double.parseDouble(ce);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return costoEstudiantes >= 0.0;
    }

    private boolean verificaVisitantes(String v) {
        if (v == null || v.trim().isEmpty())
            return false;
        try {
            visitantes = Integer.parseInt(v);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return visitantes >= 0;
    }
}
