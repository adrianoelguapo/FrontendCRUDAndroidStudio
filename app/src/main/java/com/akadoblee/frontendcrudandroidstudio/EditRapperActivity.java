package com.akadoblee.frontendcrudandroidstudio;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class EditRapperActivity extends AppCompatActivity {

    private TextView textId;
    private EditText editAka, editName, editAlbum, editSong;
    private CardView buttonUpdate;
    private ProgressBar loadingProgress;
    private RequestQueue requestQueue;
    private Rapper rapper;
    private String originalAka, originalName, originalAlbum, originalSong;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rapper);

        baseUrl = getString(R.string.base_url);

        rapper = (Rapper) getIntent().getSerializableExtra("rapper");

        initViews();
        setupListeners();
        populateForm();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initViews() {
        textId = findViewById(R.id.textId);
        editAka = findViewById(R.id.editAka);
        editName = findViewById(R.id.editName);
        editAlbum = findViewById(R.id.editAlbum);
        editSong = findViewById(R.id.editSong);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        loadingProgress = findViewById(R.id.loadingProgress);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void populateForm() {
        if (rapper != null) {
            textId.setText("ID: #" + rapper.getId());
            editAka.setText(rapper.getAka());
            editName.setText(rapper.getName());
            editAlbum.setText(rapper.getAlbum());
            editSong.setText(rapper.getSong());

            // Guardar valores originales para comparar cambios
            originalAka = rapper.getAka();
            originalName = rapper.getName();
            originalAlbum = rapper.getAlbum();
            originalSong = rapper.getSong();
        }
    }

    private void setupListeners() {
        buttonUpdate.setOnClickListener(v -> {
            if (validateForm()) {
                if (hasChanges()) {
                    showUpdateConfirmationDialog();
                } else {
                    Toast.makeText(EditRapperActivity.this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean hasChanges() {
        String newAka = editAka.getText().toString().trim();
        String newName = editName.getText().toString().trim();
        String newAlbum = editAlbum.getText().toString().trim();
        String newSong = editSong.getText().toString().trim();

        return !newAka.equals(originalAka) ||
                !newName.equals(originalName) ||
                !newAlbum.equals(originalAlbum) ||
                !newSong.equals(originalSong);
    }

    private void showUpdateConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Actualización");
        builder.setMessage("¿Estás seguro de que quieres actualizar la información de \"" + originalAka + "\"?\n\nLos cambios no se pueden deshacer.");

        builder.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateRapper();
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_green_dark));

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        });

        dialog.show();
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

    private void updateRapper() {
        loadingProgress.setVisibility(View.VISIBLE);
        buttonUpdate.setEnabled(false);

        String url = baseUrl + "/rappers/modificar/" + rapper.getId();

        StringRequest putRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingProgress.setVisibility(View.GONE);
                        buttonUpdate.setEnabled(true);

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");

                            Toast.makeText(EditRapperActivity.this, "✅ " + message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(EditRapperActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditRapperActivity.this, "❌ Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingProgress.setVisibility(View.GONE);
                        buttonUpdate.setEnabled(true);

                        error.printStackTrace();
                        Toast.makeText(EditRapperActivity.this, "❌ Error al actualizar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        requestQueue.add(putRequest);
    }
}