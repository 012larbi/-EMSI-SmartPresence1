package com.example.emsismartpresence.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.R;
import com.example.emsismartpresence.Etudiant;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.ViewHolder> {

    private final List<Etudiant> etudiants;
    private final Context context;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        this.context = context;
        this.etudiants = etudiants;
    }

    @NonNull
    @Override
    public EtudiantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_etudiant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantAdapter.ViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);

        holder.tvNom.setText(etudiant.getNom());
        holder.cbAbsent.setChecked(etudiant.isAbsent());
        holder.etRemarque.setText(etudiant.getRemarque());

        holder.cbAbsent.setOnCheckedChangeListener((buttonView, isChecked) -> etudiant.setAbsent(isChecked));

        if (holder.etRemarque.getTag() instanceof TextWatcher) {
            holder.etRemarque.removeTextChangedListener((TextWatcher) holder.etRemarque.getTag());
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                etudiant.setRemarque(s.toString());
            }
        };

        holder.etRemarque.addTextChangedListener(textWatcher);
        holder.etRemarque.setTag(textWatcher);
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom;
        CheckBox cbAbsent;
        EditText etRemarque;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNomEtudiant);
            cbAbsent = itemView.findViewById(R.id.cbAbsent);
            etRemarque = itemView.findViewById(R.id.etRemarque);
        }
    }
}
