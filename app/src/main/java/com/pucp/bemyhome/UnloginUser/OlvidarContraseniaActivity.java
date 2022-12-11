package com.pucp.bemyhome.UnloginUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.pucp.bemyhome.R;
import com.pucp.bemyhome.ScreenMessage;
import com.pucp.bemyhome.ScreenMessageActivity;

public class OlvidarContraseniaActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText etCorreo;
    ProgressBar progressBar;
    Button btnSend;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_olvidar_contrasenia);
        //Setea los EditText, ProgressBar y Button
        etCorreo = findViewById(R.id.etForgotPasswordCorreo);
        progressBar = findViewById(R.id.pbForgotPassword);
        btnSend = findViewById(R.id.btnForgotPasswordSend);
        btnRegister = findViewById(R.id.btnForgotPasswordGoToRegister);
        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void enviarEmail(View view){
        String correo = etCorreo.getText().toString().trim();

        if(correo.isEmpty()){
            etCorreo.setError("Debes ingresar tu correo");
            etCorreo.requestFocus();
            return;
        }

        if(Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            mostrarCargando();
            firebaseSendMail(correo);
        }else{
            etCorreo.setError("No es un correo válido");
            etCorreo.requestFocus();
        }
    }

    public void firebaseSendMail(String correo){
        firebaseAuth.sendPasswordResetEmail(correo).addOnSuccessListener(unused -> {
            ocultarCargando();
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_check, R.drawable.revisa_correo,
                    "Revisa tu bandeja de entrada",
                    "Enviamos un correo para que recuperes tu cuenta",
                    "Iniciar Sesión",
                    false,LoginActivity.class);
            Intent successIntent = new Intent(OlvidarContraseniaActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(OlvidarContraseniaActivity.this);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            etCorreo.setError("Verifica que el correo sea correcto");
            etCorreo.requestFocus();
        });
    }

    public void goToRegisterActivity(View view){
        Intent registerIntent = new Intent(OlvidarContraseniaActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
        ActivityCompat.finishAffinity(OlvidarContraseniaActivity.this);
        finish();
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setClickable(false);
        btnRegister.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnSend.setClickable(true);
        btnRegister.setClickable(true);
    }

}