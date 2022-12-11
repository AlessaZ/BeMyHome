package com.pucp.bemyhome.UnloginUser;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.pucp.bemyhome.Admin.AdminHomeActivity;
import com.pucp.bemyhome.Adoptante.AdoptanteAdopcionesRecycleActivity;
import com.pucp.bemyhome.Adoptante.AdoptanteHomeActivity;
import com.pucp.bemyhome.Asistente.AsistenteHomeActivity;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    final String CHANNEL_ID = "CocoChannel";
    FirebaseAuth firebaseAuth;
    CollectionReference usersRef;
    EditText etCorreo;
    EditText etContrasena;
    ProgressBar progressBar;
    Button btnLogin;
    Button btnForgotPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etCorreo = findViewById(R.id.etLoginCorreo);
        etContrasena = findViewById(R.id.etLoginContrasena);
        progressBar = findViewById(R.id.pbLogin);
        btnLogin = findViewById(R.id.btnLoginIngresar);
        btnForgotPassword = findViewById(R.id.btnLoginForgotPassword);
        btnRegister = findViewById(R.id.btnLoginGoToRegister);
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    public void showHidePass(View view){
        if(etContrasena.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_disabled);
            etContrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_open);
            etContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void logIn(View view){
        Boolean isInvalid = false;
        String correo;
        String correoOCodigo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if(correoOCodigo.isEmpty()){
            etCorreo.setError("Debes ingresar tu correo o DNI");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(contrasena.isEmpty()){
            etContrasena.setError("Debes ingresar tu contraseña");
            etContrasena.requestFocus();
            isInvalid = true;
        }

        if (isInvalid) return;

        if(Patterns.EMAIL_ADDRESS.matcher(correoOCodigo).matches()){
            correo = correoOCodigo;
            mostrarCargando();
            firebaseSignIn(correo, contrasena);
        }else{
            etCorreo.setError("No es un correo válido");
            etCorreo.requestFocus();
        }
    }

    private void firebaseSignIn(String correo, String contrasena){
        firebaseAuth.signInWithEmailAndPassword(correo,contrasena).addOnSuccessListener(authResult -> {

            assert authResult.getUser()!=null;
            accesoEnBaseARol(authResult.getUser());

        }).addOnFailureListener(e -> {
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show();
        });
    }

    public void accesoEnBaseARol(FirebaseUser firebaseUser){
        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        usersRef.document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ocultarCargando();
                if (!documentSnapshot.exists()) return;
                Intent intentPermisos;
                Gson gson = new Gson();
                User user = documentSnapshot.toObject(User.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", gson.toJson(user));
                editor.apply();
                switch (Objects.requireNonNull(documentSnapshot.getString("permisos"))){
                    case "Adoptante":
                        if(firebaseUser.isEmailVerified()){
                            Toast.makeText(LoginActivity.this, "Hola Cliente", Toast.LENGTH_SHORT).show();
                            addListener();
                            intentPermisos  = new Intent(LoginActivity.this, AdoptanteHomeActivity.class);
                            startActivity(intentPermisos);
                            finish();
                        }else{
                            Toast.makeText(LoginActivity.this, "Anonimus", Toast.LENGTH_SHORT).show();
                            Intent intentNoVerificado = new Intent(LoginActivity.this, NoVerificadoActivity.class);
                            startActivity(intentNoVerificado);
                        }
                        break;
                    case "Admin":
                        Toast.makeText(LoginActivity.this, "Hola Admin", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(LoginActivity.this, AdminHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                    case "Asistente":
                        Toast.makeText(LoginActivity.this, "Hola Asistente", Toast.LENGTH_SHORT).show();
                        intentPermisos  = new Intent(LoginActivity.this, AsistenteHomeActivity.class);
                        startActivity(intentPermisos);
                        finish();
                        break;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ocultarCargando();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginActivity.this, "No se ha podido iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void irOlivdarContraseniaActivity(View view){
        Intent fpintent = new Intent(LoginActivity.this,OlvidarContraseniaActivity.class);
        startActivity(fpintent);
        overridePendingTransition(0,0);
    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setClickable(false);
        btnRegister.setClickable(false);
        btnForgotPassword.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnLogin.setClickable(true);
        btnRegister.setClickable(true);
        btnForgotPassword.setClickable(true);
    }

    public void irRegistrar(View view){
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    public void addListener() {
        FirebaseFirestore.getInstance().collection("solicitudes")
                .whereEqualTo("adoptanteUser.uid", FirebaseAuth.getInstance().getUid())
                .whereGreaterThanOrEqualTo("horaRespuesta", Timestamp.now())
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        if (e.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            return;
                        }
                        Log.w("msg", "listen:error", e);
                        return;
                    }
                    int i = 1;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        snapshots.getDocuments();
                        if (dc.getType() == DocumentChange.Type.ADDED){
                            Log.d("msg", dc.getDocument().toString());
                            String nombre = dc.getDocument().getString("pets");
                            Log.wtf("msg",nombre);
                            String estado = dc.getDocument().getString("estado");
                            Log.wtf("msg",estado);
                            if (estado == null || nombre == null) return;
                            String typeSolici;
                            String titulo;
                            String msg;
                            if (estado.equals("Solicitud rechazada")) {
                                titulo = "Tu solicitud ha sido rechazada";
                                msg = "No hemos podido aprobar tu solicitud de adopción";
                                typeSolici = "historial";
                                notificarSolicitud(typeSolici, titulo, msg, i);
                            } else if (estado.equals("Solicitud aceptada")) {
                                titulo = "Tu solicitud ha sido aprobada";
                                msg = "Se aprobó tu solicitud de adopción para " + nombre ;
                                typeSolici = "finalizadas";
                                notificarSolicitud(typeSolici, titulo, msg, i);
                            }
                        }
                        i++;
                    }

                });
    }


    public void notificarSolicitud(String solicitudesType, String titulo, String mensajeStr, int id){
        Intent intent = new Intent(this, AdoptanteAdopcionesRecycleActivity.class);
        intent.putExtra("typeSolicitudes", solicitudesType);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(LoginActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setOnlyAlertOnce(true)
                .setContentTitle(titulo)
                .setContentText(mensajeStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id, builder.build());
    }
}