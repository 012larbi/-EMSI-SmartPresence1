package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsismartpresence.adapters.EtudiantAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsenceActivity extends AppCompatActivity {

    private RecyclerView rvEtudiants;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiants;

    private Button btnEnregistrer, btnVoirAbsents, btnModifierAbsences;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);

        rvEtudiants = findViewById(R.id.rvEtudiants);
        btnEnregistrer = findViewById(R.id.btnEnregistrer);
        btnVoirAbsents = findViewById(R.id.btnVoirAbsents);
        btnModifierAbsences = findViewById(R.id.btnModifierAbsences);

        db = FirebaseFirestore.getInstance();

        etudiants = new ArrayList<>();
        // Exemple statique — remplacez par données dynamiques si besoin
        etudiants.add(new Etudiant("et1", "Larbi"));
        etudiants.add(new Etudiant("et2", "Sara"));
        etudiants.add(new Etudiant("et3", "Mohamed"));
        etudiants.add(new Etudiant("et4", "Omar"));
        etudiants.add(new Etudiant("et5", "Alae"));
        etudiants.add(new Etudiant("et6", "Hamza"));
        etudiants.add(new Etudiant("et7", "Mouad"));
        etudiants.add(new Etudiant("et8", "Karim"));
        etudiants.add(new Etudiant("et9", "Hakim"));
        etudiants.add(new Etudiant("et10", "Salma"));
        etudiants.add(new Etudiant("et11", "Amine"));


        adapter = new EtudiantAdapter(this, etudiants);
        rvEtudiants.setLayoutManager(new LinearLayoutManager(this));
        rvEtudiants.setAdapter(adapter);

        btnEnregistrer.setOnClickListener(v -> enregistrerAbsences());

        btnVoirAbsents.setOnClickListener(v -> {
            Intent intent = new Intent(AbsenceActivity.this, ListeEtudiantsActivity.class);
            startActivity(intent);
        });

        btnModifierAbsences.setOnClickListener(v -> {
            Intent intent = new Intent(AbsenceActivity.this, ModifierAbsenceActivity.class);
            startActivity(intent);
        });
    }

    private void enregistrerAbsences() {
        for (Etudiant etudiant : etudiants) {
            Map<String, Object> data = new HashMap<>();
            data.put("nom", etudiant.getNom());
            data.put("absent", etudiant.isAbsent());
            data.put("remarque", etudiant.getRemarque());

            db.collection("absences")
                    .document(etudiant.getId())
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Optionnel : feedback par étudiant
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur sauvegarde: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        Toast.makeText(this, "Absences enregistrées avec succès", Toast.LENGTH_LONG).show();
    }
}
