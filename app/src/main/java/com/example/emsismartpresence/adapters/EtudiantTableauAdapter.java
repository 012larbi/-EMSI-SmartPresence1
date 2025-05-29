package com.example.emsismartpresence.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.R;
import com.example.emsismartpresence.Etudiant;

import java.util.List;

public class EtudiantTableauAdapter extends RecyclerView.Adapter<EtudiantTableauAdapter.ViewHolder> {

    private final List<Etudiant> etudiants;
    private final Context context;

    public EtudiantTableauAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
    }

    @NonNull
    @Override
    public EtudiantTableauAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_etudiant_tableau, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantTableauAdapter.ViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);
        holder.tvNom.setText(etudiant.getNom());
        holder.tvAbsent.setText(etudiant.isAbsent() ? "Absent" : "Present");
        holder.tvRemarque.setText(etudiant.getRemarque() != null ? etudiant.getRemarque() : "");
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvAbsent, tvRemarque;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNom);
            tvAbsent = itemView.findViewById(R.id.tvAbsent);
            tvRemarque = itemView.findViewById(R.id.tvRemarque);
        }
    }
}
