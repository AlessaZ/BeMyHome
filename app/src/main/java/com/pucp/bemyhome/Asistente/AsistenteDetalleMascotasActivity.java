package com.pucp.bemyhome.Asistente;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.R;

import java.util.ArrayList;

public class AsistenteDetalleMascotasActivity extends AppCompatActivity {

    TextView tvNombre;
    TextView tvEdad;
    TextView tvPeso;
    TextView tvSexo;
    TextView tvTamanio;
    TextView tvContexto;
    TextView tvCaracteristicas;
    TextView tvCategoria;
    ImageView ivCategoria;
    ImageSlider imgSlider;
    TextView tvNivelActividad;
    Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_detalle_mascotas);

        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        pet= (Pet) intent.getSerializableExtra("mascota");

        tvNombre = findViewById(R.id.tvAsistenteDetalleMascotasNombre);
        tvEdad = findViewById(R.id.tvAsistenteDetalleMascotasEdadcita);
        tvPeso = findViewById(R.id.tvAsistenteDetalleMascotasPesito);
        tvSexo = findViewById(R.id.tvAsistenteDetalleMascotasGenerito);
        tvTamanio = findViewById(R.id.tvAsistenteDetalleMascotasTamanito);
        tvContexto = findViewById(R.id.tvAsistenteDetalleMascotasDescripcion);
        tvCaracteristicas = findViewById(R.id.tvAsistenteDetalleMascotasCaracteristicas);
        tvNivelActividad = findViewById(R.id.tvAsistenteDetalleMascotasNivelActividad);
        tvCategoria = findViewById(R.id.tvAsistenteDetalleMascotasDispCategoria);
        ivCategoria = findViewById(R.id.ivAsistenteDetalleMascotasCategoria);
        imgSlider = findViewById(R.id.isAsistenteDetalleMascotas);

        tvNombre.setText(pet.getNombre());
        tvEdad.setText(pet.getEdad()+" meses");
        tvPeso.setText(pet.getPeso() +" kg");
        tvSexo.setText(pet.getGenero());
        tvTamanio.setText(pet.getTamanio());
        tvContexto.setText(pet.getDescripcion());
        tvCategoria.setText(pet.getCategoria());
        tvNivelActividad.setText(pet.getNivelActividad());

        String caract = "";
        for(int i=0; i<pet.getCaracteristicas().size();i++){
            if( i==0){
                caract = pet.getCaracteristicas().get(0);
            }else{
                caract = caract + "," + pet.getCaracteristicas().get(i);
            }
        }
        tvCaracteristicas.setText(caract);

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        for (String url : pet.getFotosUrl()){
            slideModels.add(new SlideModel(url, ScaleTypes.CENTER_CROP));
        }

        imgSlider.setImageList(slideModels);

        switch (pet.getCategoria().trim()){
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

    }

    public void showAlert(View view){
        MaterialAlertDialogBuilder alertEliminar = new MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme_Center);
        alertEliminar.setIcon(R.drawable.ic_trash);
        alertEliminar.setTitle("Borrar mascota");
        alertEliminar.setMessage("¿Está seguro que desea borrar esta mascota? Esta acción es irreversible");
        alertEliminar.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("pets").document(pet.getKey()).delete().addOnSuccessListener(unused -> {
                    Toast.makeText(AsistenteDetalleMascotasActivity.this, "Se ha eliminado la mascota", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(AsistenteDetalleMascotasActivity.this, "No se ha podido eliminar el dispositivo", Toast.LENGTH_SHORT).show();
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

    public void goToUpdateMascota(View view) {
        Intent updateIntent = new Intent(AsistenteDetalleMascotasActivity.this, AsistenteUpdateDeviceFormActivity.class);
        updateIntent.putExtra("mascota", pet);
        updateActivityLauncher.launch(updateIntent);
    }

    ActivityResultLauncher<Intent> updateActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if(data == null || !data.hasExtra("mascota")) return;
                        pet  = (Pet) data.getSerializableExtra("mascota");
                        tvNombre.setText(pet.getNombre());
                        tvEdad.setText(pet.getEdad()+" meses");
                        tvPeso.setText(pet.getPeso() +" kg");
                        tvSexo.setText(pet.getGenero());
                        String caract = "";
                        for(int i=0; i<pet.getCaracteristicas().size();i++){
                            if( i==0){
                                caract = pet.getCaracteristicas().get(0);
                            }else{
                                caract = caract + "," + pet.getCaracteristicas().get(i);
                            }
                        }
                        tvCaracteristicas.setText(caract);
                        tvTamanio.setText(pet.getTamanio());
                        tvContexto.setText(pet.getDescripcion());
                        tvCategoria.setText(pet.getCategoria());
                        tvNivelActividad.setText(pet.getNivelActividad());

                        ArrayList<SlideModel> slideModels = new ArrayList<>();
                        for (String url : pet.getFotosUrl()){
                            slideModels.add(new SlideModel(url, ScaleTypes.CENTER_CROP));
                        }

                        imgSlider.setImageList(slideModels);
                        setResult(RESULT_OK);
                    }
                }
            });

    public void backButton(View view){
        onBackPressed();
    }
}