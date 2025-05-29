package com.example.emsismartpresence.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.R;
import com.example.emsismartpresence.Etudiant;

import java.util.List;

public class ModifierEtudiantAdapter extends RecyclerView.Adapter<ModifierEtudiantAdapter.ViewHolder> {

    private final List<Etudiant> etudiants;
    private final Context context;

    public ModifierEtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
    }

    @NonNull
    @Override
    public ModifierEtudiantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_modifier_etudiant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModifierEtudiantAdapter.ViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);

        holder.etNom.setText(etudiant.getNom());
        holder.cbAbsent.setChecked(etudiant.isAbsent());
        holder.etRemarque.setText(etudiant.getRemarque());

        // Nom
        if (holder.etNom.getTag() instanceof TextWatcher) {
            holder.etNom.removeTextChangedListener((TextWatcher) holder.etNom.getTag());
        }
        TextWatcher nomWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                etudiant.setNom(s.toString());
            }
        };
        holder.etNom.addTextChangedListener(nomWatcher);
        holder.etNom.setTag(nomWatcher);

        // Absent
        holder.cbAbsent.setOnCheckedChangeListener((buttonView, isChecked) -> etudiant.setAbsent(isChecked));

        // Remarque
        if (holder.etRemarque.getTag() instanceof TextWatcher) {
            holder.etRemarque.removeTextChangedListener((TextWatcher) holder.etRemarque.getTag());
        }
        TextWatcher remarqueWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                etudiant.setRemarque(s.toString());
            }
        };
        holder.etRemarque.addTextChangedListener(remarqueWatcher);
        holder.etRemarque.setTag(remarqueWatcher);
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etNom;
        CheckBox cbAbsent;
        EditText etRemarque;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etNom = itemView.findViewById(R.id.etNom);
            cbAbsent = itemView.findViewById(R.id.cbAbsent);
            etRemarque = itemView.findViewById(R.id.etRemarque);
        }
    }
}
