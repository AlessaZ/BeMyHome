package com.pucp.bemyhome.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.pucp.bemyhome.R;
import com.pucp.bemyhome.UnloginUser.OboardingActivity;

public class OnboardingSlidesAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public OnboardingSlidesAdapter(Context context) {
        this.context = context;
    }

    int imagenes[] = {
            R.drawable.onboarding_abrazo,
            R.drawable.gatito_caja,
            R.drawable.onboarding_perrito,
    };

    int frases[] = {
            R.string.onboarding_abrazo,
            R.string.onboarding_gatito,
            R.string.onboarding_perrito,
    };


    @Override
    public int getCount() {
        return frases.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_onbording, container,false);

        TextView textViewTitle = view.findViewById(R.id.tvOnboardingSlideTitle);
        TextView textViewText = view.findViewById(R.id.tvOnboardingSlideText);
        ImageView imageView = view.findViewById(R.id.ivOnboardingSlideImage);

        if (position == 0){
            textViewTitle.setVisibility(View.VISIBLE);
            textViewText.setTextSize(20);
        }
        textViewText.setText(frases[position]);
        textViewText.setTextSize(22);
        imageView.setImageResource(imagenes[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

}
