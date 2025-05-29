package com.example.emsismartpresence;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.adapters.EtudiantTableauAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListeEtudiantsActivity extends AppCompatActivity {

    private RecyclerView rvEtudiants;
    private EtudiantTableauAdapter adapter;
    private List<Etudiant> etudiants;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_etudiants);

        rvEtudiants = findViewById(R.id.rvEtudiants);
        rvEtudiants.setLayoutManager(new LinearLayoutManager(this));

        etudiants = new ArrayList<>();
        adapter = new EtudiantTableauAdapter(this, etudiants);
        rvEtudiants.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        chargerEtudiants();
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
}
