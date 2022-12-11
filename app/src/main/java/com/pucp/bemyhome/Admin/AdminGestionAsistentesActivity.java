package com.pucp.bemyhome.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.UsersAsistenteAdapter;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdminGestionAsistentesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView rvUsersAsist;
    PagingConfig config = new PagingConfig(5,3,true);
    LinearLayout emptyView;
    FirestorePagingOptions<User> options;
    UsersAsistenteAdapter usersAsistenteAdapter;
    Query asistQuerys = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Asistente");
    ShimmerFrameLayout shimmerFrameLayout;
    EditText etSearch;
    private String searchText = "";
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchAsistentes = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            searchText = searchTextFromEt;
            Log.wtf("msg",searchText);
            ejecutarQuery();
        }
    };

    public void ejecutarQuery(){
        asistQuerys = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Asistente");
        if(!searchText.isEmpty()){
            asistQuerys = asistQuerys.whereArrayContains("searchKeywords", searchText);
        }

        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(asistQuerys, config, usersSnapshotParser)
                .build();
        usersAsistenteAdapter.updateOptions(options);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_gestion_asistentes);

        setBottomNavigationView();
        shimmerFrameLayout = findViewById(R.id.shimmerAdminGestionAsistentes);
        rvUsersAsist = findViewById(R.id.rvAdminGestionAsistentes);
        emptyView = findViewById(R.id.llAdminGestionAsistentesEmptyView);
        etSearch = findViewById(R.id.etAdminGestionAsistentesSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(searchAsistentes);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(searchAsistentes, DELAY);
            }
        });

        shimmerFrameLayout.startShimmerAnimation();
        Query tiQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Asistente");
        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(tiQuery, config,usersSnapshotParser)
                .build();
        usersAsistenteAdapter = new UsersAsistenteAdapter(options);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvUsersAsist.setLayoutManager(layoutManager);
        rvUsersAsist.setAdapter(usersAsistenteAdapter);

        usersAsistenteAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvUsersAsist.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (usersAsistenteAdapter.getItemCount()>0){
                        rvUsersAsist.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else {
                        rvUsersAsist.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }

                return null;
            }
        });

    }

    public void setBottomNavigationView(){
        bottomNavigationView = findViewById(R.id.bottomNavMenuAdminGestionAsistentesAct);
        bottomNavigationView.setSelectedItemId(R.id.bottomNavMenuAdminAsistentes);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.bottomNavMenuAdminHome:
                        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.bottomNavMenuAdminAsistentes:
                        return true;
                    case R.id.bottomNavMenuAdminProfile:
                        startActivity(new Intent(getApplicationContext(), AdminProfileActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        usersAsistenteAdapter.refresh();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
        overridePendingTransition(0,0);
        finish();
    }

    public void anadirAsistenteActivity(View view){
        Intent loginIntent = new Intent(AdminGestionAsistentesActivity.this, AdminNewAsistenteActivity.class);
        startActivity(loginIntent);
    }

    public SnapshotParser<User> usersSnapshotParser = new SnapshotParser<User>() {
        @NonNull
        @Override
        public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            User user = snapshot.toObject(User.class);
            if (user !=null){
                user.setUid(snapshot.getId());
                return user;
            }
            return new User();
        }
    };
}