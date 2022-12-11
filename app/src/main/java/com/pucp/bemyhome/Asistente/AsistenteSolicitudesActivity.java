package com.pucp.bemyhome.Asistente;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pucp.bemyhome.Adapters.AsistenteSolicitudesAdapter;
import com.pucp.bemyhome.Entity.Solicitud;
import com.pucp.bemyhome.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class AsistenteSolicitudesActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvTxt;
    TextView tvEmpty;
    PagingConfig config = new PagingConfig(8,4,true);
    AsistenteSolicitudesAdapter asistSolicitudesAdapter;
    FirestorePagingOptions<Solicitud> options;
    Query solicitudesQuery;
    RecyclerView rvListSolicitudes;
    LinearLayout emptyView;
    String typeSoli;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_solicitudes);
        tvTitle = findViewById(R.id.tvAsistenteSolicitudesTittle);
        tvTxt = findViewById(R.id.tvAsistenteSolicitudesTxt);
        rvListSolicitudes = findViewById(R.id.rvAsistenteSolicitudes);
        emptyView = findViewById(R.id.llAsistenteSolicitudesEmptyView);
        tvEmpty = findViewById(R.id.tvAsistenteSolicitudesTxtEmpty);
        shimmerFrameLayout = findViewById(R.id.shimmerAsistenteSolicitudes);

        typeSoli = getIntent().getStringExtra("typeSoli");

        switch (typeSoli){
            case "pendiente":
                tvTitle.setText("Solicitudes pendientes");
                tvTxt.setText("Revisa las solicitudes pendientes de aprobación");
                tvEmpty.setText("No hay solicitudes pendientes de aprobación");
                solicitudesQuery = FirebaseFirestore.getInstance().collection("solicitudes").whereEqualTo("estado","Pendiente de aprobación");
                break;
            case "historial":
                tvTitle.setText("Historial de solicitudes");
                tvTxt.setText("Revisa todas las solicitudes ya respondidas");
                tvEmpty.setText("Aún no se han respondido solicitudes de adopción");
                solicitudesQuery = FirebaseFirestore.getInstance().collection("solicitudes").whereNotEqualTo("estado","Pendiente de aprobación");
                break;
        }

        options = new FirestorePagingOptions.Builder<Solicitud>()
                .setLifecycleOwner(this)
                .setQuery(solicitudesQuery, config, solicitudSnapshotParser)
                .build();
        asistSolicitudesAdapter = new AsistenteSolicitudesAdapter(options, AsistenteDetalleSolicitudesActivity.class);

        rvListSolicitudes.setLayoutManager(new LinearLayoutManager(AsistenteSolicitudesActivity.this));
        rvListSolicitudes.setAdapter(asistSolicitudesAdapter);
        asistSolicitudesAdapter.startListening();

        asistSolicitudesAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                LoadState refresh = combinedLoadStates.getRefresh();
                if (refresh instanceof LoadState.Loading) {
                   rvListSolicitudes.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    shimmerFrameLayout.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.startShimmerAnimation();
                }else if(refresh instanceof LoadState.NotLoading){
                    shimmerFrameLayout.stopShimmerAnimation();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (asistSolicitudesAdapter.getItemCount()>0){
                        rvListSolicitudes.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }else{
                        rvListSolicitudes.setVisibility(View.GONE);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        if (asistSolicitudesAdapter!= null) asistSolicitudesAdapter.refresh();
    }

    public void backButton(View view){
        onBackPressed();
    }
}