package com.example.emsismartpresence;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.adapters.EtudiantAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListeAbsentsActivity extends AppCompatActivity {

    private RecyclerView rvAbsents;
    private EtudiantAdapter adapter;
    private List<Etudiant> absents;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_absents);

        rvAbsents = findViewById(R.id.rvAbsents);
        rvAbsents.setLayoutManager(new LinearLayoutManager(this));

        absents = new ArrayList<>();
        adapter = new EtudiantAdapter(this, absents);
        rvAbsents.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        chargerAbsents();
    }

    private void chargerAbsents() {
        db.collection("absences")
                .whereEqualTo("absent", true)
                .get()
                .addOnSuccessListener(this::onAbsentsRecuperes)
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur chargement absents", Toast.LENGTH_SHORT).show());
    }

    private void onAbsentsRecuperes(QuerySnapshot snapshot) {
        absents.clear();
        for (var doc : snapshot.getDocuments()) {
            String id = doc.getId();
            String nom = doc.getString("nom");
            String remarque = doc.getString("remarque");
            Etudiant etudiant = new Etudiant(id, nom);
            etudiant.setAbsent(true);
            etudiant.setRemarque(remarque != null ? remarque : "");
            absents.add(etudiant);
        }
        adapter.notifyDataSetChanged();
    }
}
