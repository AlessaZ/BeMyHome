package com.pucp.bemyhome.Asistente;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.PetsCardAdapter;
import com.pucp.bemyhome.Entity.Pet;
import com.pucp.bemyhome.Modals.ModalBottomSheetFilter;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AsistenteMacotasActivity extends AppCompatActivity {

    PagingConfig config = new PagingConfig(8,4,true);
    PetsCardAdapter petsCardAdapter;
    FirestorePagingOptions<Pet> options;
    Query petsQuery = FirebaseFirestore.getInstance().collection("pets");

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout emptyView;
    EditText etSearch;

    private String categoryFilter = "";
    private String generosFilter = "";
    private String searchText = "";

    private ModalBottomSheetFilter modalBottomSheet = new ModalBottomSheetFilter();
    //Text Typing
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
        setContentView(R.layout.activity_asistente_macotas);

        setBottomNavigationView();

        recyclerView = findViewById(R.id.rvAsistenteListPets);
        shimmerFrameLayout = findViewById(R.id.shimmerAsistenteListPets);
        emptyView = findViewById(R.id.llAsistenteListPetsEmptyView);
        etSearch = findViewById(R.id.etAsistenteListPetsSearch);

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

        options = new FirestorePagingOptions.Builder<Pet>()
                .setLifecycleOwner(this)
                .setQuery(petsQuery, config, petsSnapshotParser)
                .build();
        petsCardAdapter = new PetsCardAdapter(options, AsistenteDetalleMascotasActivity.class);
        petsCardAdapter.setCrudResultLauncher(refreshLauncher);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(petsCardAdapter);

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
            petsQuery = petsQuery.whereEqualTo("searchCategoria",categoryFilter);
        }
        if(!generosFilter.isEmpty()){
            petsQuery = petsQuery.whereEqualTo("searchGenero",generosFilter);
        }
        if(!searchText.isEmpty()){
            petsQuery = petsQuery.whereArrayContains("searchKeywords", searchText);
        }

        options = new FirestorePagingOptions.Builder<Pet>()
                .setLifecycleOwner(this)
                .setQuery(petsQuery, config, petsSnapshotParser)
                .build();
        petsCardAdapter.updateOptions(options);
    }

    public void showFilters(View view){
        modalBottomSheet.show(getSupportFragmentManager(), modalBottomSheet.getTag());
    }

    public void setFilters(String categoryFilter, String generosFilter) {
        if(!this.categoryFilter.equals(categoryFilter) || !this.generosFilter.equals(generosFilter)){
            this.categoryFilter = categoryFilter;
            this.generosFilter = generosFilter;
            ejecutarQuery();
        }
    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAsistMascotasAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAsistMascotas);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAsistHome:
                        startActivity(new Intent(getApplicationContext(),AsistenteHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAsistProfile:
                        startActivity(new Intent(getApplicationContext(),AsistenteProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAsistMascotas:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),AsistenteHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void backButton(View view){
        onBackPressed();
    }

    public void goToCreatePet(View view){
        Intent createIntent = new Intent(AsistenteMacotasActivity.this,AsistenteNewMascotaFormActivity.class);
        refreshLauncher.launch(createIntent);
    }


    public SnapshotParser<Pet> petsSnapshotParser = new SnapshotParser<Pet>() {
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

    ActivityResultLauncher<Intent> refreshLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(petsCardAdapter!=null && result.getResultCode() == RESULT_OK) petsCardAdapter.refresh();
                }
            });
}