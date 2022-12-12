package com.pucp.bemyhome.UnloginUser;

import android.content.Intent;
import android.net.Uri;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;
import com.pucp.bemyhome.ScreenMessage;
import com.pucp.bemyhome.ScreenMessageActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    String[] AVATAR_URLS;
    FirebaseAuth firebaseAuth;
    CollectionReference usersRef;
    EditText etNombre;
    EditText etDNI;
    EditText etCorreo;
    EditText etContrasena;
    ProgressBar progressBar;
    Button btnLogin;
    Button btnRegistrar;
    EditText etTelefono;

    boolean isBusy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etNombre = findViewById(R.id.etRegisterNombre);
        etCorreo = findViewById(R.id.etRegisterCorreo);
        etDNI = findViewById(R.id.etRegisterDNI);
        etContrasena = findViewById(R.id.etRegisterContrasena);
        progressBar = findViewById(R.id.pbRegister);
        btnLogin = findViewById(R.id.btnRegisterGoToLogin);
        btnRegistrar = findViewById(R.id.btnRegisterRegistrar);
        etTelefono = findViewById(R.id.etRegisterTelf);
        firebaseAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseFirestore.getInstance().collection("users");
        AVATAR_URLS = getResources().getStringArray(R.array.avatars);
    }

    public void showHidePass(View view){
        if(etContrasena.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_disabled);
            //Show Password
            etContrasena.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_eye_open);
            //Hide Password
            etContrasena.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void registerUser(View view){
        boolean isInvalid = false;
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String DNI = etDNI.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String avatarUrl = AVATAR_URLS[new Random().nextInt(AVATAR_URLS.length)];
        String telefono = etTelefono.getText().toString().trim();

        if(nombre.isEmpty()){
            etNombre.setError("No puede estar vacío");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.PHONE.matcher(DNI).matches() || DNI.length()!=8){
            etDNI.setError("Ingrese un DNI válido");
            etDNI.requestFocus();
            isInvalid = true;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            etCorreo.setError("Ingrese un correo válido");
            etCorreo.requestFocus();
            isInvalid = true;
        }

        if(contrasena.length()<6){
            etContrasena.setError("Debe contener al menos 6 caracteres");
            etContrasena.requestFocus();
            isInvalid = true;
        }

        if(telefono.isEmpty()){
            etTelefono.setError("No puede estar vacío");
            etTelefono.requestFocus();
            isInvalid = true;
        }

        if(telefono.length()!=9){
            etTelefono.setError("No es un teléfono válido");
            etTelefono.requestFocus();
            isInvalid = true;
        }

        if(isInvalid) return;

        //Verifica que el DNI y correo sean únicos
        mostrarCargando();
        
        User user = new User(nombre,correo,DNI,"Adoptante",avatarUrl, generateKeywords(nombre + " " + DNI + " " + correo), telefono );
        createUser(user, contrasena);
    }

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

    public void createUser(User user, String contrasena){
        firebaseAuth.createUserWithEmailAndPassword(user.getCorreo(),contrasena)
                .addOnSuccessListener(authResult -> updateProfileFireauth(authResult, user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    etCorreo.setError("Verifica que el correo no esté en uso");
                    etCorreo.requestFocus();
                });
    }

    public void updateProfileFireauth(AuthResult authResult, User user){
        assert authResult.getUser()!=null;
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getNombre()).setPhotoUri(Uri.parse(user.getAvatarUrl())).build();

        authResult.getUser().updateProfile(userProfileChangeRequest)
                .addOnSuccessListener(unused -> addUserFirestore(authResult.getUser().getUid(),user))
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    Toast.makeText(RegisterActivity.this, "Ocurrió un error al crear el perfil", Toast.LENGTH_LONG).show();
                });
    }

    public void addUserFirestore(String uid, User user){
        usersRef.document(uid).set(user)
                .addOnSuccessListener(documentReference -> sendEmail())
                .addOnFailureListener(e -> {
                    ocultarCargando();
                    Log.d("msg",e.getMessage());
                    Toast.makeText(RegisterActivity.this, "No se pudo completar el registro", Toast.LENGTH_LONG).show();
                });

    }

    public void sendEmail(){
        assert firebaseAuth.getCurrentUser() !=null;
        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(unused -> {
            firebaseAuth.signOut();
            ocultarCargando();
            ScreenMessage screenMessage = new ScreenMessage(R.drawable.circle_check, R.drawable.registro_exitoso,
                    "Te has registrado en BeMyHome",
                    "Verifica tu correo para poder usar la aplicación",
                    "Iniciar Sesión",
                    false,LoginActivity.class);
            Intent successIntent = new Intent(RegisterActivity.this, ScreenMessageActivity.class);
            successIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            successIntent.putExtra("screenMessage", screenMessage);
            startActivity(successIntent);
            ActivityCompat.finishAffinity(RegisterActivity.this);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(RegisterActivity.this, "No pudimos enviar el correo de confirmación", Toast.LENGTH_LONG).show();
        });

    }

    public void mostrarCargando(){
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setClickable(false);
        btnRegistrar.setClickable(false);
    }

    public void ocultarCargando(){
        progressBar.setVisibility(View.GONE);
        btnLogin.setClickable(true);
        btnRegistrar.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }

    public void goToLogin(View view){
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        ActivityCompat.finishAffinity(RegisterActivity.this);
        finish();
    }
}