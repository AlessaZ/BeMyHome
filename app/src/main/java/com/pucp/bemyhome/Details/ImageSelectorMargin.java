package com.pucp.bemyhome.Details;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ImageSelectorMargin extends RecyclerView.ItemDecoration {
    private final int columns;
    private final int margin;

    public ImageSelectorMargin(int columns, int margin) {
        this.columns = columns;
        this.margin = margin;
    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildLayoutPosition(view);
        //set right margin to all
        outRect.right = margin;
        //set bottom margin to all
        outRect.bottom = margin;
        //we only add top margin to the first row
        if (position <columns) {
            outRect.top = margin;
        }
        //add left margin only to the first column
        if(position%columns==0){
            outRect.left = margin;
        }
    }
}
