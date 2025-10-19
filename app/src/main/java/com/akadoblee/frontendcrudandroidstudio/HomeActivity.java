package com.akadoblee.frontendcrudandroidstudio;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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

        // Configurar FAB (ahora es CardView)
        CardView fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            // Aquí iría la lógica para añadir nuevo rapero
            // Por ahora solo mostraremos un mensaje
            // Toast.makeText(HomeActivity.this, "Añadir nuevo rapero", Toast.LENGTH_SHORT).show();
        });

        // Configurar botón volver
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Configurar botón buscar
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            // Lógica para buscar
            // Toast.makeText(HomeActivity.this, "Buscar raperos", Toast.LENGTH_SHORT).show();
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rappersRecyclerView);
        loadingProgress = findViewById(R.id.loadingProgress);
        rapperList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupRecyclerView() {
        adapter = new RapperAdapter(rapperList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadRappers() {
        String url = "http://192.168.1.41:3000/rappers";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    loadingProgress.setVisibility(View.GONE);
                    try {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    // Manejar error
                    error.printStackTrace();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }
}