package com.pucp.bemyhome.Admin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AdminNewAsistenteActivity extends AppCompatActivity {

    String[] AVATAR_URLS;
    FirebaseAuth firebaseAuth;
    FirebaseAuth firebaseAuth2;
    CollectionReference usersRef;
    EditText etNombre;
    EditText etCorreo;
    EditText etDNI;
    ProgressBar progressBar;
    Button btnRegistrar;
    ImageButton btnBack;
    boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_asistente);

        etNombre = findViewById(R.id.etAdminNewAsistenteNombre);
        etCorreo = findViewById(R.id.etAdminNewAsistenteCorreo);
        etDNI = findViewById(R.id.etAdminNewAsistenteDNI);
        progressBar = findViewById(R.id.pbCreateUserAsist);
        btnRegistrar = findViewById(R.id.btnAdminNewAsistenteRegistrar);
        btnBack = findViewById(R.id.ibCreateUserTiBack);

        //Setea Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseOptions options = firebaseAuth.getApp().getOptions();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), options, "BeMyHome");
            firebaseAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("BeMyHome"));
        }
        usersRef = FirebaseFirestore.getInstance().collection("users");

        AVATAR_URLS = getResources().getStringArray(R.array.avatars);
    }

    public void registrarAsistente(View view){
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
            etDNI.setError("Ingrese un DNIválido");
            etDNI.requestFocus();
            isInvalid = true;
        }

        if(isInvalid) return;
        byte[] array = new byte[16];
        new Random().nextBytes(array);
        Random aleatorio = new Random();
        String avatarUrl = AVATAR_URLS[aleatorio.nextInt(AVATAR_URLS.length)];
        String contrasena = new String(array, Charset.forName("UTF-8"));

        //Verifica que el codigo y correo sean únicos
        mostrarCargando();
        usersRef.whereEqualTo("dni",dni).count().get(AggregateSource.SERVER).addOnSuccessListener(aggregateQuerySnapshot -> {
            if (aggregateQuerySnapshot.getCount()>0){
                ocultarCargando();
                etDNI.setError("Ya existe una cuenta con este dni");
                etDNI.requestFocus();
                return;
            }
            User user = new User(nombre,correo,dni,"Asistente",avatarUrl, generateKeywords(nombre + " " + dni + " "+ correo),"");
            crearUsuario(user, contrasena);
        }).addOnFailureListener(e -> ocultarCargando());
    }

    public void crearUsuario(User user, String contrasena){
        firebaseAuth2.createUserWithEmailAndPassword(user.getCorreo(),contrasena)
                .addOnSuccessListener(authResult -> actualizarPerfilFireauth(authResult, user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    etCorreo.setError("Verifica que el correo no esté en uso");
                    etCorreo.requestFocus();
                });
    };

    private List<String> generateKeywords(String inputString){
        inputString = inputString.toLowerCase();
        List<String> keywords = new ArrayList<>();

        List<String> words = Arrays.asList(inputString.split(" "));
        for (String word : words){
            String appendString = "";

            for (char c : inputString.toCharArray()){
                appendString+=c;
                keywords.add(appendString);
            }

            inputString = inputString.replace(word+" ","");
        }

        return keywords;
    }

    public void actualizarPerfilFireauth(AuthResult authResult, User user){
        assert authResult.getUser()!=null;
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getNombre()).setPhotoUri(Uri.parse(user.getAvatarUrl())).build();

        authResult.getUser().updateProfile(userProfileChangeRequest)
                .addOnSuccessListener(unused -> anadirUsuarioFirestore(authResult.getUser().getUid(),user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Toast.makeText(AdminNewAsistenteActivity.this, "Ocurrió un error al crear el perfil", Toast.LENGTH_LONG).show();
                });
    }

    public void anadirUsuarioFirestore(String uid, User user){
        usersRef.document(uid).set(user)
                .addOnSuccessListener(documentReference -> enviarCorreo(user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Toast.makeText(AdminNewAsistenteActivity.this, "No se pudo completar el registro", Toast.LENGTH_LONG).show();
                });
    }

    public void enviarCorreo(User user){
        assert firebaseAuth2.getCurrentUser() !=null;
        firebaseAuth2.sendPasswordResetEmail(user.getCorreo()).addOnSuccessListener(unused -> {
            firebaseAuth2.signOut();
            ocultarCargando();
            Toast.makeText(AdminNewAsistenteActivity.this, "Se ha creado el usuario "+user.getNombre(), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(AdminNewAsistenteActivity.this, "No pudimos enviar el correo de confirmación", Toast.LENGTH_LONG).show();
        });

    };

    public void mostrarCargando(){
        isBusy = true;
        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setClickable(false);
        btnBack.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        progressBar.setVisibility(View.GONE);
        btnRegistrar.setClickable(true);
        btnBack.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }

    public void backButton(View view){
        onBackPressed();
    }
}