package com.jpyamamoto.basededatosmuseos.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jpyamamoto.basededatosmuseos.BaseDeDatosMuseos;
import com.jpyamamoto.basededatosmuseos.CampoMuseo;
import com.jpyamamoto.basededatosmuseos.Lista;
import com.jpyamamoto.basededatosmuseos.Museo;
import com.jpyamamoto.basededatosmuseos.R;
import com.jpyamamoto.basededatosmuseos.red.Conexion;
import com.jpyamamoto.basededatosmuseos.red.Mensaje;

import java.io.IOException;
import java.net.Socket;

public class ActividadTabla extends AppCompatActivity {

    private final static int FORMA;
    private final static int BUSQUEDA;

    static {
        FORMA = 1;
        BUSQUEDA = 2;
    }

    private String servidor;
    private int puerto;

    private TextView orden;

    private BaseDeDatosMuseos bdd;
    private Conexion<Museo> conexion;
    private AdaptadorMuseosOrdenados adaptador;
    private Lista<Museo> seleccionados;

    private class ConectaClienteBaseDeDatosMuseos implements Runnable {
        @Override public void run() {
            try {
                Socket enchufe = new Socket(servidor, puerto);
                conexion = new Conexion<Museo>(bdd, enchufe);
                new Thread(() -> conexion.recibeMensajes()).start();
                conexion.agregaEscucha((con, men) -> mensajeRecibido(con, men));
                conexion.enviaMensaje(Mensaje.BASE_DE_DATOS);
            } catch (IOException ioe) {
                dialogoError("Error al establecer conexión.",
                        String.format("Ocurrió un error al tratar de conectarnos a %s:%d",
                                servidor, puerto), true);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_tabla);
        seleccionados = new Lista<Museo>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Museos");
        actionBar.setDisplayHomeAsUpEnabled(true);

        bdd = new BaseDeDatosMuseos();
        orden = (TextView) findViewById(R.id.ordenamiento);

        Intent intent = getIntent();
        servidor = intent.getStringExtra("servidor");
        puerto = intent.getIntExtra("puerto", 0);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recyclerId);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adaptador = new AdaptadorMuseosOrdenados(bdd,
                (museo1, museo2) ->museo1.getNombre().compareTo(museo2.getNombre()),
                (sel) -> cambiaToolbar(sel));
        recycler.setAdapter(adaptador);

        bdd.agregaEscucha((ev, r1, r2) -> runOnUiThread(() -> adaptador.notificaCambio()));
        new Thread(new ConectaClienteBaseDeDatosMuseos()).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tabla, menu);

        if (seleccionados.getLongitud() == 1)
            menu.findItem(R.id.accionEditar).setVisible(true);
        else
            menu.findItem(R.id.accionEditar).setVisible(false);

