package com.akadoblee.frontendcrudandroidstudio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class AddRapperActivity extends AppCompatActivity {

    private EditText editAka, editName, editAlbum, editSong;
    private CardView buttonSave;
    private ProgressBar loadingProgress;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rapper);

        initViews();
        setupListeners();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initViews() {
        editAka = findViewById(R.id.editAka);
        editName = findViewById(R.id.editName);
        editAlbum = findViewById(R.id.editAlbum);
        editSong = findViewById(R.id.editSong);
        buttonSave = findViewById(R.id.buttonSave);
        loadingProgress = findViewById(R.id.loadingProgress);

        // Configurar botón volver
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        buttonSave.setOnClickListener(v -> {
            if (validateForm()) {
                addRapper();
            }
        });
    }

    private boolean validateForm() {
        if (editAka.getText().toString().trim().isEmpty()) {
            editAka.setError("Este campo es obligatorio");
            editAka.requestFocus();
            return false;
        }
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError("Este campo es obligatorio");
            editName.requestFocus();
            return false;
        }
        if (editAlbum.getText().toString().trim().isEmpty()) {
            editAlbum.setError("Este campo es obligatorio");
            editAlbum.requestFocus();
            return false;
        }
        if (editSong.getText().toString().trim().isEmpty()) {
            editSong.setError("Este campo es obligatorio");
            editSong.requestFocus();
            return false;
        }
        return true;
    }

    private void addRapper() {
        // Mostrar loading
        loadingProgress.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        String url = "http://192.168.1.34:3000/rappers";

        StringRequest postRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingProgress.setVisibility(View.GONE);
                        buttonSave.setEnabled(true);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");

                            Toast.makeText(AddRapperActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();

                            // Redirigir a HomeActivity - ahora se recargarán automáticamente los datos
                            Intent intent = new Intent(AddRapperActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AddRapperActivity.this, "❌ Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgress.setVisibility(View.GONE);
                        buttonSave.setEnabled(true);

                        error.printStackTrace();
                        Toast.makeText(AddRapperActivity.this, "❌ Error al agregar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("aka", editAka.getText().toString().trim());
                params.put("name", editName.getText().toString().trim());
                params.put("album", editAlbum.getText().toString().trim());
                params.put("song", editSong.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        requestQueue.add(postRequest);
    }
}