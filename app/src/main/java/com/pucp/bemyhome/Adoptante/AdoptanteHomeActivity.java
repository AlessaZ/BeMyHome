package com.pucp.bemyhome.Adoptante;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pucp.bemyhome.Adapters.ImageSelectorKindPetAdapter;
import com.pucp.bemyhome.Details.ImageSelectorKindPetMargin;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import java.util.Arrays;
import java.util.List;

public class AdoptanteHomeActivity extends AppCompatActivity {

    final List<Integer> CATEGORY_IMAGES = Arrays.asList(R.drawable.cat_unselected, R.drawable.dog_unselected, R.drawable.bird_unselected, R.drawable.rabbit_unselected, R.drawable.others_unselected);
    final List<Integer> CATEGORY_TEXTS = Arrays.asList(R.string.kind_pet_cat, R.string.kind_pet_dog, R.string.kind_pet_bird, R.string.kind_pet_rabbit, R.string.kind_pet_others);
    final int CATEGORY_SELECTOR_COLUMNS = CATEGORY_IMAGES.size();
    final int CATEGORY_SELECTOR_MARGIN = 16;
    final int CATEGORY_SELECTOR_MARGIN_HORIZONTAL = 40;
    BottomNavigationView bottomNavigationView;
    RecyclerView categorySelector;
    TextView tvSaludo;
    TextView tvNombre;
    ImageView ivPfp;
    EditText etSearch;
    TextView tvSolicitudes;
    ShapeableImageView ivFotitoAdopt;

    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchPets = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            if(!searchTextFromEt.isEmpty()){
                Intent searchIntent = new Intent(AdoptanteHomeActivity.this, AdoptanteBusquedasActivity.class);
                searchIntent.putExtra("searchText", searchTextFromEt);
                startActivity(searchIntent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_adoptante_home);
        setBottomNavigationView();
        categorySelector = findViewById(R.id.rvAdopHomeCategories);
        tvSaludo = findViewById(R.id.tvAdopHomeSaludo);
        tvNombre = findViewById(R.id.tvAdopHomeNombre);
        etSearch = findViewById(R.id.etAdopHomeSearch);
        ivPfp = findViewById(R.id.ivAdopHomePfp);
        tvSolicitudes = findViewById(R.id.tvAdoptHomeCantSolis);
        ivFotitoAdopt = findViewById(R.id.ivAdoptHomeFotitoAdop);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(searchPets);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(searchPets, DELAY);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        User user = gson.fromJson(sharedPreferences.getString("user",""), User.class);

        FirebaseUser userG = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivFotitoAdopt);
        FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("adoptanteUser.uid", userG.getUid()).count().get(AggregateSource.SERVER).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                tvSolicitudes.setText(aggregateQuerySnapshot.getCount() + "");
            }
        });

        tvNombre.setText(user.getNombre());
        Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.avatar_placeholder).into(ivPfp);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        ImageSelectorKindPetAdapter categoryAdapter = new ImageSelectorKindPetAdapter(this, CATEGORY_IMAGES, CATEGORY_TEXTS);
        categoryAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = categorySelector.getChildAdapterPosition(view);
                if (position>=0 && position<CATEGORY_IMAGES.size()){
                    Intent categoryIntent = new Intent(AdoptanteHomeActivity.this, AdoptanteBusquedasActivity.class);
                    categoryIntent.putExtra("categoryFilter", getString(CATEGORY_TEXTS.get(position)));
                    startActivity(categoryIntent);
                }
            }
        });
        ImageSelectorKindPetMargin categorySelectorMargin = new ImageSelectorKindPetMargin(CATEGORY_SELECTOR_COLUMNS,CATEGORY_SELECTOR_MARGIN, CATEGORY_SELECTOR_MARGIN_HORIZONTAL);

        categorySelector.setLayoutManager(layoutManager);
        categorySelector.setAdapter(categoryAdapter);
        categorySelector.addItemDecoration(categorySelectorMargin);
        setBottomNavigationView();

    }


    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdoptHomeAct);
        bottomNavigationView.clearAnimation();
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdoptHome);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdoptHome:
                        return true;
                    case R.id.bottomNavMenuAdoptSolicitud:
                        startActivity(new Intent(getApplicationContext(), AdoptanteAdopcionesActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdoptProfile:
                        startActivity(new Intent(getApplicationContext(), AdoptanteProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

}