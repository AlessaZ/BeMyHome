package com.pucp.bemyhome.Asistente;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pucp.bemyhome.Adapters.ImageUploadAdapter;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AsistenteUpdateDeviceFormActivity extends AppCompatActivity {

    RecyclerView rvFotos;
    GridLayout glFotos;
    Pet pet;
    EditText etNombre;
    RadioGroup rgGenero;
    EditText etEdad;
    RadioGroup rgTamanio;
    EditText etPeso;
    EditText etContexto;
    RadioGroup rgActividad;
    EditText etCaracteristicas;
    ChipGroup cgCaracteristicas;
    TextView tvTamanio;
    TextView tvCategoria;
    ImageView ivCategoria;

    ProgressBar pbPhoto;
    ProgressBar pbLoading;

    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnAnadir;
    boolean isBusy = false;
    private Uri cameraUri;
    private List<String> listFotos = new ArrayList<>();
    private List<String> listCaracteristicas = new ArrayList<>();
    ImageUploadAdapter fotosAdapter;

    ActivityResultLauncher<Intent> launcherPhotoDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    compressImageAndUpload(uri,50);
                } else {
                    Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_update_device_form);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        pet = (Pet) intent.getSerializableExtra("mascota");

        rvFotos = findViewById(R.id.rvAsistenteUpdateDeviceFormFotos);
        glFotos = findViewById(R.id.glAsistenteUpdateDeviceForm);
        etNombre = findViewById(R.id.etAsistenteUpdateDeviceFormNombre);
        rgGenero = findViewById(R.id.rgAsistenteUpdateDeviceFormGenero);
        etEdad = findViewById(R.id.etAsistenteUpdateDeviceFormEdad);
        rgTamanio = findViewById(R.id.rgAsistenteUpdateDeviceFormTamanio);
        etPeso = findViewById(R.id.etAsistenteUpdateDeviceFormPeso);
        etContexto = findViewById(R.id.etAsistenteUpdateDeviceFormSituacion);
        rgActividad = findViewById(R.id.rgAsistenteUpdateDeviceFormActividad);
        etCaracteristicas = findViewById(R.id.etAsistenteUpdateDeviceFormCaracteristicas);
        cgCaracteristicas = findViewById(R.id.cgAsistenteUpdateDeviceFormCaracteristicas);
        pbPhoto = findViewById(R.id.pbAsistenteUpdateDeviceFormPhoto);
        pbLoading = findViewById(R.id.pbAsistenteUpdateDeviceFormLoading);
        tvCategoria = findViewById(R.id.tvAsistenteUpdateDeviceFormCategory);
        ivCategoria = findViewById(R.id.ivAsistenteUpdateDeviceFormCategory);

        btnBack = findViewById(R.id.ibAsistenteUpdateDeviceFormBack);
        btnPhotoAttach = findViewById(R.id.ibAsistenteUpdateDeviceFormPhotoAttach);
        btnPhotoCam = findViewById(R.id.ibAsistenteUpdateDeviceFormPhotoCam);
        btnAnadir = findViewById(R.id.btnAsistenteUpdateDeviceFormAct);

        listCaracteristicas = pet.getCaracteristicas();

        for (String carac:listCaracteristicas) {
            Chip chip = new Chip(AsistenteUpdateDeviceFormActivity.this);
            ChipDrawable drawable = ChipDrawable.createFromAttributes(AsistenteUpdateDeviceFormActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
            chip.setChipDrawable(drawable);
            chip.setCheckable(false);
            chip.setClickable(false);
            chip.setPadding(60,10,60,10);
            chip.setText(carac);
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cgCaracteristicas.removeView(chip);
                    listCaracteristicas.remove(chip.getText().toString());
                }
            });
            cgCaracteristicas.addView(chip);
        }

        etCaracteristicas.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    Chip chip = new Chip(AsistenteUpdateDeviceFormActivity.this);
                    String programa = etCaracteristicas.getText().toString().trim();
                    if(!listCaracteristicas.contains(programa.toLowerCase(Locale.ROOT))){
                        ChipDrawable drawable = ChipDrawable.createFromAttributes(AsistenteUpdateDeviceFormActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
                        chip.setChipDrawable(drawable);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        chip.setPadding(60,10,60,10);
                        chip.setText(programa);
                        chip.setOnCloseIconClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cgCaracteristicas.removeView(chip);
                                listCaracteristicas.remove(chip.getText().toString());
                            }
                        });
                        cgCaracteristicas.addView(chip);
                        listCaracteristicas.add(programa);
                        etCaracteristicas.setText("");
                        return true;
                    }else{
                        etCaracteristicas.setError("La característica ya se ha añadido");
                        etCaracteristicas.clearFocus();
                    }
                }
                return false;
            }
        });

        //Setea adapters
        listFotos = pet.getFotosUrl();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        fotosAdapter = new ImageUploadAdapter(this, listFotos);

        //Implementa adapters
        rvFotos.setAdapter(fotosAdapter);
        rvFotos.setLayoutManager(gridLayoutManager);
        evaluarEmpty();

        tvCategoria.setText(pet.getCategoria());
        switch (pet.getSearchCategoria()){
            case "Gato":
                ivCategoria.setImageResource(R.drawable.cat);
                break;
            case "Perro":
                ivCategoria.setImageResource(R.drawable.dog);
                break;
            case "Ave":
                ivCategoria.setImageResource(R.drawable.bird);
                break;
            case "Conejo":
                ivCategoria.setImageResource(R.drawable.rabbit);
                break;
            case "Otros":
                ivCategoria.setImageResource(R.drawable.others);
        }

        etNombre.setText(pet.getNombre());
        etEdad.setText(pet.getEdad());
        etPeso.setText(pet.getPeso());
        etContexto.setText(pet.getDescripcion());
        RadioButton rbTamanio;
        RadioButton rbGenero;
        RadioButton rbActividad;
        switch (pet.getTamanio()){
            case "Pequeño":
                rbTamanio = (RadioButton) rgTamanio.getChildAt(0);
                rbTamanio.setChecked(true);
                break;
            case "Mediano":
                rbTamanio = (RadioButton) rgTamanio.getChildAt(1);;
                rbTamanio.setChecked(true);
                break;
            case "Grande":
                rbTamanio = (RadioButton) rgTamanio.getChildAt(2);
                rbTamanio.setChecked(true);
                break;
        }
        switch (pet.getGenero()){
            case "Macho":
                rbGenero = (RadioButton) rgGenero.getChildAt(0);
                rbGenero.setChecked(true);
                break;
            case "Hembra":
                rbGenero = (RadioButton) rgGenero.getChildAt(1);;
                rbGenero.setChecked(true);
                break;
        }
        switch (pet.getNivelActividad()){
            case "Bajo":
                rbActividad = (RadioButton) rgActividad.getChildAt(0);
                rbActividad.setChecked(true);
                break;
            case "Medio":
                rbActividad = (RadioButton) rgActividad.getChildAt(1);;
                rbActividad.setChecked(true);
                break;
            case "Alto":
                rbActividad = (RadioButton) rgActividad.getChildAt(2);
                rbActividad.setChecked(true);
                break;
            case "Muy alto":
                rbActividad = (RadioButton) rgActividad.getChildAt(3);
                rbActividad.setChecked(true);
                break;
        }
    }

    public void actualizarPet(View view){
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        String nombre = etNombre.getText().toString().trim();
        RadioButton rbGenero=(RadioButton)findViewById(rgGenero.getCheckedRadioButtonId());
        String genero = rbGenero.getText().toString().trim();
        String descripcion = etContexto.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();
        RadioButton rbTamanio=(RadioButton)findViewById(rgTamanio.getCheckedRadioButtonId());
        String tamanio = rbTamanio.getText().toString().trim();
        RadioButton rbNivelActividad=(RadioButton)findViewById(rgActividad.getCheckedRadioButtonId());
        String nivelActividad = rbNivelActividad.getText().toString().trim();
        String peso = etPeso.getText().toString().trim();

        if(nombre.isEmpty()){
            etNombre.setError("El nombre no puede estar vacía");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(!nombre.matches("[a-zA-Z]*")){
            etNombre.setError("El nombre solo puede contener letras");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(nombre.length()>20){
            etNombre.setError("La marca puede contener hasta 20 caracteres");
            etNombre.requestFocus();
            isInvalid = true;
        }

        if(genero.isEmpty()){
            isInvalid = true;
        }

        if(tamanio.isEmpty()){
            tvTamanio.setError("El tamanio no puede estar vacío");
            isInvalid = true;
        }

        if(nivelActividad.isEmpty()){
            isInvalid = true;
        }

        if(descripcion.isEmpty()){
            etContexto.setError("El contexto no puede estar vacío");
            etContexto.requestFocus();
            isInvalid = true;
        }

        if(descripcion.length()>255){
            etContexto.setError("La descripción puede contener hasta 255 caracteres");
            etContexto.requestFocus();
            isInvalid = true;
        }

        if(edad.isEmpty()){
            etEdad.setError("La edad no puede estar vacía");
            etEdad.requestFocus();
            isInvalid = true;
        }

        if(peso.isEmpty()){
            etPeso.setError("La edad no puede estar vacía");
            etPeso.requestFocus();
            isInvalid = true;
        }

        if(listCaracteristicas.isEmpty()){
            etCaracteristicas.setError("El campo características no puede estar vacío");
            etCaracteristicas.requestFocus();
            isInvalid = true;
        }

        if(listCaracteristicas.size()>5){
            etCaracteristicas.setError("Las características de la mascota no pueden ser más de 5");
            etCaracteristicas.requestFocus();
            isInvalid = true;
        }

        if(listFotos.size()<3 || listFotos.size()>6){
            rvFotos.requestFocus();
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Se deben subir entre 3 a 6 fotos", Toast.LENGTH_SHORT).show();
            isInvalid = true;
        }

        if(isInvalid) return;

        mostrarCargando();

        pet.setNombre(nombre);
        pet.setGenero(genero);
        pet.setEdad(edad);
        pet.setTamanio(tamanio);
        pet.setPeso(peso);
        pet.setDescripcion(descripcion);
        pet.setFotosUrl(listFotos);
        pet.setNivelActividad(nivelActividad);
        pet.setCaracteristicas(listCaracteristicas);
        pet.setSearchKeywords(generateKeywords(genero + " " +edad+""+nombre));
        FirebaseFirestore.getInstance().collection("pets").document(pet.getKey()).set(pet).addOnSuccessListener(unused -> {
            ocultarCargando();
            Intent intent = new Intent();
            intent.putExtra("mascota", pet);
            setResult(RESULT_OK, intent);
            finish();
        }).addOnFailureListener(e -> {
            ocultarCargando();
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "No se pudo actualizar el dispositivo", Toast.LENGTH_SHORT).show();
        });
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

    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
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
        StorageReference photoChild = FirebaseStorage.getInstance().getReference().child("pets/" + "photo_" + Timestamp.now().getSeconds() + ".jpg");
        pbPhoto.setVisibility(View.VISIBLE);
        photoChild.putBytes(imageBytes).addOnSuccessListener(taskSnapshot -> {
            pbPhoto.setVisibility(View.GONE);
            photoChild.getDownloadUrl().addOnSuccessListener(uri -> {
                listFotos.add(uri.toString());
                fotosAdapter.notifyDataSetChanged();
                evaluarEmpty();
            }).addOnFailureListener(e ->{
                Log.d("msg-test", "error",e);
                Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(AsistenteUpdateDeviceFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
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

    public void removerFoto(int position){
        listFotos.remove(position);
        fotosAdapter.notifyDataSetChanged();
        evaluarEmpty();
    }

    public void evaluarEmpty(){
        if (listFotos.size()>0){
            rvFotos.setVisibility(View.VISIBLE);
            glFotos.setVisibility(View.GONE);
        }else{
            rvFotos.setVisibility(View.GONE);
            glFotos.setVisibility(View.VISIBLE);
        }
    }

    public void mostrarCargando(){
        isBusy = true;
        pbLoading.setVisibility(View.VISIBLE);
        btnPhotoAttach.setClickable(false);
        btnPhotoCam.setClickable(false);
        btnBack.setClickable(false);
        btnAnadir.setClickable(false);
    }

    public void ocultarCargando(){
        isBusy = false;
        pbLoading.setVisibility(View.GONE);
        btnPhotoAttach.setClickable(true);
        btnPhotoCam.setClickable(true);
        btnBack.setClickable(true);
        btnAnadir.setClickable(true);
    }

    @Override
    public void onBackPressed() {
        if (!isBusy){
            super.onBackPressed();
        }
    }

}