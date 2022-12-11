package com.pucp.bemyhome.Adoptante;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.io.ByteArrayOutputStream;

public class AdoptanteFormSolicitudActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Pet pet;
    ShapeableImageView ivDni;
    private Uri cameraUri;
    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnSolicitar;
    ProgressBar pbPhoto;
    ProgressBar pbLoading;
    FirebaseUser user;
    boolean isBusy = false;
    User userG;
    String fotoUrl = "";
    EditText etMotivo;
    RadioGroup rgDomicilio;
    RadioGroup rgPropioAlquilado;
    RadioGroup rgJardinPatio;
    EditText etCantPersonas;
    RadioGroup rgAlergico;
    EditText etCantMascotas;
    RadioGroup rgVacunas;
    EditText etTiempoDedicar;
    EditText etTiempoPasear;
    RadioGroup rgLugarTiempo;

    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri,50);
                } else {
                    Toast.makeText(AdoptanteFormSolicitudActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(AdoptanteFormSolicitudActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptante_form_solicitud);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        userG = gson.fromJson(sharedPreferences.getString("user",""), User.class);


        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }

        pet = (Pet) intent.getSerializableExtra("pet");
        user = FirebaseAuth.getInstance().getCurrentUser();

        etMotivo = findViewById(R.id.etAdoptanteFormMotivo);
        rgDomicilio = findViewById(R.id.rgAdoptanteFormDomicilio);
        rgPropioAlquilado = findViewById(R.id.rgAdoptanteFormAlquiladoOno);
        rgJardinPatio = findViewById(R.id.rgAdoptanteFormPatioOno);
        etCantPersonas = findViewById(R.id.etAdoptanteFormCantidadPersonas);
        rgAlergico = findViewById(R.id.rgAdoptanteFormAlergico);
        etCantMascotas = findViewById(R.id.etAdoptanteFormCantidadMascotas);
        rgVacunas = findViewById(R.id.rgAdoptanteFormVacunas);
        etTiempoDedicar = findViewById(R.id.etAdoptanteFormDedicacion);
        etTiempoPasear = findViewById(R.id.etAdoptanteFormPaseo);
        rgLugarTiempo = findViewById(R.id.rgAdoptanteFormLugar);
        pbPhoto = findViewById(R.id.pbAdoptanteDNIPhoto);
        pbLoading = findViewById(R.id.pbAdoptanteFormLoading);
        btnBack = findViewById(R.id.ibAdoptanteFormBack);
        btnPhotoAttach = findViewById(R.id.ibAdoptanteFormPhotoAttach);
        btnPhotoCam = findViewById(R.id.ibAdoptanteFormPhotoCam);
        btnSolicitar = findViewById(R.id.btnAdoptanteFormEnviarSoli);
        ivDni = findViewById(R.id.ivAdoptanteFormDNI);

    }

    public void realizarSolicitud(View view){

        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        String motivo = etMotivo.getText().toString().trim();
        RadioButton radioDomButton=(RadioButton)findViewById(rgDomicilio.getCheckedRadioButtonId());
        String domicilio = radioDomButton.getText().toString().trim();
        RadioButton radioPoAButton=(RadioButton)findViewById(rgPropioAlquilado.getCheckedRadioButtonId());
        String propioAlquilado = radioPoAButton.getText().toString().trim();
        RadioButton radioPoJButton=(RadioButton)findViewById(rgJardinPatio.getCheckedRadioButtonId());
        String patioJardin = radioPoJButton.getText().toString().trim();
        String cantPersonas = etCantPersonas.getText().toString().trim();
        RadioButton radioAlergButton=(RadioButton)findViewById(rgAlergico.getCheckedRadioButtonId());
        String alergico  = radioAlergButton.getText().toString().trim();
        String cantMascotas = etCantMascotas.getText().toString().trim();
        RadioButton radioVacButton=(RadioButton)findViewById(rgVacunas.getCheckedRadioButtonId());
        String vacunas = radioVacButton.getText().toString().trim();
        String tiempoDedicar = etTiempoDedicar.getText().toString().trim();
        String tiempoPasear = etTiempoPasear.getText().toString().trim();
        RadioButton radioLugarParteButton=(RadioButton)findViewById(rgLugarTiempo.getCheckedRadioButtonId());
        String lugarMayorParte = radioLugarParteButton.getText().toString().trim();
        String dni = fotoUrl;
        Toast.makeText(AdoptanteFormSolicitudActivity.this, domicilio, Toast.LENGTH_SHORT).show();

        if(motivo.isEmpty()){
            etMotivo.setError("El motivo no puede estar vacío");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(motivo.length()>255){
            etMotivo.setError("El motivo puede contener hasta 255 caracteres");
            etMotivo.requestFocus();
            isInvalid = true;
        }

        if(domicilio.isEmpty()){
            isInvalid = true;
        }

        if(propioAlquilado.isEmpty()){
            isInvalid = true;
        }

        if(patioJardin.isEmpty()){
            isInvalid = true;
        }

        if(alergico.isEmpty()){
            isInvalid = true;
        }

        if(lugarMayorParte.isEmpty()){
            isInvalid = true;
        }

        if(vacunas.isEmpty()){
            isInvalid = true;
        }

        if(tiempoDedicar.isEmpty()){
            etTiempoDedicar.setError("El tiempo a dedicar no puede estar vacío");
            etTiempoDedicar.requestFocus();
            isInvalid = true;
        }

        if(cantPersonas.isEmpty()){
            etCantPersonas.setError("La cantidad de personas no puede estar vacía");
            etCantMascotas.requestFocus();
            isInvalid = true;
        }

        if(cantMascotas.isEmpty()){
            etCantMascotas.setError("La cantidad de mascotas no puede estar vacía");
            etCantMascotas.requestFocus();
            isInvalid = true;
        }

        if(tiempoPasear.isEmpty()){
            etTiempoPasear.setError("El tiempo de paseo no puede estar vacío");
            etTiempoPasear.requestFocus();
            isInvalid = true;
        }


        if(fotoUrl.isEmpty()){
            isInvalid = true;
        }


        if(isInvalid) return;

        mostrarCargando();

        crearSolicitudFirestore(new Solicitud(new Solicitud.AdoptanteUser(userG.getNombre(), user.getUid(), user.getPhotoUrl().toString(), userG.getDNI()), new Solicitud.AsistenteUser(), new Solicitud.Pets(pet.getNombre(), pet.getGenero(), pet.getEdad(),pet.getFotosUrl().get(0), pet.getCategoria(), pet.getKey()), motivo,domicilio, cantMascotas, cantPersonas, tiempoDedicar, tiempoPasear,dni,propioAlquilado,lugarMayorParte,patioJardin,null,"","","Pendiente de aprobación",alergico,vacunas,Timestamp.now(),null));
    }

    public void compressImageAndUpload(Uri uri, int quality){
        try{
            Bitmap originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG,quality,stream);
            subirImagenAFirebase(stream.toByteArray());
        }catch (Exception e){
            Log.d("msg","error",e);
        }
    }

    public void crearSolicitudFirestore(Solicitud solicitud){
        FirebaseFirestore.getInstance().collection("solicitudes").add(solicitud).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Se realizó la solicitud con éxito", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e->{
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
        });
    }


    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            launcherPhotoCamera.launch(cameraIntent);
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void subirImagenAFirebase(byte[] imageBytes) {
        StorageReference photoChild = FirebaseStorage.getInstance().getReference().child("dni/" + user.getUid() +"/"+"photo_" + Timestamp.now().getSeconds() + ".jpg");
        pbPhoto.setVisibility(View.VISIBLE);
        photoChild.putBytes(imageBytes).addOnSuccessListener(taskSnapshot -> {
            pbPhoto.setVisibility(View.GONE);
            photoChild.getDownloadUrl().addOnSuccessListener(uri -> {
                fotoUrl = uri.toString();
                Glide.with(AdoptanteFormSolicitudActivity.this).load(fotoUrl).into(ivDni);
                ivDni.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }).addOnFailureListener(e ->{
                Log.d("msg-test", "error",e);
                Toast.makeText(AdoptanteFormSolicitudActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(AdoptanteFormSolicitudActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                long bytesTransferred = snapshot.getBytesTransferred();
                long totalByteCount = snapshot.getTotalByteCount();
                double progreso = (100.0 * bytesTransferred) / totalByteCount;
                Long round = Math.round(progreso);
                pbPhoto.setProgress(round.intValue());
            }
        });
    }

    public void mostrarCargando(){
        isBusy = true;
        pbLoading.setVisibility(View.VISIBLE);
        btnPhotoAttach.setClickable(false);
        btnPhotoCam.setClickable(false);
        btnBack.setClickable(false);
        btnSolicitar.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        pbLoading.setVisibility(View.GONE);
        btnPhotoAttach.setClickable(true);
        btnPhotoCam.setClickable(true);
        btnBack.setClickable(true);
        btnSolicitar.setClickable(true);
    }


}