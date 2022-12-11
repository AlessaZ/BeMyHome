package com.pucp.bemyhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pucp.bemyhome.Admin.AdminHomeActivity;
import com.pucp.bemyhome.Adoptante.AdoptanteHomeActivity;
import com.pucp.bemyhome.Asistente.AsistenteHomeActivity;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.UnloginUser.OboardingActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    CollectionReference usersRef;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser  == null || currentUser.isAnonymous()){
            Intent intentAnonymus = new Intent(MainActivity.this, OboardingActivity.class);
            startActivity(intentAnonymus);
            finish();
        }else{
            accesoEnBaseARol(currentUser);
        }
    }

    public void accesoEnBaseARol(FirebaseUser firebaseUser){
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        usersRef.document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    FirebaseAuth.getInstance().signOut();
                    Intent intentAnonymus = new Intent(MainActivity.this, OboardingActivity.class);
                    startActivity(intentAnonymus);
                    finish();
                }
                Intent intentPermisos;
                Gson gson = new Gson();
                User user = documentSnapshot.toObject(User.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", gson.toJson(user));
                editor.apply();
                switch (Objects.requireNonNull(documentSnapshot.getString("permisos"))){
                    case "Adoptante":
                        Toast.makeText(MainActivity.this, "Hola Adoptante", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, AdoptanteHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "Admin":
                        Toast.makeText(MainActivity.this, "Hola Admin", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, AdminHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "Asistente":
                        Toast.makeText(MainActivity.this, "Hola Asistente", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(MainActivity.this, AsistenteHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseAuth.getInstance().signOut();
                Intent intentAnonymus = new Intent(MainActivity.this, OboardingActivity.class);
                startActivity(intentAnonymus);
                finish();
            }
        });;
    }

}