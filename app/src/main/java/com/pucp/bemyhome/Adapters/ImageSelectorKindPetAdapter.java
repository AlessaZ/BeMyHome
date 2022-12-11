package com.pucp.bemyhome.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.bemyhome.R;

import java.util.List;

public class ImageSelectorKindPetAdapter extends RecyclerView.Adapter<ImageSelectorKindPetAdapter.ViewHolder>
implements View.OnClickListener {
    private Context context;
    private int selectedItem = 0;
    private List<Integer> images;
    private List<Integer> texts;
    private View.OnClickListener listener;

    public ImageSelectorKindPetAdapter(Context context, List<Integer> images, List<Integer> texts) {
        this.context = context;
        this.images = images;
        this.texts = texts;
    }

    public void setOnItemClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selector_kind_pet, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.ivImage.setImageResource(images.get(position));
        holder.tvDesc.setText(texts.get(position));
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onClick(View view) {
        if (listener !=null){
            listener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivImage;
        TextView tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImageSelectorCategory);
            tvDesc = itemView.findViewById(R.id.tvImageSelectorCategory);
        }
    }
}
