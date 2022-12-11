package com.pucp.bemyhome.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AsistenteSolicitudesAdapter extends FirestorePagingAdapter<Solicitud, AsistenteSolicitudesAdapter.SolicitudesViewHolder> {
    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());

    public AsistenteSolicitudesAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;

    @NonNull
    @Override
    public SolicitudesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_asistente_solicitud, parent, false);
        return new SolicitudesViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull SolicitudesViewHolder holder, int position, @NonNull Solicitud model) {
        if(model.getEstado().equals("Pendiente de aprobación")){
            holder.tvSolicitud.setText(("Solicitud "+model.getKey()).toUpperCase(Locale.ROOT));
        }else{
            holder.tvSolicitud.setText(model.getEstado());
            holder.tvSolicitud.setTextColor(R.color.black);
        }
        String fechaReserva = df.format(model.getHoraReserva().toDate());
        holder.tvFechaRealizado.setText("Enviada "+fechaReserva);
        holder.tvAsist.setText("De "+ model.getAdoptanteUser().getNombre());
        holder.solicitud = model;
    }

    public class SolicitudesViewHolder extends RecyclerView.ViewHolder {

        TextView tvSolicitud;
        TextView tvAsist;
        TextView tvFechaRealizado;
        Solicitud solicitud;
        Button btnDetalleAsist;

        public SolicitudesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSolicitud = itemView.findViewById(R.id.tvAsistCardSolicitud);
            tvAsist= itemView.findViewById(R.id.tvAsistCardAsist);
            tvFechaRealizado= itemView.findViewById(R.id.tvAsistCardFechaReserva);
            btnDetalleAsist= itemView.findViewById(R.id.btnAsistCardDetalle);
            itemView.setOnClickListener(view -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("solicitud", solicitud);
                reservaIntent.putExtra("horaReservaNano",solicitud.getHoraReserva().getNanoseconds());
                reservaIntent.putExtra("horaReservaSec",solicitud.getHoraReserva().getSeconds());
                if(solicitud.getHoraRespuesta()!=null) {
                    reservaIntent.putExtra("horaRespNano", solicitud.getHoraRespuesta().getNanoseconds());
                    reservaIntent.putExtra("horaRespSec", solicitud.getHoraRespuesta().getSeconds());
                }
                if(solicitud.getEstado().equals("Solicitud aceptada")){
                    reservaIntent.putExtra("lati", solicitud.getLugarRecojo().getLatitude());
                    reservaIntent.putExtra("long", solicitud.getLugarRecojo().getLongitude());
                }
                itemView.getContext().startActivity(reservaIntent);
            });
            btnDetalleAsist.setOnClickListener(v -> {
                Intent reservaIntent = new Intent(itemView.getContext(), nextActivity);
                reservaIntent.putExtra("solicitud", solicitud);
                reservaIntent.putExtra("horaReservaNano",solicitud.getHoraReserva().getNanoseconds());
                reservaIntent.putExtra("horaReservaSec",solicitud.getHoraReserva().getSeconds());
                if(solicitud.getHoraRespuesta()!=null) {
                    reservaIntent.putExtra("horaRespNano", solicitud.getHoraRespuesta().getNanoseconds());
                    reservaIntent.putExtra("horaRespSec", solicitud.getHoraRespuesta().getSeconds());
                }
                if(solicitud.getEstado().equals("Solicitud aceptada")){
                    reservaIntent.putExtra("lati", solicitud.getLugarRecojo().getLatitude());
                    reservaIntent.putExtra("long", solicitud.getLugarRecojo().getLongitude());
                }
                itemView.getContext().startActivity(reservaIntent);
            });
        }
    }

}
