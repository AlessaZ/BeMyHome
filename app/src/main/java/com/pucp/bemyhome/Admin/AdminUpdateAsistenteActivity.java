package com.pucp.bemyhome.Admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.util.HashMap;
import java.util.Map;

public class AdminUpdateAsistenteActivity extends AppCompatActivity {

    boolean isBusy;
    User userAsist;
    EditText etNombre;
    EditText etCorreo;
    EditText etDNI;
    ImageView ivPfp;
    CollectionReference usersRef;
    Button btnActualizar;
    Button btnEliminar;
    ImageButton btnBack;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_asistente);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("userAsist")){
            finish();
            return;
        }
        userAsist = (User) intent.getSerializableExtra("userAsist");

        ivPfp = findViewById(R.id.ivAdminUpdateAsistentePfp);
        etNombre = findViewById(R.id.etAdminUpdateAsistenteNombre);
        etDNI = findViewById(R.id.etAdminUpdateAsistenteDNI);
        etCorreo = findViewById(R.id.etAdminUpdateAsistenteCorreo);
        btnActualizar = findViewById(R.id.btnAdminUpdateAsistenteActualizar);
        btnEliminar = findViewById(R.id.btnAdminUpdateAsistenteEliminar);
        btnBack = findViewById(R.id.ibUpdateUserAsistBack);
        progressBar = findViewById(R.id.pbAdminUpdateAsistente);

        usersRef = FirebaseFirestore.getInstance().collection("users");
        Glide.with(this).load(userAsist.getAvatarUrl()).into(ivPfp);
        etNombre.setText(userAsist.getNombre());
        etCorreo.setText(userAsist.getCorreo());
        etDNI.setText(userAsist.getDNI());

    }

    public void actualizarTi(View view) {
        boolean isInvalid = false;
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String dni = etDNI.getText().toString().trim();
        if(nombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(nombre.length()>30){
            etNombre.setError("No puede tener más de 30 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.PHONE.matcher(dni).matches() || dni.length()!=8){
            etDNI.setError("Ingrese un DNI válido");
            etDNI.requestFocus();
            isInvalid = true;
        }

        if (isInvalid) return;

        Map<String, Object> updates = new HashMap<>();
        if (!userAsist.getNombre().equals(nombre)) {
            updates.put("nombre", nombre);
        }

        if (!userAsist.getDNI().equals(dni)) {
            updates.put("dni", dni);
        }

        if (!userAsist.getCorreo().equals(correo)) {
            updates.put("correo", correo);
        }

        if (updates.isEmpty()) return;
        Log.d("msg", "not empty");
        //Verifica que el codigo y correo sean únicos
        mostrarCargando();
        if (!userAsist.getDNI().equals(dni)) {
            usersRef.whereEqualTo("dni",dni).count().get(AggregateSource.SERVER).addOnSuccessListener(aggregateQuerySnapshot -> {
                if (aggregateQuerySnapshot.getCount()>0){
                    ocultarCargando();
                    etDNI.setError("Ya existe una cuenta con este DNI");
                    etDNI.requestFocus();
                    return;
                }
                actualizarUserFirebase(updates);
            }).addOnFailureListener(e -> ocultarCargando());
        } else {
            actualizarUserFirebase(updates);
        }

    }

    public void actualizarUserFirebase(Map<String, Object> updates) {
        usersRef.document(userAsist.getUid()).update(updates).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(AdminUpdateAsistenteActivity.this, "Se ha actualizado el usuario", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(AdminUpdateAsistenteActivity.this, "Hubo un error al actualizar los datos", Toast.LENGTH_SHORT).show();
        });
    }

    public void showAlert(View view){
        MaterialAlertDialogBuilder alertEliminar = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme_Center);
        alertEliminar.setIcon(R.drawable.ic_user_remove);
        alertEliminar.setTitle("Eliminar asistente");
        alertEliminar.setMessage("¿Está seguro que desea eliminar este usuario asistente? Esta acción es irreversible");
        alertEliminar.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                usersRef.document(userAsist.getUid()).delete().addOnSuccessListener( unused -> {
                    Toast.makeText(AdminUpdateAsistenteActivity.this, "Se ha eliminado a "+userAsist.getNombre() , Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Log.d("msg", "error", e);
                    Toast.makeText(AdminUpdateAsistenteActivity.this, "Revisa tu conexión a internet", Toast.LENGTH_SHORT).show();
                });
                Log.d("msgAlert","ELIMINAR");
            }

        });
        alertEliminar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("msgAlert","CANCELAR");
            }
        });
        alertEliminar.show();
    }

    public void backButton(View view) {
        onBackPressed();
    }

    public void mostrarCargando(){
        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        btnActualizar.setClickable(false);
        btnEliminar.setClickable(false);
        btnBack.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        progressBar.setVisibility(View.GONE);
        btnActualizar.setClickable(true);
        btnEliminar.setClickable(true);
        btnBack.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }

}