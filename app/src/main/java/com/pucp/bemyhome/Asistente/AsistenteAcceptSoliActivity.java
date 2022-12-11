package com.pucp.bemyhome.Asistente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Transaction;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AsistenteAcceptSoliActivity extends AppCompatActivity {

    private static final String ICON_ID = "optionsMarker";
    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    double lugarRecojoLat;
    double lugarRecojoLong;
    Button btnAccept;
    private MapView mapView;
    private MapboxMap mapboxMapG;
    private SymbolManager symbolManager;
    private FillManager fillManager;
    FirebaseUser user;
    String lugarRecojo;
    ArrayList<LatLng> listMarkers = new ArrayList<LatLng>();
    LatLng vet1 = new LatLng(-12.064741590825314, -77.11124146922614);
    LatLng vet2= new LatLng(-11.984149549000444, -77.07005141957364);
    LatLng vet3 = new LatLng(-12.041233499500793, -77.07244532651576);
    LatLng vet4 = new LatLng(-12.00799057793402, -77.06936109836981);
    LatLng vet5 = new LatLng(-12.050302364650477, -77.0964781576717);
    LatLng vet6 = new LatLng(-12.072795592832819, -77.06282457117406);
    ArrayList<String> listNamesMarkers = new ArrayList<String>();

    protected OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            mapboxMapG = mapboxMap;
            for (int i=0;i<listMarkers.size();i++){
                mapboxMapG.addMarker(new MarkerOptions().position(listMarkers.get(i)).title(listNamesMarkers.get(i)));
                mapboxMapG.moveCamera(CameraUpdateFactory.newLatLng(listMarkers.get(i)));
            }
            mapboxMapG.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    String markerTitle = marker.getTitle();
                    lugarRecojo = markerTitle;
                    lugarRecojoLat = marker.getPosition().getLatitude();
                    lugarRecojoLong = marker.getPosition().getLongitude();
                    return false;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_asistente_accept_soli);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Solicitud solicitudes = (Solicitud) intent.getSerializableExtra("solicitudes");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updates = new HashMap<>();
        btnAccept = findViewById(R.id.btnAsistenteAcceptSoliAceptarSoli);
        btnAccept.setOnClickListener(v -> {
            if(!lugarRecojo.isEmpty()){
                updates.put("asistUser.avatarUrl",user.getPhotoUrl().toString());
                updates.put("asistUser.nombre",user.getDisplayName());
                updates.put("asistUser.uid",user.getUid());
                updates.put("estado","Solicitud aceptada");
                Timestamp horaRespuesta = Timestamp.now();
                updates.put("horaRespuesta", horaRespuesta);
                updates.put("lugarRecojo", new GeoPoint(lugarRecojoLat,lugarRecojoLong));
                updates.put("nombreLugarRecojo",lugarRecojo);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference solicitudesRef = db.collection("solicitudes").document(solicitudes.getKey());
                final DocumentReference petRef = db.collection("pets").document(solicitudes.getPets().getUid());
                db.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(petRef);
                        Pet pet = snapshot.toObject(Pet.class);
                        if (pet.getAdoptado().equals("adoptado")) {
                            Toast.makeText(AsistenteAcceptSoliActivity.this, "La mascota ya fue adoptada", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                        Map<String, Object> petUpdates = new HashMap<>();
                        petUpdates.put("adoptado", "adoptado");
                        transaction.update(petRef, petUpdates);
                        transaction.update(solicitudesRef, updates);
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AsistenteAcceptSoliActivity.this, "Se realizó la actualización con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("horaRespNano", horaRespuesta.getNanoseconds());
                        intent.putExtra("horaRespSec", horaRespuesta.getSeconds());
                        intent.putExtra("lati", lugarRecojoLat);
                        intent.putExtra("long", lugarRecojoLong);
                        intent.putExtra("nombreLugarRecojo", lugarRecojo);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    Log.d("msg",e.getMessage());
                    Toast.makeText(AsistenteAcceptSoliActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
                });

        }else{
                Toast.makeText(AsistenteAcceptSoliActivity.this, "Debes seleccionar un lugar de recojo", Toast.LENGTH_LONG).show();
            }
        });

        listMarkers.add(vet1);
        listMarkers.add(vet2);
        listMarkers.add(vet3);
        listMarkers.add(vet4);
        listMarkers.add(vet5);
        listMarkers.add(vet6);
        listNamesMarkers.add("Av. Víctor Raúl Haya de la Torre 410, La Perla 07016");
        listNamesMarkers.add("Av. Carlos Izaguirre 937, Los Olivos 15301");
        listNamesMarkers.add("Av. Óscar R. Benavides 2505, Lima 15081");
        listNamesMarkers.add("Av. Germán Aguirre 701-739, San Martín de Porres 15103");
        listNamesMarkers.add("Av. Elmer Faucett 1809, Bellavista 07011");
        listNamesMarkers.add("Av Antonio José de Sucre 688, Pueblo Libre 15084");
        mapView.getMapAsync(onMapReadyCallback);
    }

    public void backButton(View view){
        onBackPressed();
    }
}