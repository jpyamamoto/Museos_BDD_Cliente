package com.jpyamamoto.basededatosmuseos.vistas;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jpyamamoto.basededatosmuseos.BaseDeDatosMuseos;
import com.jpyamamoto.basededatosmuseos.CampoMuseo;
import com.jpyamamoto.basededatosmuseos.Lista;
import com.jpyamamoto.basededatosmuseos.Museo;
import com.jpyamamoto.basededatosmuseos.R;

import java.util.Comparator;

public class AdaptadorMuseosOrdenados
        extends RecyclerView.Adapter<AdaptadorMuseosOrdenados.ViewHolderMuseos> {

    private BaseDeDatosMuseos bdd;
    private Lista<Museo> listaMuseos;
    private Comparator<Museo> comparador;
    private EscuchaSeleccion escuchaSeleccion;
    private Lista<Museo> seleccionados;

    public AdaptadorMuseosOrdenados(BaseDeDatosMuseos bdd, Comparator<Museo> comparador,
                                    EscuchaSeleccion escuchaSeleccion) {
        this.bdd = bdd;
        this.comparador = comparador;
        this.escuchaSeleccion = escuchaSeleccion;
        this.listaMuseos = bdd.getRegistros().mergeSort(comparador);
        this.seleccionados = new Lista<Museo>();
    }

    @NonNull
    @Override
    public AdaptadorMuseosOrdenados.ViewHolderMuseos
    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.registro, null, false);

        return new ViewHolderMuseos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMuseosOrdenados.ViewHolderMuseos holder, int position) {
        Museo museo = listaMuseos.get(position);
        boolean contiene = seleccionados.contiene(museo);
        holder.asignarMuseos(museo, contiene);

        if (contiene)
            holder.itemView.setBackgroundColor(Color.parseColor("#77D1ED"));
        else
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return listaMuseos.getLongitud();
    }

    public void notificaCambio() {
        this.listaMuseos = bdd.getRegistros().mergeSort(comparador);
        notifyDataSetChanged();
    }

    public void setComparador(Comparator<Museo> comparador) {
        this.comparador = comparador;
        seleccionados.limpia();
        escuchaSeleccion.cambioSeleccion(seleccionados);
        notificaCambio();
    }

    public void busca(CampoMuseo campo, Object valor) {
        seleccionados = bdd.buscaRegistros(campo, valor);
        escuchaSeleccion.cambioSeleccion(seleccionados);
        notificaCambio();
    }

    public class ViewHolderMuseos extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nombre;
        TextView salas;
        TextView costoGeneral;
        TextView costoEstudiantes;
        TextView visitantes;
        TextView estacionamiento;
        boolean seleccionado;

        ViewHolderMuseos(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.museoNombre);
            salas = (TextView) itemView.findViewById(R.id.museoSalas);
            costoGeneral = (TextView) itemView.findViewById(R.id.museoCostoGeneral);
            costoEstudiantes = (TextView) itemView.findViewById(R.id.museoCostoEstudiantes);
            visitantes = (TextView) itemView.findViewById(R.id.museoVisitantes);
            estacionamiento = (TextView) itemView.findViewById(R.id.museoEstacionamiento);
            seleccionado = false;
            itemView.setOnClickListener(this);
        }

        void asignarMuseos(Museo museo, boolean seleccionado) {
            this.nombre.setText(museo.getNombre());
            this.salas.setText(String.format("%d salas", museo.getSalas()));
            this.costoGeneral.setText(String.format("$%.2f", museo.getCostoGeneral()));
            this.costoEstudiantes.setText(String.format("$%.2f", museo.getCostoEstudiantes()));
            this.visitantes.setText(String.format("%,d visitantes/año", museo.getVisitantes()));
            this.estacionamiento.setText(museo.getEstacionamiento() ? "Sí tiene estacionamiento" : "No tiene estacionamiento");
            this.seleccionado = seleccionado;

            escuchaSeleccion.cambioSeleccion(seleccionados);
        }

        @Override
        public void onClick(View v) {
            seleccionado = !seleccionado;

            if (seleccionado) {
                itemView.setBackgroundColor(Color.parseColor("#77D1ED"));
                seleccionados.agregaFinal(listaMuseos.get(getLayoutPosition()));
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
                seleccionados.elimina(listaMuseos.get(getLayoutPosition()));
            }

            escuchaSeleccion.cambioSeleccion(seleccionados);
        }
    }
}
