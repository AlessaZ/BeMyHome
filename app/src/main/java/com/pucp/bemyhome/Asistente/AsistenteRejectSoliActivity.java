package com.pucp.bemyhome.Asistente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AsistenteRejectSoliActivity extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("EEE dd MMM yyy", Locale.getDefault());
    EditText etMotivoRechazo;
    Button btnReject;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_reject_soli);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        Solicitud reservas = (Solicitud) intent.getSerializableExtra("solicitudes");
        user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> updates = new HashMap<>();
        etMotivoRechazo = findViewById(R.id.etAsistenteRejectSoliMotivoRechazo);
        btnReject = findViewById(R.id.btnAsistenteRejectSoliRechazarSoli);
        btnReject.setOnClickListener(v -> {

            String motivoRechazo = etMotivoRechazo.getText().toString().trim();
            if (motivoRechazo.isEmpty()) {
                etMotivoRechazo.setError("No puede estar vacío");
                etMotivoRechazo.requestFocus();
                return;
            }
            if (motivoRechazo.length()>500) {
                etMotivoRechazo.setError("No puede ser mayor a 500 caracteres");
                etMotivoRechazo.requestFocus();
                return;
            }

            updates.put("asistUser.avatarUrl",user.getPhotoUrl().toString());
            updates.put("asistUser.nombre",user.getDisplayName());
            updates.put("asistUser.uid",user.getUid());
            updates.put("estado","Solicitud rechazada");
            Timestamp horaRespuesta = Timestamp.now();
            updates.put("horaRespuesta", horaRespuesta);
            updates.put("motivoRechazo",etMotivoRechazo.getText().toString().trim());
            FirebaseFirestore.getInstance().collection("solicitudes").document(reservas.getKey()).update(updates).addOnSuccessListener(unused -> {
                Toast.makeText(AsistenteRejectSoliActivity.this, "Se realizó la act con éxito", Toast.LENGTH_SHORT).show();
                Intent intents = new Intent();
                intents.putExtra("horaRespNano", horaRespuesta.getNanoseconds());
                intents.putExtra("horaRespSec", horaRespuesta.getSeconds());
                intents.putExtra("motivoRechazo", motivoRechazo);
                setResult(RESULT_OK, intents);
                finish();
            }).addOnFailureListener(e->{
                Toast.makeText(AsistenteRejectSoliActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
            });
        });
    }

    public void backButton(View view){
        onBackPressed();
    }
}