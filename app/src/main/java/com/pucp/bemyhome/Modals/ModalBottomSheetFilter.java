package com.pucp.bemyhome.Modals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pucp.bemyhome.Adoptante.AdoptanteBusquedasActivity;
import com.pucp.bemyhome.Asistente.AsistenteMacotasActivity;
import com.pucp.bemyhome.R;

import java.util.List;

public class ModalBottomSheetFilter extends BottomSheetDialogFragment {
    public String TAG = "ModalBottomSheet";
    public String categoryFilter = "";
    public String generosFilter = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modal_bottom_sheet_filter, container, false);
        ChipGroup chipGroup = view.findViewById(R.id.cgModalBottomSheet);
        ChipGroup chipGroup2 = view.findViewById(R.id.cgModalBottomSheet2);

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                for (int i = 0; i<chipGroup.getChildCount(); i++){
                    Chip c = (Chip) group.getChildAt(i);
                    if (c.isChecked()){
                        c.setTextAppearanceResource(R.style.chipTextSelected);
                        c.setTextColor(view.getContext().getColor(R.color.pink_main));
                        c.setChipStrokeColorResource(R.color.pink_main);
                        c.setChipStrokeWidthResource(R.dimen.dp1);
                        categoryFilter = c.getText().toString().equals("Todas las categorías")?"":c.getText().toString();
                    }else{
                        c.setTextAppearanceResource(R.style.chipTextUnselected);
                        c.setTextColor(view.getContext().getColor(R.color.white));
                        c.setChipStrokeColorResource(R.color.grey_25);
                        c.setChipStrokeWidthResource(R.dimen.dp05);
                    }
                }
            }
        });

        chipGroup2.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                for (int i = 0; i<chipGroup2.getChildCount(); i++){
                    Chip c = (Chip) group.getChildAt(i);
                    if (c.isChecked()){
                        c.setTextAppearanceResource(R.style.chipTextSelected);
                        c.setTextColor(view.getContext().getColor(R.color.pink_main));
                        c.setChipStrokeColorResource(R.color.pink_main);
                        c.setChipStrokeWidthResource(R.dimen.dp1);
                        generosFilter = c.getText().toString().equals("Todos los géneros")?"":c.getText().toString();
                    }else{
                        c.setTextAppearanceResource(R.style.chipTextUnselected);
                        c.setTextColor(view.getContext().getColor(R.color.white));
                        c.setChipStrokeColorResource(R.color.grey_25);
                        c.setChipStrokeWidthResource(R.dimen.dp05);
                    }
                }
            }
        });

        if(!categoryFilter.isEmpty()){
            for (int i = 0; i<chipGroup.getChildCount(); i++){
                Chip c = (Chip) chipGroup.getChildAt(i);
                c.setChecked(c.getText().equals(categoryFilter));
            }
        }

        if(!generosFilter.isEmpty()){
            for (int i = 0; i<chipGroup2.getChildCount(); i++){
                Chip c = (Chip) chipGroup2.getChildAt(i);
                c.setChecked(c.getText().equals(generosFilter));
            }
        }

        view.findViewById(R.id.ibModalBottomSheetClose).setOnClickListener(view1 -> {
            dismiss();
        });
        view.findViewById(R.id.btnModalBottomSheetApply).setOnClickListener(view1 -> {
            if (getActivity() instanceof AsistenteMacotasActivity){
                ((AsistenteMacotasActivity) getActivity()).setFilters(categoryFilter, generosFilter);
            }else if(getActivity() instanceof AdoptanteBusquedasActivity){
                ((AdoptanteBusquedasActivity) getActivity()).setFilters(categoryFilter, generosFilter);
            }
            dismiss();
        });
        return view;
    }

    public void setCategoryFilter(String categoryFilter) {
        this.categoryFilter = categoryFilter;
    }
}
