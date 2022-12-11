package com.pucp.bemyhome.Adoptante;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.R;

import java.util.ArrayList;

public class AdoptanteDetalleMascotaActivity extends AppCompatActivity {

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
    Button btnAdoptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptante_detalle_mascota);
        Intent intent = getIntent();
        if(intent == null){
            finish();
            return;
        }
        Pet pet= (Pet) intent.getSerializableExtra("mascota");

        tvNombre = findViewById(R.id.tvAdoptDetalleNombre);
        tvEdad = findViewById(R.id.tvCAdopDetalleMascotaEdadcita);
        tvPeso = findViewById(R.id.tvCAdopDetalleMascotaPesito);
        tvSexo = findViewById(R.id.tvCAdopDetalleMascotaGenerito);
        tvTamanio = findViewById(R.id.tvCAdopDetalleMascotaTamanito);
        tvContexto = findViewById(R.id.tvAdopDetalleMascotaDescripcion);
        tvCaracteristicas = findViewById(R.id.tvAdopDetalleMascotaCaracteristicas);
        tvNivelActividad = findViewById(R.id.tvAdopDetalleMascotaNivelActividad);
        tvCategoria = findViewById(R.id.tvAdoptKindPet);
        ivCategoria = findViewById(R.id.ivAdoptDetalleCategoria);
        imgSlider = findViewById(R.id.isAdopDisp);

        tvNombre.setText(pet.getNombre());
        tvEdad.setText(pet.getEdad());
        tvPeso.setText(pet.getPeso());
        tvSexo.setText(pet.getGenero());
        tvTamanio.setText(pet.getTamanio());
        tvContexto.setText(pet.getDescripcion());
        tvCategoria.setText(pet.getCategoria());

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

        btnAdoptar = findViewById(R.id.btnAdoptar);
        btnAdoptar.setOnClickListener(view -> {
            Intent deviceIntent = new Intent(view.getContext(),AdoptanteFormSolicitudActivity.class);
            deviceIntent.putExtra("pet", pet);
            view.getContext().startActivity(deviceIntent);
        });
    }

    public void backButton(View view){
        onBackPressed();
    }
}