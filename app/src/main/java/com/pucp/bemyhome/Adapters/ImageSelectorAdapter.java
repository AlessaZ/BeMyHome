package com.pucp.bemyhome.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.bemyhome.R;

import java.util.List;

public class ImageSelectorAdapter extends RecyclerView.Adapter<ImageSelectorAdapter.ViewHolder>
        implements View.OnClickListener {
    private Context context;
    private int selectedItem = 0;
    private List<Integer> images;
    private List<Integer> texts;
    private View.OnClickListener listener;

    public ImageSelectorAdapter(Context context, List<Integer> images, List<Integer> texts) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_selector, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (selectedItem == position) {
            holder.tvDesc.setTextColor(context.getColor(R.color.pink_main));
            holder.tvDesc.setTypeface(context.getResources().getFont(R.font.montserrat_semibold));
            holder.ivImage.setImageDrawable(context.getDrawable(R.drawable.cat_selected));
        }else{
            holder.tvDesc.setTextColor(context.getColor(R.color.black));
            holder.ivImage.setImageDrawable(context.getDrawable(R.drawable.cat_unselected));
            holder.tvDesc.setTypeface(context.getResources().getFont(R.font.montserrat_medium));
        }

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

        FrameLayout flOverlay;
        ImageView ivImage;
        TextView tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flOverlay =itemView.findViewById(R.id.flImageSelector);
            ivImage = itemView.findViewById(R.id.ivImageSelector);
            tvDesc = itemView.findViewById(R.id.tvImageSelector);
        }
    }

    public int getSelectedItem(){
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}