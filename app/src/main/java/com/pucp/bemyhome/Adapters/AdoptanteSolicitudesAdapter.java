package com.pucp.bemyhome.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdoptanteSolicitudesAdapter extends FirestorePagingAdapter<Solicitud, AdoptanteSolicitudesAdapter.SolicitudesViewHolder> {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());

    public AdoptanteSolicitudesAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;

    @NonNull
    @Override
    public SolicitudesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_adoptante_adopciones, parent, false);
        return new SolicitudesViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull SolicitudesViewHolder holder, int position, @NonNull Solicitud model) {
        holder.tvPetName.setText(model.getPets().getNombre());
        String fechaReserva = df.format(model.getHoraReserva().toDate());
        holder.estado.setText(model.getEstado());
        holder.tvFechaRealizado.setText(fechaReserva);
        holder.solicitud = model;
        holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.itemView.getContext()).load(model.getPets().getFotoUrl())
                .placeholder(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.image_pets_placeholder)).dontAnimate()
                .into(holder.ivPhoto);
    }

    public class SolicitudesViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPhoto;
        TextView tvPetName;
        TextView tvFechaRealizado;
        Solicitud solicitud;
        TextView estado;
        Button btnDetalle;

        public SolicitudesViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivAdopAdopcionesCardImage);
            tvPetName = itemView.findViewById(R.id.tvAdopAdopcionesCardNombre);
            tvFechaRealizado= itemView.findViewById(R.id.tvAdopAdopcionesCardFecha);
            estado = itemView.findViewById(R.id.tvAdopAdopcionesCardEstadoSolicitud);
            btnDetalle = itemView.findViewById(R.id.btnCAdopAdopcionesCardDetalle);
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
            btnDetalle.setOnClickListener(v -> {
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
