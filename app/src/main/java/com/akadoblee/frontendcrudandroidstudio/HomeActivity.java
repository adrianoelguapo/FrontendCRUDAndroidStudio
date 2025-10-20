package com.akadoblee.frontendcrudandroidstudio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        baseUrl = getString(R.string.base_url);

        initViews();
        setupRecyclerView();
        setupViewAllButton();
        loadRappers();

        CardView fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddRapperActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            navigateToWelcome();
        });

        findViewById(R.id.searchButton).setOnClickListener(v -> {
            showSearchDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        String url = baseUrl + "/rappers";

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

    private void setupViewAllButton() {
        CardView buttonShowAll = findViewById(R.id.buttonShowAll);
        buttonShowAll.setOnClickListener(v -> {
            loadRappers();
            Toast.makeText(HomeActivity.this, "Mostrando todos los rappers", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar Rapero");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setView(dialogView);

        EditText editSearch = dialogView.findViewById(R.id.editSearch);

        builder.setPositiveButton("BUSCAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String searchTerm = editSearch.getText().toString().trim();
                if (!searchTerm.isEmpty()) {
                    showSearchTypeDialog(searchTerm);
                } else {
                    Toast.makeText(HomeActivity.this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSearchTypeDialog(String searchTerm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Buscar por:");
        builder.setMessage("Selecciona el tipo de búsqueda para: \"" + searchTerm + "\"");

        builder.setPositiveButton("NOMBRE ARTÍSTICO (AKA)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchByAka(searchTerm);
            }
        });

        builder.setNegativeButton("NOMBRE REAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchByName(searchTerm);
            }
        });

        builder.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void searchByAka(String aka) {
        loadingProgress.setVisibility(View.VISIBLE);
        String url = baseUrl + "/rappers/aka/" + aka;

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
                            Toast.makeText(HomeActivity.this, "No se encontraron rappers con ese AKA", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Encontrados: " + rapperList.size() + " rapper(s)", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        rapperList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(HomeActivity.this, "No se encontraron rappers con ese AKA", Toast.LENGTH_SHORT).show();
                    } else {
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this,
                                "Error de conexión: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void searchByName(String name) {
        loadingProgress.setVisibility(View.VISIBLE);
        String url = baseUrl + "/rappers/name/" + name;

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
                            Toast.makeText(HomeActivity.this, "No se encontraron rappers con ese nombre", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Encontrados: " + rapperList.size() + " rapper(s)", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingProgress.setVisibility(View.GONE);
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        rapperList.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(HomeActivity.this, "No se encontraron rappers con ese nombre", Toast.LENGTH_SHORT).show();
                    } else {
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this,
                                "Error de conexión: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}