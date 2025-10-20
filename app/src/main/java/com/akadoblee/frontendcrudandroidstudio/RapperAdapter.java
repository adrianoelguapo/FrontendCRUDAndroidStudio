package com.akadoblee.frontendcrudandroidstudio;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class RapperAdapter extends RecyclerView.Adapter<RapperAdapter.RapperViewHolder> {

    private List<Rapper> rapperList;
    private RequestQueue requestQueue;
    private Context context;
    private String baseUrl;

    public RapperAdapter(List<Rapper> rapperList, Context context) {
        this.rapperList = rapperList;
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.baseUrl = context.getString(R.string.base_url);
    }

    @NonNull
    @Override
    public RapperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rapper_card, parent, false);
        return new RapperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RapperViewHolder holder, int position) {
        Rapper rapper = rapperList.get(position);
        holder.textAka.setText(rapper.getAka());
        holder.textName.setText(rapper.getName());
        holder.textAlbum.setText(rapper.getAlbum());
        holder.textSong.setText(rapper.getSong());
        holder.textId.setText("#" + rapper.getId());

        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRapperActivity.class);
            intent.putExtra("rapper", rapper);
            context.startActivity(intent);
        });

        holder.buttonDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(rapper.getId(), position, rapper.getAka());
        });
    }

    private void showDeleteConfirmationDialog(int rapperId, int position, String aka) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmar Eliminación");
        builder.setMessage("¿Estás seguro de que quieres eliminar a \"" + aka + "\"?\n\nEsta acción no se puede deshacer.");

        builder.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRapper(rapperId, position, aka);
            }
        });

        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Personalizar el diálogo
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Cambiar color del botón positivo (rojo para eliminar)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                // Cambiar color del botón negativo (gris para cancelar)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        });

        dialog.show();
    }

    private void deleteRapper(int rapperId, int position, String aka) {
        String url = baseUrl + "/rappers/borrar/" + rapperId;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");

                            rapperList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, rapperList.size());

                            Toast.makeText(context, "✅ " + message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "❌ Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "❌ Error al eliminar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(deleteRequest);
    }

    @Override
    public int getItemCount() {
        return rapperList.size();
    }

    static class RapperViewHolder extends RecyclerView.ViewHolder {
        TextView textAka, textName, textAlbum, textSong, textId;
        CardView buttonEdit, buttonDelete;

        public RapperViewHolder(@NonNull View itemView) {
            super(itemView);
            textAka = itemView.findViewById(R.id.textAka);
            textName = itemView.findViewById(R.id.textName);
            textAlbum = itemView.findViewById(R.id.textAlbum);
            textSong = itemView.findViewById(R.id.textSong);
            textId = itemView.findViewById(R.id.textId);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}