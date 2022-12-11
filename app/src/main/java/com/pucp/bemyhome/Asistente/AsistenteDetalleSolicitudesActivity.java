package com.pucp.bemyhome.Asistente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Locale;

public class AsistenteDetalleSolicitudesActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    private static final String ICON_ID = "optionsMarker";

    TextView tvFechaReserva;
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
    LinearLayout llButtons;
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
    TextView tvNombreAdopt;
    ImageView ivAdopt;
    Button btnAccept;
    Button btnReject;
    TextView tvdniAdopt;
    ProgressBar pbLoading;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private SymbolManager symbolManager;
    private Double latitude;
    private  Double longitud;
    Solicitud solicitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_asistente_detalle_solicitudes);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        solicitud = (Solicitud) intent.getSerializableExtra("solicitud");
        horaReservaNano = (Integer) intent.getSerializableExtra("horaReservaNano");
        horaReservaSec = (Long) intent.getSerializableExtra("horaReservaSec");
        latitude = (Double) intent.getSerializableExtra("lati");
        longitud = (Double) intent.getSerializableExtra("long");

        tvFechaReserva = findViewById(R.id.tvAsistenteDetalleSolicitudesGeFechaReserva);
        tvEstado = findViewById(R.id.tvAsistenteDetalleSolicitudesEstado);
        tvMotivo = findViewById(R.id.tVAdoptanteDetalleSoliMotivo);
        ivDni = findViewById(R.id.ivAsistenteDetalleSolicitudesDNI);
        llResponseInfo = findViewById(R.id.llAsistenteDetalleResponseInfo);
        llAcceptInfo = findViewById(R.id.llvLugarRecojo);
        llRejectInfo = findViewById(R.id.llAsistenteDetalleSolicitudesMotivoRechazo);
        tvNombreTI = findViewById(R.id.tvAsistenteDetalleSolicitudesNombreAsist);
        tvFechaResponse = findViewById(R.id.tvAsistenteDetalleSolicitudesFechaResponseAsist);
        tvMotivoRechazo = findViewById(R.id.tvAsistenteDetalleSolicitudesMotivoRechazo);
        ivti = findViewById(R.id.ivAsistenteDetalleSolicitudesImageAsist);
        tvCategoria = findViewById(R.id.tvAsistenteDetalleSoliPetCategoria);
        tvNombreAdopt = findViewById(R.id.tvAsistenteDetalleSolicitudesNombreAdopt);
        ivAdopt= findViewById(R.id.ivAsistenteDetalleSolicitudesImageAdopt);
        nombrePet = findViewById(R.id.tvAsistenteDetalleSolicitudesNombre);
        generoPet = findViewById(R.id.tvAsistenteDetalleSolicitudesGenero);
        edadPet = findViewById(R.id.tvAsistenteDetalleSolicitudesAnios);
        ivCategoriaPet = findViewById(R.id.ivAsistenteDetalleSoliPetCategoria);
        ivFotoPet = findViewById(R.id.ivAsistenteDetalleSolicitudesImagePet);
        tvDomicilio = findViewById(R.id.tvAsistenteDetalleSolicitudesDomicilioType);
        tvPropioAlquilado = findViewById(R.id.tvAsistenteDetalleSolicitudesAlquiladoOno);
        tvJardinPatio = findViewById(R.id.tvAsistenteDetalleSolicitudesPatioOno);
        tvCantPersonas = findViewById(R.id.tVAsistenteDetalleSolicitudesCantidadPersonas);
        tvAlergico = findViewById(R.id.tvAsistenteDetalleSolicitudesAlergico);
        tvCantMascotas = findViewById(R.id.tVAsistenteDetalleSolicitudesCantidadMascotas);
        tvVacunas = findViewById(R.id.tvAsistenteDetalleSolicitudesVacunas);
        tvTiempoDedicar = findViewById(R.id.tVAsistenteDetalleSolicitudesDedicacion);
        tvTiempoPasear = findViewById(R.id.tVAsistenteDetalleSolicitudesPaseo);
        tvLugarTiempo = findViewById(R.id.tvAsistenteDetalleSolicitudesLugar);
        pbLoading = findViewById(R.id.pbAdoptanteDetalleSoliLoading);
        llButtons = findViewById(R.id.llAsistenteDetalleSolicitudesButtons);
        btnAccept = findViewById(R.id.btnAsistenteDetalleSolicitudesAceptarSoli);
        btnReject = findViewById(R.id.btnAsistenteDetalleSolicitudesRechazarSoli);
        nombreLugar = findViewById(R.id.tvAsistenteDetalleSolicitudesLugarRecojoNombre);
        tvdniAdopt = findViewById(R.id.tvAsistenteDetalleSolicitudesDni);

        if(horaReservaNano!=null && horaReservaSec!=null){
            String fechaReserva = df.format(new Timestamp(horaReservaSec,horaReservaNano).toDate());
            tvFechaReserva.setText("Enviada "+fechaReserva);
        }

        tvEstado.setText(solicitud.getEstado());
        tvMotivo.setText(solicitud.getMotivoReserva());
        nombrePet.setText(solicitud.getPets().getNombre());
        edadPet.setText(solicitud.getPets().getEdad()+" meses");
        generoPet.setText(solicitud.getPets().getGenero()+" , ");
        tvDomicilio.setText("Vivo en "+solicitud.getDomicilio());
        tvPropioAlquilado.setText(solicitud.getPropioOalquilado());
        tvJardinPatio.setText(solicitud.getPatioOjardin());
        tvCantPersonas.setText(solicitud.getCantPersonasCasa());
        tvAlergico.setText(solicitud.getAlergico());
        tvCantMascotas.setText(solicitud.getCantMascotas());
        tvVacunas.setText(solicitud.getVacunasAlDia());
        tvTiempoDedicar.setText(solicitud.getTiempoDedicar());
        tvTiempoPasear.setText(solicitud.getTiempoPaseo());
        tvLugarTiempo.setText(solicitud.getLugarParteTiempo());

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

        tvdniAdopt.setText(solicitud.getAdoptanteUser().getDni());
        tvNombreAdopt.setText(solicitud.getAdoptanteUser().getNombre());
        Glide.with(this).load(solicitud.getAdoptanteUser().getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivAdopt);

        if(!solicitud.getEstado().equals("Pendiente de aprobación")){
            llResponseInfo.setVisibility(View.VISIBLE);
            llButtons.setVisibility(View.GONE);
            horaRespNano = (Integer) intent.getSerializableExtra("horaRespNano");
            horaRespSec = (Long) intent.getSerializableExtra("horaRespSec");
            if(horaRespNano!=null && horaRespSec!=null){
                String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                tvFechaResponse.setText("Respondida "+fechaResp);
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
                    mapView = findViewById(R.id.mvAsistenteDetalleSolicitudesMap);
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
            llButtons.setVisibility(View.VISIBLE);
            btnAccept.setOnClickListener(view -> {
                Intent reservasIntent = new Intent(view.getContext(), AsistenteAcceptSoliActivity.class);
                reservasIntent.putExtra("solicitudes",solicitud);
                acceptlauncher.launch(reservasIntent);
            });
            btnReject.setOnClickListener(view -> {
                Intent reservasIntent = new Intent(view.getContext(), AsistenteRejectSoliActivity.class);
                reservasIntent.putExtra("solicitudes",solicitud);
                rejectlauncher.launch(reservasIntent);
            });
        }
    }

    ActivityResultLauncher<Intent> acceptlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent data = result.getData();
                horaRespNano = data.getIntExtra("horaRespNano",0);
                horaRespSec = data.getLongExtra("horaRespSec",0);
                latitude = data.getDoubleExtra("lati", 0);
                longitud = data.getDoubleExtra("long",0);
                String lugarRecojo = data.getStringExtra("nombreLugarRecojo");
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                llResponseInfo.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);
                if(horaRespNano!=null && horaRespSec!=null){
                    String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                    tvFechaResponse.setText(fechaResp);
                }
                tvNombreTI.setText(fUser.getDisplayName());
                Glide.with(this).load(fUser.getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);

                llAcceptInfo.setVisibility(View.VISIBLE);
                llRejectInfo.setVisibility(View.GONE);
                tvEstado.setText("Solicitud aceptada");
                tvEstado.setTextColor(getResources().getColor(R.color.green));

                LatLng coord = new LatLng(latitude,longitud);
                if(latitude != null && longitud != null){
                    mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(Style.OUTDOORS, style -> {
                        this.mapboxMap = mapboxMap;
                        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitud)).setTitle(lugarRecojo));
                        symbolManager = new SymbolManager(mapView, mapboxMap, style, null);
                        SymbolOptions symbolOptions = new SymbolOptions()
                                .withLatLng(coord)
                                .withIconImage(ICON_ID)
                                .withIconSize(0.5f);
                        symbolManager.create(symbolOptions);
                        mapboxMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(latitude,longitud)).zoom(15).build());
                    }));
                }
                nombreLugar.setText(lugarRecojo);
            });

    ActivityResultLauncher<Intent> rejectlauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent data = result.getData();
                horaRespNano = data.getIntExtra("horaRespNano",0);
                horaRespSec = data.getLongExtra("horaRespSec",0);
                String motivoRechazo = data.getStringExtra("motivoRechazo");
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                llResponseInfo.setVisibility(View.VISIBLE);
                llButtons.setVisibility(View.GONE);
                if(horaRespNano!=null && horaRespSec!=null){
                    String fechaResp = df.format(new Timestamp(horaRespSec,horaRespNano).toDate());
                    tvFechaResponse.setText(fechaResp);
                }
                tvNombreTI.setText(fUser.getDisplayName());
                Glide.with(this).load(fUser.getPhotoUrl()).placeholder(R.drawable.avatar_placeholder).into(ivti);

                llRejectInfo.setVisibility(View.VISIBLE);
                llAcceptInfo.setVisibility(View.GONE);
                tvEstado.setText("Solicitud rechazada");
                tvEstado.setTextColor(getResources().getColor(R.color.red));
                tvMotivoRechazo.setText(motivoRechazo);
            });

    public void backButton(View view){
        onBackPressed();
    }
}