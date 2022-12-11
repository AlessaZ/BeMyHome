package com.pucp.bemyhome.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.pucp.bemyhome.Asistente.AsistenteDetalleMascotasActivity;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.R;

import java.util.Locale;

public class PetsCardAdapter extends FirestorePagingAdapter<Pet, PetsCardAdapter.PetViewHolder>  {
    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public PetsCardAdapter(@NonNull FirestorePagingOptions options, Class activity) {
        super(options);
        nextActivity = activity;
    }

    Class nextActivity;
    ActivityResultLauncher<Intent> crudResultLauncher;


    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pets, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull PetViewHolder holder, int position, @NonNull Pet model) {
        holder.tvNombre.setText(model.getNombre().toUpperCase(Locale.ROOT));
        holder.tvEdad.setText(model.getEdad() + " meses");
        holder.tvSexo.setText(model.getGenero() + ", ");
        holder.tvCategoria.setText(model.getCategoria());
        switch (model.getSearchCategoria()){
            case "Gato":
                holder.ivCategoria.setImageResource(R.drawable.cat);
                break;
            case "Perro":
                holder.ivCategoria.setImageResource(R.drawable.dog);
                break;
            case "Ave":
                holder.ivCategoria.setImageResource(R.drawable.bird);
                break;
            case "Conejo":
                holder.ivCategoria.setImageResource(R.drawable.rabbit);
                break;
            case "Otros":
                holder.ivCategoria.setImageResource(R.drawable.others);
        }
        holder.pet = model;
        holder.ivPetFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(holder.itemView.getContext()).load(model.getFotosUrl().get(0))
                .placeholder(AppCompatResources.getDrawable(holder.itemView.getContext(), R.drawable.image_pets_placeholder)).dontAnimate()
                .into(holder.ivPetFoto);
    }

    public class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPetFoto;
        ImageView ivCategoria;
        TextView tvNombre;
        TextView tvSexo;
        TextView tvCategoria;
        TextView tvEdad;
        TextView tvDetalle;
        TextView tvAdoptado;
        Pet pet;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetFoto = itemView.findViewById(R.id.ivCardPetsPhoto);
            ivCategoria = itemView.findViewById(R.id.ivCardPetsCategoria);
            tvCategoria = itemView.findViewById(R.id.tvCardPetsCategoria);
            tvNombre = itemView.findViewById(R.id.tvCardPetsNombre);
            tvEdad = itemView.findViewById(R.id.tvCardPetsAnio);
            tvSexo = itemView.findViewById(R.id.tvCardPetsSexo);
            tvDetalle = itemView.findViewById(R.id.tvCardPetsDetalles);
            tvAdoptado = itemView.findViewById(R.id.tvCardPetsAdoptado);
            if(!pet.getAdoptado().equals("adoptado")){
                itemView.setOnClickListener(view -> {
                    Intent petIntent = new Intent(itemView.getContext(), nextActivity);
                    petIntent.putExtra("mascota", pet);
                    if (nextActivity == AsistenteDetalleMascotasActivity.class) {
                        crudResultLauncher.launch(petIntent);
                        return;
                    }
                    itemView.getContext().startActivity(petIntent);
                });
            }else{
                tvDetalle.setVisibility(View.GONE);
                tvAdoptado.setVisibility(View.VISIBLE);
            }

        }
    }

    public void setCrudResultLauncher(ActivityResultLauncher<Intent> crudResultLauncher) {
        this.crudResultLauncher = crudResultLauncher;
    }

}
