package com.pucp.bemyhome.Adoptante;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pucp.bemyhome.ChooseAvatarActivity;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;
import com.pucp.bemyhome.UnloginUser.LoginActivity;

import java.util.HashMap;
import java.util.Map;

public class AdoptanteProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText etNombre;
    EditText etCorreo;
    TextView tvdni;
    TextView tvNombre;
    String  userName;
    String userCorreo;
    ImageView ivAdoptante;
    Uri userFoto;
    CollectionReference user;
    User userAdopt;
    ImageView ivPfp;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptante_profile);
        setBottomNavigationView();
        etNombre =  findViewById(R.id.etAdoptanteProfileNombre);
        etCorreo = findViewById(R.id.etAdoptanteProfileCorreo);
        tvdni = findViewById(R.id.tvAdoptanteProfileDNI);
        tvNombre = findViewById(R.id.tvAdoptanteProfileNombre);
        ivAdoptante = findViewById(R.id.ivAdoptanteProfilePfp);
        user = FirebaseFirestore.getInstance().collection("users");
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userAdopt = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvdni.setText(userAdopt.getDNI());
        tvNombre.setText(userAdopt.getNombre());

        userName= userAdopt.getNombre();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etNombre.setText(userName);
        etCorreo.setText(userCorreo);
        Glide.with(this).load(userAdopt.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivAdoptante);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdoptanteProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdoptProfile);
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
                        startActivity(new Intent(getApplicationContext(), AdoptanteAdopcionesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdoptProfile:
                        return true;
                }
                return false;
            }
        });
    }

    public void actualizarPerfil(View view){

        String newNombre = etNombre.getText().toString().trim();
        String newCorreo = etCorreo.getText().toString().trim();

        boolean isInvalid = false;

        if(newNombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(newNombre.length()>30){
            etNombre.setError("No puede tener más de 30 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newCorreo).matches()){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }
        Map<String, Object> updates = new HashMap<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (isInvalid || firebaseUser == null) return;

        if (!userAdopt.getNombre().equals(newNombre)) {
            updates.put("nombre", newNombre);
            tvNombre.setText(newNombre);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNombre).build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        }
        if(!userAdopt.getCorreo().equals(newCorreo)) {
            updates.put("correo", newCorreo);
            firebaseUser.updateEmail(newCorreo);
        }

        user.document(firebaseUser.getUid()).update(updates).addOnSuccessListener(unused -> {
            Gson gson = new Gson();
            userAdopt.setNombre(newNombre);
            userAdopt.setCorreo(newCorreo);
            sharedPreferences.edit().putString("user", gson.toJson(userAdopt)).apply();
        }).addOnFailureListener(e -> {
            Toast.makeText(AdoptanteProfileActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToChooseAvatar(View view) {
        Intent caIntent = new Intent(AdoptanteProfileActivity.this, ChooseAvatarActivity.class);
        caIntent.putExtra("avatarUrl", userAdopt.getAvatarUrl());
        launcherChooseAvatar.launch(caIntent);
    }

    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AdoptanteProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().remove("user").apply();
        startActivity(new Intent(AdoptanteProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(AdoptanteProfileActivity.this);
        finish();
    }

    ActivityResultLauncher<Intent> launcherChooseAvatar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Gson gson = new Gson();
                    userAdopt.setAvatarUrl(data.getStringExtra("avatarUrl"));
                    Glide.with(AdoptanteProfileActivity.this).load(userAdopt.getAvatarUrl()).into(ivAdoptante);
                    sharedPreferences.edit().putString("user", gson.toJson(userAdopt)).apply();
                }
            }
    );

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdoptanteHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

}