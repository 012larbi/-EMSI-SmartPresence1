package com.example.emsismartpresence;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.adapters.ModifierEtudiantAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierAbsenceActivity extends AppCompatActivity {

    private RecyclerView rvModifierEtudiants;
    private ModifierEtudiantAdapter adapter;
    private List<Etudiant> etudiants;

    private Button btnSauvegarder;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_absences);

        rvModifierEtudiants = findViewById(R.id.rvModifierEtudiants);
        btnSauvegarder = findViewById(R.id.btnSauvegarderModifications);

        db = FirebaseFirestore.getInstance();

        etudiants = new ArrayList<>();
        adapter = new ModifierEtudiantAdapter(this, etudiants);
        rvModifierEtudiants.setLayoutManager(new LinearLayoutManager(this));
        rvModifierEtudiants.setAdapter(adapter);

        chargerEtudiants();

        btnSauvegarder.setOnClickListener(v -> sauvegarderModifications());
    }

    private void chargerEtudiants() {
        db.collection("absences")
                .get()
                .addOnSuccessListener(this::onEtudiantsRecuperes)
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur chargement étudiants", Toast.LENGTH_SHORT).show());
    }

    private void onEtudiantsRecuperes(QuerySnapshot snapshot) {
        etudiants.clear();
        for (var doc : snapshot.getDocuments()) {
            String id = doc.getId();
            String nom = doc.getString("nom");
            Boolean absent = doc.getBoolean("absent");
            String remarque = doc.getString("remarque");
            Etudiant etudiant = new Etudiant(id, nom);
            etudiant.setAbsent(absent != null && absent);
            etudiant.setRemarque(remarque != null ? remarque : "");
            etudiants.add(etudiant);
        }
        adapter.notifyDataSetChanged();
    }

    private void sauvegarderModifications() {
        for (Etudiant etudiant : etudiants) {
            Map<String, Object> data = new HashMap<>();
            data.put("nom", etudiant.getNom());
            data.put("absent", etudiant.isAbsent());
            data.put("remarque", etudiant.getRemarque());

            db.collection("absences")
                    .document(etudiant.getId())
                    .set(data)
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur sauvegarde: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        Toast.makeText(this, "Modifications sauvegardées", Toast.LENGTH_LONG).show();
    }
}
