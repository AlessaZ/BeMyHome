package com.pucp.bemyhome.Asistente;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.time.LocalTime;

public class AsistenteHomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    User user;
    BottomNavigationView bottomNavigationView;
    TextView tvNombre;
    ImageView ivPfp;
    TextView tvSaludo;
    TextView tvSolicitudesRespondidas;
    ShapeableImageView ivFotitoAsist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_home);
        setBottomNavigationView();
        tvNombre = findViewById(R.id.tvAsistenteHomeNombre);
        ivPfp = findViewById(R.id.ivAsistenteHomePfp);
        tvSaludo = findViewById(R.id.tvAsistenteHomeSaludo);
        tvSolicitudesRespondidas = findViewById(R.id.tvAsistenteHomeCantReservas);
        ivFotitoAsist = findViewById(R.id.ivAsistenteHomeFotitoAsist);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        FirebaseUser userG = FirebaseAuth.getInstance().getCurrentUser();
        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).into(ivPfp);
        Glide.with(this).load(user.getAvatarUrl()).into(ivFotitoAsist);
        FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("asistUser.uid", userG.getUid()).count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                tvSolicitudesRespondidas.setText(aggregateQuerySnapshot.getCount() + "");
            }
        });

    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAsistHomeAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAsistHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAsistHome:
                        return true;
                    case R.id.bottomNavMenuAsistMascotas:
                        startActivity(new Intent(getApplicationContext(), AsistenteMacotasActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAsistProfile:
                        startActivity(new Intent(getApplicationContext(), AsistenteProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    public void solicitudesPendientes(View view){
        Intent intent = new Intent(view.getContext(), AsistenteSolicitudesActivity.class);
        intent.putExtra("typeSoli","pendiente");
        view.getContext().startActivity(intent);
    }

    public void historialSolicitudes(View view){
        Intent intent = new Intent(view.getContext(), AsistenteSolicitudesActivity.class);
        intent.putExtra("typeSoli","historial");
        view.getContext().startActivity(intent);
    }

    public void mostrarSaludo(){
        String saludo = "Buenas noches,";
        LocalTime localTime = LocalTime.now();
        if(localTime.getHour()>=6 && localTime.getHour()<12){
            saludo = "Buenos días,";
        } else if(localTime.getHour() >= 12 && localTime.getHour()<19){
            saludo = "Buenas tardes,";
        }
        tvSaludo.setText(saludo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarSaludo();
    }

}