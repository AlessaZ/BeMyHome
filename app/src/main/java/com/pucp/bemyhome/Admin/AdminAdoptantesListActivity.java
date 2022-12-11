package com.pucp.bemyhome.Admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.UsersAdoptanteAdapter;
import com.pucp.bemyhome.Entity.User;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdminAdoptantesListActivity extends AppCompatActivity {

    RecyclerView rvAdoptantes;
    PagingConfig config = new PagingConfig(5,3,true);
    FirestorePagingOptions<User> options;
    UsersAdoptanteAdapter usersAdoptanteAdapter;
    ShimmerFrameLayout shimmerFrameLayout;
    Query adoptQuerys = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Adoptante");
    LinearLayout emptyView;
    EditText etSearch;
    private String searchText = "";
    private Handler handler = new Handler(Looper.getMainLooper());
    private final long DELAY = 900;

    private Runnable searchAdoptantes = new Runnable() {
        @Override
        public void run() {
            String searchTextFromEt = etSearch.getText().toString().trim();
            searchText = searchTextFromEt;
            Log.wtf("msg",searchText);
            ejecutarQuery();
        }
    };

    public void ejecutarQuery(){
        adoptQuerys = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Adoptante");
        if(!searchText.isEmpty()){
            adoptQuerys = adoptQuerys.whereArrayContains("searchKeywords", searchText);
        }

        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(adoptQuerys, config, usersSnapshotParser)
                .build();
        usersAdoptanteAdapter.updateOptions(options);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_adoptantes_list);

        shimmerFrameLayout = findViewById(R.id.shimmerAdminAdoptantesList);
        rvAdoptantes = findViewById(R.id.rvAdminAdoptantesList);
        emptyView = findViewById(R.id.llAdminAdoptantesListEmptyView);

        shimmerFrameLayout.startShimmerAnimation();
        Query tiQuery = FirebaseFirestore.getInstance().collection("users").whereEqualTo("permisos","Adoptante");
        options = new FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(this)
                .setQuery(tiQuery, config, usersSnapshotParser)
                .build();
       usersAdoptanteAdapter = new UsersAdoptanteAdapter(options);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvAdoptantes.setLayoutManager(layoutManager);
        rvAdoptantes.setAdapter(usersAdoptanteAdapter);

        usersAdoptanteAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvAdoptantes.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (usersAdoptanteAdapter.getItemCount()>0){
                        rvAdoptantes.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        rvAdoptantes.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            }
        });
    }

    public void backButton(View view) { onBackPressed(); }
}