        if (seleccionados.getLongitud() != 0)
            menu.findItem(R.id.accionEliminar).setVisible(true);
        else
            menu.findItem(R.id.accionEliminar).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.accionEditar:
                abrirFormaMuseo(true);
                return true;
            case R.id.accionEliminar:
                eliminaSeleccionados();
                return true;
            case R.id.accionBuscar:
                abrirBusqueda();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FORMA)
            resultadoForma(resultCode, data);

        if (requestCode == BUSQUEDA)
            resultadoBusqueda(resultCode, data);
    }

    @Override
    protected void onDestroy() {
        desconecta();
        super.onDestroy();
    }

    public void nuevoRegistro(View view) {
        abrirFormaMuseo(false);
    }

    public void cambiarOrden(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActividadTabla.this);
        builder.setTitle("Elige un campo por el cuál ordenar.");

        CampoMuseo[] campos = CampoMuseo.values();
        String[] opciones = new String[campos.length];

        for (int i = 0; i < campos.length; i++)
            opciones[i] = campos[i].toString();

        builder.setItems(opciones, (dialog, position) -> cambiaComparador(campos[position]));
        builder.show();
    }

    private void resultadoForma(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            boolean edita = data.getBooleanExtra("editaMuseo", false);

            if (edita)
                editaMuseo(data);
            else
                agregaMuseo(data);

            return;
        }

        if (resultCode == RESULT_CANCELED)
            return;

        dialogoError("Error al agregar registro.",
                "Ocurrió un error inesperado al agregar el registro", false);
    }

    private void resultadoBusqueda(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            CampoMuseo campo = CampoMuseo.getCampo(data.getStringExtra("campo"));

            switch (campo) {
                case NOMBRE:
                    adaptador.busca(campo, data.getStringExtra("entrada"));
                    break;
                case SALAS:
                case VISITANTES:
                    adaptador.busca(campo, data.getIntExtra("entrada", 0));
                    break;
                case COSTOGENERAL:
                case COSTOESTUDIANTES:
                    adaptador.busca(campo, data.getDoubleExtra("entrada", 0.0));
                    break;
                case ESTACIONAMIENTO:
                    adaptador.busca(campo, data.getBooleanExtra("entrada", false));
            }
            return;
        }

        if (resultCode == RESULT_CANCELED)
            return;

        dialogoError("Error al buscar registros.",
                "Ocurrió un error inesperado al buscar registros.", false);
    }

    private void cambiaComparador(CampoMuseo campo) {
        switch (campo) {
            case NOMBRE:
                adaptador.setComparador((museo1, museo2) ->
                        museo1.getNombre().compareTo(museo2.getNombre()));
                break;
            case SALAS:
                adaptador.setComparador((museo1, museo2) ->
                        Integer.compare(museo1.getSalas(), museo2.getSalas()));
                break;
            case COSTOGENERAL:
                adaptador.setComparador((museo1, museo2) ->
                        Double.compare(museo1.getCostoGeneral(), museo2.getCostoGeneral()));
                break;
            case COSTOESTUDIANTES:
                adaptador.setComparador((museo1, museo2) ->
                        Double.compare(museo1.getCostoEstudiantes(), museo2.getCostoEstudiantes()));
                break;
            case VISITANTES:
                adaptador.setComparador((museo1, museo2) ->
                        Integer.compare(museo1.getVisitantes(), museo2.getVisitantes()));
                break;
            case ESTACIONAMIENTO:
                adaptador.setComparador((museo1, museo2) ->
                        Boolean.compare(museo1.getEstacionamiento(), museo2.getEstacionamiento()));
        }
        orden.setText(campo.toString());
    }

    private void eliminaSeleccionados() {
        new Thread(() -> {
            for (Museo museo : seleccionados) {
                try {
                    conexion.enviaMensaje(Mensaje.REGISTRO_ELIMINADO);
                    conexion.enviaRegistro(museo);
                } catch (IOException ioe) {
                    dialogoError("Error con el servidor",
                            "No se pudo enviar el museo a eliminar", true);
                }

                seleccionados.elimina(museo);
                bdd.eliminaRegistro(museo);
            }
        }).start();
    }

    private void cambiaToolbar(Lista<Museo> seleccionados) {
        this.seleccionados = seleccionados;
        invalidateOptionsMenu();
    }

    private void abrirBusqueda() {
        Intent intent = new Intent(getApplicationContext(), ActividadBusqueda.class);
        startActivityForResult(intent, BUSQUEDA);
    }

    private void abrirFormaMuseo(boolean edita) {
        Intent intent = new Intent(getApplicationContext(), ActividadFormaMuseo.class);
        intent.putExtra("editaMuseo", false);

        if (edita && seleccionados.getLongitud() == 1) {
            intent.putExtra("infoMuseo", seleccionados.get(0).aLinea());
            intent.putExtra("editaMuseo", true);
        }

        startActivityForResult(intent, FORMA);
    }

    private void editaMuseo(Intent intent) {
        seleccionados.limpia();
        invalidateOptionsMenu();

        String lineaOriginal = intent.getStringExtra("infoMuseoOriginal");
        String lineaNueva = intent.getStringExtra("infoMuseo");

        Museo museoOriginal = new Museo(null, 0, 0, 0, 0, false);
        Museo museoNuevo = new Museo(null, 0, 0, 0, 0, false);

        museoOriginal.deLinea(lineaOriginal);
        museoNuevo.deLinea(lineaNueva);

        new Thread(() -> {
            try {
                conexion.enviaMensaje(Mensaje.REGISTRO_MODIFICADO);
                conexion.enviaRegistro(museoOriginal);
                conexion.enviaRegistro(museoNuevo);
            } catch (IOException ioe) {
                dialogoError("Error con el servidor",
                        "No se pudo enviar el museo a modificar.", false);
            }
        }).start();

        bdd.modificaRegistro(museoOriginal, museoNuevo);
    }

    private void agregaMuseo(Intent intent) {
        String linea = intent.getStringExtra("infoMuseo");
        Museo museo = new Museo(null, 0, 0, 0, 0, false);
        museo.deLinea(linea);

        new Thread(() -> {
            try {
                conexion.enviaMensaje(Mensaje.REGISTRO_AGREGADO);
                conexion.enviaRegistro(museo);
            } catch (IOException ioe) {
                dialogoError("Error con el servidor",
                        "No se pudo enviar el museo a ser agregado.", false);
            }
        }).start();

        bdd.agregaRegistro(museo);
    }

    private void mensajeRecibido(Conexion<Museo> conexion, Mensaje mensaje) {
        switch (mensaje) {
            case BASE_DE_DATOS:
                manejaBaseDeDatos(conexion);
                break;
            case REGISTRO_AGREGADO:
            case REGISTRO_ELIMINADO:
                manejaRegistroAlterado(conexion, mensaje);
                break;
            case REGISTRO_MODIFICADO:
                manejaRegistroModificado(conexion);
                break;
            case DESCONECTAR:
                manejaDesconectar();
            case ECO:
                break;
            case INVALIDO:
                dialogoError("Error con el servidor.",
                             "Mensaje inválido recibido. " +
                             "Se finalizará la conexión.", true);
                break;
        }
    }

    private void manejaBaseDeDatos(Conexion<Museo> conexion) {
        try {
            conexion.recibeBaseDeDatos();
        } catch (IOException ioe) {
            dialogoError("Error con el servidor.",
                    "No se pudo recibir la base de datos. " +
                            "Se finalizará la conexión.", true);
        }
    }

    private void manejaRegistroAlterado(Conexion<Museo> conexion, Mensaje mensaje) {
        Museo museo;
        try {
            museo = conexion.recibeRegistro();
        } catch (IOException ioe) {
            dialogoError("Error con el servidor",
                    "No se pudo recibir un registro. " +
                            "Se finalizará la conexión.", true);
            return;
        }
        if (mensaje == Mensaje.REGISTRO_AGREGADO)
            bdd.agregaRegistro(museo);
        else
            bdd.eliminaRegistro(museo);
    }

    private void manejaRegistroModificado(Conexion<Museo> conexion) {
        Museo e1, e2;
        try {
            e1 = conexion.recibeRegistro();
            e2 = conexion.recibeRegistro();
        } catch (IOException ioe) {
            dialogoError("Error con el servidor",
                    "No se pudieron recibir registros." +
                            "Se finalizará la conexión.", true);
            return;
        }
        bdd.modificaRegistro(e1, e2);
    }

    private void manejaDesconectar() {
        desconecta();
        finish();
    }

    private void desconecta() {
        if (conexion != null && conexion.isActiva())
            conexion.desconecta();
        conexion = null;
        bdd.limpia();
    }

    private void dialogoError(String titulo, String mensaje, boolean exit) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(ActividadTabla.this)
                    .setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        if (exit)
                            finish();
                    })
                    .setIcon(R.drawable.ic_warning)
                    .show();
        });
    }
}
