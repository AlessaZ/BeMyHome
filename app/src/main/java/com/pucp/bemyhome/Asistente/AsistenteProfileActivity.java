package com.pucp.bemyhome.Asistente;

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

public class AsistenteProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText etNombre;
    EditText etCorreo;
    TextView tvdni;
    TextView tvNombre;
    String  userName;
    String userCorreo;
    ImageView ivAsistente;
    Uri userFoto;
    CollectionReference user;
    User userAsist;
    ImageView ivPfp;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_profile);
        setBottomNavigationView();
        etNombre =  findViewById(R.id.etAsistProfileNombre);
        etCorreo = findViewById(R.id.etAsistProfileCorreo);
        tvdni = findViewById(R.id.tvAsistenteProfileDNI);
        tvNombre = findViewById(R.id.tvAsistenteProfileNombre);
        ivAsistente = findViewById(R.id.ivAdminProfilePfp);
        user = FirebaseFirestore.getInstance().collection("users");
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userAsist = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvdni.setText(userAsist.getDNI());
        tvNombre.setText(userAsist.getNombre());

        userName= userAsist.getNombre();
        userCorreo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        etNombre.setText(userName);
        etCorreo.setText(userCorreo);
        Glide.with(this).load(userAsist.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivAsistente);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAsistProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAsistProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAsistHome:
                        startActivity(new Intent(getApplicationContext(), AsistenteHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAsistMascotas:
                        startActivity(new Intent(getApplicationContext(), AsistenteMacotasActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAsistProfile:
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

        if (!userAsist.getNombre().equals(newNombre)) {
            updates.put("nombre", newNombre);
            tvNombre.setText(newNombre);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNombre).build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        }
        if(!userAsist.getCorreo().equals(newCorreo)) {
            updates.put("correo", newCorreo);
            firebaseUser.updateEmail(newCorreo);
        }

        user.document(firebaseUser.getUid()).update(updates).addOnSuccessListener(unused -> {
            Gson gson = new Gson();
            userAsist.setNombre(newNombre);
            userAsist.setCorreo(newCorreo);
            sharedPreferences.edit().putString("user", gson.toJson(userAsist)).apply();
        }).addOnFailureListener(e -> {
            Toast.makeText(AsistenteProfileActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToChooseAvatar(View view) {
        Intent caIntent = new Intent(AsistenteProfileActivity.this, ChooseAvatarActivity.class);
        caIntent.putExtra("avatarUrl", userAsist.getAvatarUrl());
        launcherChooseAvatar.launch(caIntent);
    }

    public void cerrarSesionAsist(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AsistenteProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().remove("user").apply();
        startActivity(new Intent(AsistenteProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(AsistenteProfileActivity.this);
        finish();
    }

    ActivityResultLauncher<Intent> launcherChooseAvatar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Gson gson = new Gson();
                    userAsist.setAvatarUrl(data.getStringExtra("avatarUrl"));
                    Glide.with(AsistenteProfileActivity.this).load(userAsist.getAvatarUrl()).into(ivAsistente);
                    sharedPreferences.edit().putString("user", gson.toJson(userAsist)).apply();
                }
            }
    );

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AsistenteHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

}