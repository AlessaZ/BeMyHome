package com.pucp.bemyhome.Adoptante;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.AdoptanteSolicitudesAdapter;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AdoptanteAdopcionesRecycleActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    AdoptanteSolicitudesAdapter adoptanteSolicitudesAdapter;
    PagingConfig config = new PagingConfig(8,4,true);
    FirestorePagingOptions<Solicitud> options;
    Query solicitudQuery;
    String typeSoli;
    RecyclerView rvListReservas;
    TextView tvTitle;
    TextView tvTxt;
    TextView tvEmpty;
    FirebaseUser user;
    LinearLayout emptyView;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoptante_adopciones_recycle);

        tvTitle = findViewById(R.id.tvClienteReservasTittle);
        tvTxt = findViewById(R.id.tvAdoptanteAdopcionesRecycleTxt);
        rvListReservas = findViewById(R.id.rvAdoptanteAdopcionesRecycle);
        emptyView = findViewById(R.id.llAdoptanteAdopcionesRecycleEmptyView);
        tvEmpty = findViewById(R.id.tvAdoptanteAdopcionesRecycleTxtEmpty);
        shimmerFrameLayout = findViewById(R.id.shimmerAdoptanteAdopcionesRecycle);

        typeSoli = getIntent().getStringExtra("typeSolicitudes");
        user = FirebaseAuth.getInstance().getCurrentUser();

        switch (typeSoli){
            case "historial":
                tvTitle.setText("Historial de solicitudes");
                tvTxt.setText("Encuentra aquí todas las solicitudes que has realizado");
                tvEmpty.setText("Aún no has realizado ninguna solicitud de adopción");
                solicitudQuery = FirebaseFirestore.getInstance().collection("solicitudes");
                break;
            case "enProceso":
                tvTitle.setText("Pendientes de aprobación");
                tvTxt.setText("Encuentra aquí todas las solicitudes que están a la espera de una respuesta");
                tvEmpty.setText("No hay solicitudes pendientes de aprobación");
                solicitudQuery = FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("adoptanteUser.uid",user.getUid()).whereEqualTo("estado","Pendiente de aprobación");
                break;
            case "finalizadas":
                tvTitle.setText("Solicitudes finalizadas");
                tvTxt.setText("Encuentra aquí todas las solicitudes que hayan sido respondidas");
                tvEmpty.setText("Aún no hay solicitudes que hayan sido respondidas");
                solicitudQuery = FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("adoptanteUser.uid",user.getUid()).whereNotEqualTo("estado","Pendiente de aprobación");
                break;
        }

        options = new FirestorePagingOptions.Builder<Solicitud>()
                .setLifecycleOwner(this)
                .setQuery(solicitudQuery, config, solicitudSnapshotParser)
                .build();
        adoptanteSolicitudesAdapter = new AdoptanteSolicitudesAdapter(options, AdoptanteDetalleSolicitudActivity.class);

        rvListReservas.setLayoutManager(new LinearLayoutManager(AdoptanteAdopcionesRecycleActivity.this));
        rvListReservas.setAdapter(adoptanteSolicitudesAdapter);
        adoptanteSolicitudesAdapter.startListening();

        adoptanteSolicitudesAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                Log.d("msg", adoptanteSolicitudesAdapter.getItemCount()+"");
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                    rvListReservas.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (adoptanteSolicitudesAdapter.getItemCount()>0){
                        rvListReservas.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        rvListReservas.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
                return null;
            }
        });
    }

    public SnapshotParser<Solicitud> solicitudSnapshotParser = new SnapshotParser<Solicitud>() {
        @NonNull
        @Override
        public Solicitud parseSnapshot(@NonNull DocumentSnapshot snapshot) {
            Solicitud solicitud = snapshot.toObject(Solicitud.class);
            Log.d("msg", solicitud.getEstado());
            if (solicitud !=null){
                solicitud.setKey(snapshot.getId());
                return solicitud;
            }
            return new Solicitud();
        }
    };

    public void backButton(View view){
        onBackPressed();
    }
}