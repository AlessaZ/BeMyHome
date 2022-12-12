package com.pucp.bemyhome.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.pucp.bemyhome.Admin.AdminUpdateAsistenteActivity;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

public class UsersAsistenteAdapter extends FirestorePagingAdapter<User, UsersAsistenteAdapter.UserAsistenteViewHolder>  {

    public UsersAsistenteAdapter(@NonNull FirestorePagingOptions options) {
        super(options);
    }

    @NonNull
    @Override
    public UserAsistenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_admin_asistente_user, parent, false);
        return new UserAsistenteViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAsistenteViewHolder holder, int position, @NonNull User model) {
        holder.tvDni.setText(model.getDNI());
        holder.tvNombre.setText(model.getNombre());
        holder.tvcorreo.setText(model.getCorreo());
        holder.userAsist = model;
        Glide.with(holder.itemView.getContext()).load(model.getAvatarUrl())
                .placeholder(holder.itemView.getContext().getDrawable(R.drawable.avatar_placeholder)).dontAnimate()
                .into(holder.ivProfilePic);
    }

    public class UserAsistenteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvDni;
        TextView tvNombre;
        User userAsist;
        TextView tvcorreo;
        Button btnAct;

        public UserAsistenteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivAdminUserAsistPImage);
            tvDni = itemView.findViewById(R.id.tvAdminUserAsistDni);
            tvNombre = itemView.findViewById(R.id.tvAdminUserAsistNombre);
            btnAct = itemView.findViewById(R.id.btnAdminUserAsistActualizar);
            tvcorreo = itemView.findViewById(R.id.tvAdminUserAsistCorreo);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), AdminUpdateAsistenteActivity.class);
                intent.putExtra("userAsist", userAsist);
                itemView.getContext().startActivity(intent);
            });
            btnAct.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), AdminUpdateAsistenteActivity.class);
                intent.putExtra("userAsist", userAsist);
                itemView.getContext().startActivity(intent);
            });
        }

    }

}
