package com.pucp.bemyhome.Adoptante;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.PetsCardAdapter;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.Modals.ModalBottomSheetFilter;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdoptanteBusquedasActivity extends AppCompatActivity {

    PagingConfig config = new PagingConfig(8,4,true);
    PetsCardAdapter petsCardAdapter;
    FirestorePagingOptions<Pet> options;
    Query petsQuery;

    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout emptyView;
    EditText etSearch;

    private String categoryFilter = "";
    private String generoFilter = "";
    private String searchText = "";

    private ModalBottomSheetFilter modalBottomSheet = new ModalBottomSheetFilter();

    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchPets = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            if(!searchTextFromEt.isEmpty()){
                searchText = searchTextFromEt;
                ejecutarQuery();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_adoptante_busquedas);

        recyclerView = findViewById(R.id.rvAdoptanteListPets);
        shimmerFrameLayout = findViewById(R.id.shimmerAdoptanteListPets);
        emptyView = findViewById(R.id.llAdoptanteListPetsEmptyView);
        etSearch = findViewById(R.id.etAdoptanteListPetsSearch);

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

        petsQuery = FirebaseFirestore.getInstance().collection("pets");

        Intent intent = getIntent();
        if (intent!=null){
            if (intent.hasExtra("categoryFilter")) {
                categoryFilter = intent.getStringExtra("categoryFilter");
                modalBottomSheet.setCategoryFilter(categoryFilter);
                petsQuery = petsQuery.whereEqualTo("searchCategoria",categoryFilter);
            }
            if (intent.hasExtra("searchText")){
                searchText = intent.getStringExtra("searchText");
                petsQuery = petsQuery.whereArrayContains("searchKeywords", searchText);
                etSearch.setText(searchText);
            }
        }

        options = new FirestorePagingOptions.Builder<Pet>()
                .setLifecycleOwner(this)
                .setQuery(petsQuery, config, petSnapshotParser)
                .build();
        petsCardAdapter = new PetsCardAdapter(options, AdoptanteDetalleMascotaActivity.class);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(petsCardAdapter);
        petsCardAdapter.startListening();

        petsCardAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (petsCardAdapter.getItemCount()>0){
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            }
        });
    }

    public void ejecutarQuery(){
        petsQuery = FirebaseFirestore.getInstance().collection("pets");
        if(!categoryFilter.isEmpty()){
            petsQuery =  petsQuery.whereEqualTo("searchCategoria",categoryFilter);
        }
        if(!generoFilter.isEmpty()){
            petsQuery =  petsQuery.whereEqualTo("searchGenero",generoFilter);
        }
        if(!searchText.isEmpty()){
            petsQuery =  petsQuery.whereArrayContains("searchKeywords", searchText);
        }

        options = new FirestorePagingOptions.Builder<Pet>()
                .setLifecycleOwner(this)
                .setQuery(petsQuery, config, petSnapshotParser)
                .build();
        petsCardAdapter.updateOptions(options);
    }

    public void showFilters(View view){
        modalBottomSheet.show(getSupportFragmentManager(), modalBottomSheet.getTag());
    }

    public void setFilters(String categoryFilter, String generoFilter) {
        Log.d("msg", generoFilter);
        if(!this.categoryFilter.equals(categoryFilter) || !this.generoFilter.equals(generoFilter)){
            this.categoryFilter = categoryFilter;
            this.generoFilter = generoFilter;
            ejecutarQuery();
        }
    }

    public void backButton(View view){
        onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(petsCardAdapter!=null) petsCardAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(petsCardAdapter!=null)  petsCardAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(petsCardAdapter!=null) petsCardAdapter.stopListening();
    }

    public SnapshotParser<Pet> petSnapshotParser = new SnapshotParser<Pet>() {
        @NonNull
        @Override
        public Pet parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            Pet pet = snapshot.toObject(Pet.class);
            if (pet !=null){
                pet.setKey(snapshot.getId());
                return pet;
            }
            return new Pet();
        }
    };
}