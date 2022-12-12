package com.pucp.bemyhome.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;


public class UsersAdoptanteAdapter extends FirestorePagingAdapter<User, UsersAdoptanteAdapter.UserAdoptViewHolder>  {

    public UsersAdoptanteAdapter(@NonNull FirestorePagingOptions options) {
        super(options);
    }
    @NonNull
    @Override
    public UserAdoptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_admin_adoptante_user, parent, false);
        return new UserAdoptViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserAdoptViewHolder holder, int position, @NonNull User model) {
        holder.tvDni.setText(model.getDNI());
        holder.tvNombre.setText(model.getNombre());
        holder.tvTel.setText(model.getTelefono());
        Glide.with(holder.itemView.getContext()).load(model.getAvatarUrl())
                .placeholder(holder.itemView.getContext().getDrawable(R.drawable.avatar_placeholder)).dontAnimate()
                .into(holder.ivProfilePic);
    }

    public class UserAdoptViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvDni;
        TextView tvNombre;
        TextView tvTel;
        TextView tvCorreo;

        public UserAdoptViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.ivAdminUserAdptPImage);
            tvDni = itemView.findViewById(R.id.tvAdminUserAsistDni);
            tvNombre = itemView.findViewById(R.id.tvAdminUserAdptNombre);
            tvTel = itemView.findViewById(R.id.tvAdminUserAdptTelefono);
            tvCorreo = itemView.findViewById(R.id.tvAdminUserAdptCorreo);
        }

    }


}
