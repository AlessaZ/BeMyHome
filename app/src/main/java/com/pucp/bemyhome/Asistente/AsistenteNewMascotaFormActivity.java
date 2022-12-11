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
import com.pucp.bemyhome.Adapters.ImageSelectorAdapter;
import com.pucp.bemyhome.Adapters.ImageUploadAdapter;
import com.pucp.bemyhome.Details.ImageSelectorMargin;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AsistenteNewMascotaFormActivity extends AppCompatActivity {

    final int IMAGE_SELECTOR_MARGIN = 8;
    final List<Integer> CATEGORY_IMAGES = Arrays.asList(R.drawable.cat_unselected, R.drawable.dog_unselected, R.drawable.bird_unselected, R.drawable.rabbit_unselected, R.drawable.others_unselected);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.kind_pet_cat, R.string.kind_pet_dog, R.string.kind_pet_bird, R.string.kind_pet_rabbit, R.string.kind_pet_others);


    RecyclerView categorySelector;
    RecyclerView rvFotos;
    GridLayout glFotos;

    EditText etNombre;
    RadioGroup rgGenero;
    EditText etEdad;
    RadioGroup rgTamanio;
    EditText etPeso;
    EditText etContexto;
    RadioGroup rgActividad;
    EditText etCaracteristicas;
    ChipGroup cgCaracteristicas;
    EditText etOtros;
    TextView tvTamanio;
    ProgressBar pbPhoto;
    ProgressBar pbLoading;

    private ImageButton btnBack;
    private ImageButton btnPhotoAttach;
    private ImageButton btnPhotoCam;
    private Button btnAnadir;
    boolean isBusy = false;

    ImageSelectorAdapter selectorAdapter;
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
                    Toast.makeText(AsistenteNewMascotaFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPhotoCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    compressImageAndUpload(cameraUri,25);
                } else {
                    Toast.makeText(AsistenteNewMascotaFormActivity.this, "Debe seleccionar un archivo", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_new_mascota_form);

        categorySelector = findViewById(R.id.rvAsistenteNewMascotaFormPetSelector);
        rvFotos = findViewById(R.id.rvAsistenteNewMascotaFormFotos);
        glFotos = findViewById(R.id.glAsistenteNewMascotaForm);
        etNombre = findViewById(R.id.etAsistenteNewMascotaFormNombre);
        rgGenero = findViewById(R.id.rgAsistenteNewMascotaFormGenero);
        etEdad = findViewById(R.id.etAsistenteNewMascotaFormEdad);
        rgTamanio = findViewById(R.id.rgAsistenteNewMascotaFormTamanio);
        etPeso = findViewById(R.id.etAsistenteNewMascotaFormPeso);
        etContexto = findViewById(R.id.etAsistenteNewMascotaFormSituacion);
        rgActividad = findViewById(R.id.rgAsistenteNewMascotaFormActividad);
        etCaracteristicas = findViewById(R.id.etAsistenteNewMascotaFormCaracteristicas);
        cgCaracteristicas = findViewById(R.id.cgAsistenteNewMascotaFormCaracteristicas);
        etOtros = findViewById(R.id.etAsistenteNewMascotaFormOtrosPet);
        pbPhoto = findViewById(R.id.pbAsistenteNewMascotaFormPhoto);
        pbLoading = findViewById(R.id.pbetAsistenteNewMascotaFormLoading);

        btnBack = findViewById(R.id.ibAsistenteNewMascotaFormBack);
        btnPhotoAttach = findViewById(R.id.ibAsistenteNewMascotaFormPhotoAttach);
        btnPhotoCam = findViewById(R.id.ibAsistenteNewMascotaFormPhotoCam);
        btnAnadir = findViewById(R.id.btnAsistenteNewMascotaFormAnadir);
        tvTamanio = findViewById(R.id.tvAsisteNewFormTamnioTitle);

        etCaracteristicas.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    Chip chip = new Chip(AsistenteNewMascotaFormActivity.this);
                    String programa = etCaracteristicas.getText().toString().trim();
                    if(!listCaracteristicas.contains(programa.toLowerCase(Locale.ROOT))){
                        ChipDrawable drawable = ChipDrawable.createFromAttributes(AsistenteNewMascotaFormActivity.this,null,0, com.denzcoskun.imageslider.R.style.Widget_MaterialComponents_Chip_Entry);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        fotosAdapter = new ImageUploadAdapter(this, listFotos);

        Log.wtf("msg" ,"antes de image selector");
        selectorAdapter = new ImageSelectorAdapter(this, CATEGORY_IMAGES, CATEGORY_TEXTS);
        selectorAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categorySelector.getChildAdapterPosition(view);
                if (position>=0 && position<CATEGORY_IMAGES.size()){
                    selectorAdapter.setSelectedItem(position);
                    selectorAdapter.notifyDataSetChanged();
                    //Caso otros
                    if(position == CATEGORY_IMAGES.size()-1){
                        etOtros.setVisibility(View.VISIBLE);
                    }else{
                        etOtros.setVisibility(View.GONE);
                    }
                }
            }
        });
        ImageSelectorMargin imageSelectorMargin = new ImageSelectorMargin(3,IMAGE_SELECTOR_MARGIN);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 7 is the sum of items in one repeated section
                switch (position % 5) {
                    // first three items span 3 columns each
                    case 0:
                    case 1:
                    case 2:
                        return 2;
                    // next two items span 2 columns each
                    case 3:
                    case 4:
                        return 3;
                }
                throw new IllegalStateException("internal error");
            }
        });

        //Implementa adapters
        rvFotos.setAdapter(fotosAdapter);
        rvFotos.setLayoutManager(gridLayoutManager);
        categorySelector.setLayoutManager(layoutManager);
        Log.wtf("msg" ,"antes de setAdapter");
        categorySelector.setAdapter(selectorAdapter);
        categorySelector.addItemDecoration(imageSelectorMargin);
        evaluarEmpty();
        Log.wtf("msg" ,"al final");
    }

    public void anadirDispositivo(View view){
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInvalid = false;
        int selectedCategory = ((ImageSelectorAdapter) categorySelector.getAdapter()).getSelectedItem();
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
        String searchCategoria = getResources().getString(CATEGORY_TEXTS.get(selectedCategory)); //Search categoria será el campo por el que se hará la búsqueda mientras que categoría podrá ser cualquier string
        String searchGenero = rbGenero.getText().toString().trim();
        String categoria;
        String otrosCategoria = etOtros.getText().toString().trim();

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
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Se deben subir entre 3 a 6 fotos", Toast.LENGTH_SHORT).show();
            isInvalid = true;
        }

        if (selectedCategory == CATEGORY_TEXTS.size()-1){
            if(otrosCategoria.isEmpty()){
                etOtros.setError("La categoría no puede estar vacía");
                etOtros.requestFocus();
                isInvalid = true;
            }

            if(otrosCategoria.length()>20){
                etOtros.setError("La categoría puede contener hasta 20 caracteres");
                etOtros.requestFocus();
                isInvalid = true;
            }

            categoria = otrosCategoria;
        }else{
            categoria = searchCategoria;
        }

        if(isInvalid) return;

        mostrarCargando();

        crearMascotaFirestore(new Pet(genero, edad, categoria, peso, tamanio, nombre,listFotos, descripcion, listCaracteristicas, nivelActividad,generateKeywords(genero + " " +edad+ " " +nombre), searchCategoria, searchGenero, ""));
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

    public void crearMascotaFirestore(Pet pet){
        FirebaseFirestore.getInstance().collection("pets").add(pet).addOnSuccessListener(unused -> {
            ocultarCargando();
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Se añadió la mascota "+pet.getNombre(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }).addOnFailureListener(e->{
            ocultarCargando();
            Log.d("msg",e.getMessage());
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Ocurrió un error en el servidor", Toast.LENGTH_LONG).show();
        });
    }

    public void uploadPhotoFromDocument(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            launcherPhotoDocument.launch(intent);
        }
    }

    public void uploadPhotoFromCamera(View view) {
        if (pbPhoto.getVisibility()==View.VISIBLE){
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Espera a que se termine de subir la foto", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AsistenteNewMascotaFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.d("msg-test", "error",e);
            pbPhoto.setVisibility(View.GONE);
            Toast.makeText(AsistenteNewMascotaFormActivity.this, "Hubo un error al subir la imagen", Toast.LENGTH_SHORT).show();
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