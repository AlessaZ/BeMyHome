package com.pucp.bemyhome.Admin;

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
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

public class AdminHomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    User user;
    BottomNavigationView bottomNavigationView;
    TextView tvNombre;
    ImageView ivPfp;
    TextView totalAdopciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        setBottomNavigationView();

        tvNombre = findViewById(R.id.tvAdminHomeNombre);
        ivPfp = findViewById(R.id.ivAdminHomePfp);
        totalAdopciones = findViewById(R.id.tvAdminHomeCantAdopc);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).into(ivPfp);
        FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("estado", "Solicitud aceptada").count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                totalAdopciones.setText(aggregateQuerySnapshot.getCount() + "");
            }
        });
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminHomeAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
                        return true;
                    case R.id.bottomNavMenuAdminAsistentes:
                        startActivity(new Intent(getApplicationContext(), AdminGestionAsistentesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminProfile:
                        startActivity(new Intent(getApplicationContext(), AdminProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    public void anadirAsist(View view){
        Intent intent = new Intent(view.getContext(),AdminNewAsistenteActivity.class);
        view.getContext().startActivity(intent);
    }

    public void listAdopt(View view){
        Intent intent = new Intent(view.getContext(),AdminAdoptantesListActivity.class);
        view.getContext().startActivity(intent);
    }

}