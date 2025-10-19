package com.akadoblee.frontendcrudandroidstudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar loadingProgress;
    private RapperAdapter adapter;
    private List<Rapper> rapperList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        initViews();
        setupRecyclerView();
        loadRappers();

        // Configurar FAB para redirigir a AddRapperActivity
        CardView fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddRapperActivity.class);
            startActivity(intent);
        });

        // Configurar botón volver
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Configurar botón buscar
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            Toast.makeText(HomeActivity.this, "Buscar raperos - Próximamente", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar los datos cada vez que la actividad se reanude
        loadRappers();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rappersRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        rapperList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupRecyclerView() {
        adapter = new RapperAdapter(rapperList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadRappers() {
        loadingProgress.setVisibility(View.VISIBLE);
        String url = "http://192.168.1.34:3000/rappers";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    try {
                        rapperList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject rapperObject = response.getJSONObject(i);
                            Rapper rapper = new Rapper(
                                    rapperObject.getInt("id"),
                                    rapperObject.getString("aka"),
                                    rapperObject.getString("name"),
                                    rapperObject.getString("album"),
                                    rapperObject.getString("song")
                            );
                            rapperList.add(rapper);
                        }
                        adapter.notifyDataSetChanged();

                        if (rapperList.isEmpty()) {
                            Toast.makeText(HomeActivity.this, "No se encontraron rappers", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    error.printStackTrace();
                    Toast.makeText(HomeActivity.this,
                            "Error de conexión: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}