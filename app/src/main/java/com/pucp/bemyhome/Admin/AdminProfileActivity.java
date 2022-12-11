package com.pucp.bemyhome.Admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AdminProfileActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    EditText etUpdateNombre;
    EditText etUpdateCorreo;
    TextView tvDNI;
    TextView tvNombre;
    CollectionReference userRef;
    ImageView ivPfp;
    User userAdmin;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);
        setBottomNavigationView();
        etUpdateNombre = findViewById(R.id.etAdminProfileNombre);
        etUpdateCorreo = findViewById(R.id.etAdminProfileCorreo);
        tvDNI = findViewById(R.id.tvAdminProfileDNI);
        tvNombre = findViewById(R.id.tvAdminProfileNombre);
        ivPfp = findViewById(R.id.ivAdminProfilePfp);
        userRef = FirebaseFirestore.getInstance().collection("users");

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userAdmin = gson.fromJson(sharedPreferences.getString("user",""),User.class);
        tvDNI.setText(userAdmin.getDNI());
        tvNombre.setText(userAdmin.getNombre());

        etUpdateNombre.setText(userAdmin.getNombre());
        etUpdateCorreo.setText(userAdmin.getCorreo());
        Glide.with(this).load(userAdmin.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminProfileAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminAsistentes:
                        startActivity(new Intent(getApplicationContext(), AdminGestionAsistentesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminProfile:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void actualizarPerfilAdmin(View view){

        String newNombre = etUpdateNombre.getText().toString().trim();
        String newCorreo = etUpdateCorreo.getText().toString().trim();

        boolean isInvalid = false;

        if(newNombre.isEmpty()){
            etUpdateNombre.setError("No puede estar vacío");
            etUpdateNombre.requestFocus();
            isInvalid = true;
        }

        if(newNombre.length()>30){
            etUpdateNombre.setError("No puede tener más de 30 caracteres");
            etUpdateNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(newCorreo).matches()){
            etUpdateCorreo.setError("Ingrese un correo válido");
            etUpdateCorreo.requestFocus();
            isInvalid = true;
        }
        Map<String, Object> updates = new HashMap<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (isInvalid || firebaseUser == null) return;

        if (!userAdmin.getNombre().equals(newNombre)) {
            updates.put("nombre", newNombre);
            tvNombre.setText(newNombre);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newNombre).build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        }
        if(!userAdmin.getCorreo().equals(newCorreo)) {
            updates.put("correo", newCorreo);
            firebaseUser.updateEmail(newCorreo);
        }

        userRef.document(firebaseUser.getUid()).update(updates).addOnSuccessListener(unused -> {
            Gson gson = new Gson();
            userAdmin.setNombre(newNombre);
            userAdmin.setCorreo(newCorreo);
            sharedPreferences.edit().putString("user", gson.toJson(userAdmin)).apply();
        }).addOnFailureListener(e -> {
            Toast.makeText(AdminProfileActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void goToChooseAvatar(View view) {
        Intent caIntent = new Intent(AdminProfileActivity.this, ChooseAvatarActivity.class);
        caIntent.putExtra("avatarUrl", userAdmin.getAvatarUrl());
        launcherChooseAvatar.launch(caIntent);
    }

    public void cerrarSesionAdmin(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(AdminProfileActivity.this, "Has cerrado sesión", Toast.LENGTH_SHORT).show();
        sharedPreferences.edit().remove("user").apply();
        startActivity(new Intent(AdminProfileActivity.this, LoginActivity.class));
        ActivityCompat.finishAffinity(AdminProfileActivity.this);
        finish();
    }

    ActivityResultLauncher<Intent> launcherChooseAvatar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;
                    Gson gson = new Gson();
                    userAdmin.setAvatarUrl(data.getStringExtra("avatarUrl"));
                    Glide.with(AdminProfileActivity.this).load(userAdmin.getAvatarUrl()).into(ivPfp);
                    sharedPreferences.edit().putString("user", gson.toJson(userAdmin)).apply();
                }
            }
    );
}