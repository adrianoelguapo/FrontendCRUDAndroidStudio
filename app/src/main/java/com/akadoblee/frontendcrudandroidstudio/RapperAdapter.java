package com.akadoblee.frontendcrudandroidstudio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RapperAdapter extends RecyclerView.Adapter<RapperAdapter.RapperViewHolder> {

    private List<Rapper> rapperList;

    public RapperAdapter(List<Rapper> rapperList) {
        this.rapperList = rapperList;
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

        // Configurar listeners para los botones
        holder.buttonEdit.setOnClickListener(v -> {
            // Lógica para editar (pendiente)
            Toast.makeText(v.getContext(), "Editar: " + rapper.getAka(), Toast.LENGTH_SHORT).show();
        });

        holder.buttonDelete.setOnClickListener(v -> {
            // Lógica para eliminar (pendiente)
            Toast.makeText(v.getContext(), "Eliminar: " + rapper.getAka(), Toast.LENGTH_SHORT).show();
        });
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