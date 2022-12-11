package com.pucp.bemyhome.UnloginUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.pucp.bemyhome.R;
import com.pucp.bemyhome.ScreenMessage;
import com.pucp.bemyhome.ScreenMessageActivity;

public class NoVerificadoActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    Button btnResend;
    Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_no_verificado);
        progressBar = findViewById(R.id.pbNonVerified);
        btnResend = findViewById(R.id.btnNonVerifiedResend);
        btnGoBack = findViewById(R.id.btnNonVerifiedGoBack);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void volverAEnviarCorreo(View view){
        assert firebaseAuth.getCurrentUser() != null;
        mostrarCargando();
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> {
            firebaseAuth.signOut();
            progressBar.setVisibility(View.GONE);
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_check, R.drawable.revisa_correo,
                    "Hemos vuelto a enviar el correo de verificación",
                    "Verifica tu correo para poder usar la aplicación",
                    "Iniciar Sesión",
                    false,LoginActivity.class);
            Intent successIntent = new Intent(NoVerificadoActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(NoVerificadoActivity.this);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(NoVerificadoActivity.this, "No pudimos enviar el correo de confirmación", Toast.LENGTH_LONG).show();
        });
    }

    public void regresar(View view){
        firebaseAuth.signOut();
        finish();
    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnGoBack.setClickable(false);
        btnResend.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnGoBack.setClickable(true);
        btnResend.setClickable(true);
    }

}