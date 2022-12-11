package com.pucp.bemyhome.Adoptante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdoptanteDetalleSolicitudActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    private static final String ICON_ID = "optionsMarker";
    TextView tvFechaReseva;
    TextView tvEstado;
    ImageView ivDni;
    TextView nombrePet;
    TextView generoPet;
    TextView edadPet;
    ImageView ivti;
    TextView tvMotivo;
    TextView tvDomicilio;
    TextView tvPropioAlquilado;
    TextView tvJardinPatio;
    TextView tvCantPersonas;
    TextView tvAlergico;
    TextView tvCantMascotas;
    TextView tvVacunas;
    TextView tvTiempoDedicar;
    TextView tvTiempoPasear;
    TextView tvLugarTiempo;
    LinearLayout llResponseInfo;
    LinearLayout llAcceptInfo;
    LinearLayout llRejectInfo;
    TextView tvNombreTI;
    TextView tvFechaResponse;
    TextView tvMotivoRechazo;
    TextView tvCategoria;
    ImageView ivCategoriaPet;
    ImageView ivFotoPet;
    Integer horaReservaNano;
    Long horaReservaSec;
    Integer horaRespNano;
    Long horaRespSec;
    TextView nombreLugar;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private Double latitude;
    private  Double longitud;

    private ArrayList<String> listProgramas = new ArrayList<>();
    ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_adoptante_detalle_solicitud);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Solicitud solicitud = (Solicitud) intent.getSerializableExtra("solicitud");
        latitude = (Double) intent.getSerializableExtra("lati");
        longitud = (Double) intent.getSerializableExtra("long");

        tvFechaReseva = findViewById(R.id.tvAdoptanteDetalleSoliFecha);
        tvEstado = findViewById(R.id.tvCAdoptanteDetalleSoliEstado);
        tvMotivo = findViewById(R.id.tVAdoptanteDetalleSoliMotivo);
        ivDni = findViewById(R.id.ivAdoptanteDetalleSoliDNI);
        llResponseInfo = findViewById(R.id.llAdoptanteDetalleSoliResponse);
        llAcceptInfo = findViewById(R.id.llAdoptanteDetalleSoliLugarRecojo);
        llRejectInfo = findViewById(R.id.llAdoptanteDetalleSoliMotivoRechazo);
        tvNombreTI = findViewById(R.id.tvAdoptanteDetalleSoliNombreAsist);
        tvFechaResponse = findViewById(R.id.tvAdoptanteDetalleSoliFechaResponseAsist);
        tvMotivoRechazo = findViewById(R.id.tvAdoptanteDetalleSoliMotivoRechazo);
        ivti = findViewById(R.id.ivAdoptanteDetalleSoliImageAsist);
        nombrePet = findViewById(R.id.tvAdoptanteDetalleSoliNombre);
        generoPet = findViewById(R.id.tvAdoptanteDetalleSoliGenero);
        edadPet = findViewById(R.id.tvAdoptanteDetalleSoliAnios);
        pbLoading = findViewById(R.id.pbAdoptanteDetalleSoliDNIPhoto);
        ivFotoPet = findViewById(R.id.ivAdoptanteDetalleSoliImagePet);
        ivCategoriaPet = findViewById(R.id.ivAdoptanteDetalleSoliPetCategoria);
        tvCategoria = findViewById(R.id.tvAdoptanteDetalleSoliPetCategoria);
        nombreLugar = findViewById(R.id.tvAdoptanteDetalleSoliLugarRecojoNombre);
        tvDomicilio = findViewById(R.id.tvAdoptanteDetalleSolicitudesDomicilioType);
        tvPropioAlquilado = findViewById(R.id.tvAdoptanteDetalleSoliAlquiladoOno);
        tvJardinPatio = findViewById(R.id.tvAdoptanteDetalleSoliPatioOno);
        tvCantPersonas = findViewById(R.id.tvAdoptanteDetalleSoliCantidadPersonas);
        tvAlergico = findViewById(R.id.tvAdoptanteDetalleSoliAlergico);
        tvCantMascotas = findViewById(R.id.tvAdoptanteDetalleSoliCantidadMascotas);
        tvVacunas = findViewById(R.id.tvAdoptanteDetalleSoliVacunas);
        tvTiempoDedicar = findViewById(R.id.tVAdoptanteDetalleSoliDedicacion);
        tvTiempoPasear = findViewById(R.id.tVAdoptanteDetalleSoliPaseo);
        tvLugarTiempo = findViewById(R.id.tvAdoptanteDetalleSoliLugar);

        horaReservaNano = (Integer) intent.getSerializableExtra("horaReservaNano");
        horaReservaSec = (Long) intent.getSerializableExtra("horaReservaSec");

        if(horaReservaNano!=null && horaReservaSec!=null){
            String fechaReserva = df.format(new Timestamp(horaReservaSec,horaReservaNano).toDate());
            tvFechaReseva.setText("Enviada "+fechaReserva);
        }

        tvEstado.setText(solicitud.getEstado());
        tvMotivo.setText(solicitud.getMotivoReserva());
        tvDomicilio.setText("Vivo en "+solicitud.getDomicilio());
        tvAlergico.setText(solicitud.getAlergico());
        tvCantMascotas.setText(solicitud.getCantMascotas());
        tvCantPersonas.setText(solicitud.getCantPersonasCasa());
        tvJardinPatio.setText(solicitud.getPatioOjardin());
        tvPropioAlquilado.setText(solicitud.getPropioOalquilado());
        tvVacunas.setText(solicitud.getVacunasAlDia());
        tvTiempoDedicar.setText(solicitud.getTiempoDedicar());
        tvTiempoPasear.setText(solicitud.getTiempoPaseo());
        tvLugarTiempo.setText(solicitud.getLugarParteTiempo());
        nombrePet.setText(solicitud.getPets().getNombre());
        edadPet.setText(solicitud.getPets().getEdad() + " meses");
        generoPet.setText(solicitud.getPets().getGenero() + " ,");

        ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(solicitud.getDni()).placeholder(com.denzcoskun.imageslider.R.drawable.placeholder).into(ivDni);


        tvCategoria.setText(solicitud.getPets().getCategoria());

        switch (solicitud.getPets().getCategoria()){
            case "Gato":
                ivCategoriaPet.setImageResource(R.drawable.cat);
                break;
            case "Perro":
                ivCategoriaPet.setImageResource(R.drawable.dog);
                break;
            case "Ave":
                ivCategoriaPet.setImageResource(R.drawable.bird);
                break;
            case "Conejo":
                ivCategoriaPet.setImageResource(R.drawable.rabbit);
                break;
            case "Otros":
                ivCategoriaPet.setImageResource(R.drawable.others);
        }

        ivFotoPet.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this).load(solicitud.getPets().getFotoUrl()).placeholder(com.denzcoskun.imageslider.R.drawable.placeholder).into(ivFotoPet);

        if(!solicitud.getEstado().equals("Pendiente de aprobación")){
            llResponseInfo.setVisibility(View.VISIBLE);
            horaRespNano = (Integer) intent.getSerializableExtra("horaRespNano");
            horaRespSec = (Long) intent.getSerializableExtra("horaRespSec");
            if(horaRespNano!=null && horaRespSec!=null){
                String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                tvFechaResponse.setText(fechaResp);
            }
            tvNombreTI.setText(solicitud.getAsistUser().getNombre());
            Glide.with(this).load(solicitud.getAsistUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);
            switch (solicitud.getEstado()){
                case "Solicitud rechazada":
                    llRejectInfo.setVisibility(View.VISIBLE);
                    llAcceptInfo.setVisibility(View.GONE);
                    tvEstado.setTextColor(getResources().getColor(R.color.red));
                    tvMotivoRechazo.setText(solicitud.getMotivoRechazo());
                    break;
                case "Solicitud aceptada":
                    llAcceptInfo.setVisibility(View.VISIBLE);
                    llRejectInfo.setVisibility(View.GONE);
                    tvEstado.setTextColor(getResources().getColor(R.color.green));
                    mapView = findViewById(R.id.mvAdoptanteDetalleSoliMap);
                    mapView.onCreate(savedInstanceState);
                    LatLng coord = new LatLng(latitude,longitud);
                    if(latitude != null && longitud != null){
                        mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {
                            this.mapboxMap = mapboxMap;
                            mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitud)).setTitle(solicitud.getNombreLugarRecojo()));
                            symbolManager = new SymbolManager(mapView, mapboxMap, style, null);
                            SymbolOptions symbolOptions = new SymbolOptions()
                                    .withLatLng(coord)
                                    .withIconImage(ICON_ID)
                                    .withIconSize(0.5f);
                            symbolManager.create(symbolOptions);
                            mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude,longitud)).zoom(15).build());
                        }));
                    }
                    nombreLugar.setText(solicitud.getNombreLugarRecojo());
                    break;
            }

        }else{
            tvEstado.setTextColor(getResources().getColor(R.color.yellow));
            llResponseInfo.setVisibility(View.GONE);
            llAcceptInfo.setVisibility(View.GONE);
            llRejectInfo.setVisibility(View.GONE);
        }

    }

    public void backButton(View view){
        onBackPressed();
    }

}