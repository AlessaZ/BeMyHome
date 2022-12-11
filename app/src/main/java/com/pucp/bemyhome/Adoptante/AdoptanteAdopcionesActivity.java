package com.pucp.bemyhome.Adoptante;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.pucp.bemyhome.R;

public class AdoptanteAdopcionesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptante_solicitudes);
        setBottomNavigationView();

    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdoptanteAdopcionesAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdoptSolicitud);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdoptHome:
                        startActivity(new Intent(getApplicationContext(), AdoptanteHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdoptSolicitud:
                        return true;
                    case R.id.bottomNavMenuAdoptProfile:
                        startActivity(new Intent(getApplicationContext(), AdoptanteProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    public void historial(View view){
        Intent reservasIntent = new Intent(view.getContext(),AdoptanteAdopcionesRecycleActivity.class);
        reservasIntent.putExtra("typeSolicitudes", "historial");
        view.getContext().startActivity(reservasIntent);
    }

    public void enProceso(View view){
        Intent reservasIntent = new Intent(view.getContext(),AdoptanteAdopcionesRecycleActivity.class);
        reservasIntent.putExtra("typeSolicitudes", "enProceso");
        view.getContext().startActivity(reservasIntent);
    }

    public void solicitudesFinalizadas(View view){
        Intent reservasIntent = new Intent(view.getContext(),AdoptanteAdopcionesRecycleActivity.class);
        reservasIntent.putExtra("typeSolicitudes", "finalizadas");
        view.getContext().startActivity(reservasIntent);
    }

}