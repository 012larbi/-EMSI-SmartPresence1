package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddPlanningActivity extends AppCompatActivity {

    private EditText etPlanningText;
    private Button btnAddPlanning, btnViewPlannings;
    private FirebaseFirestore db;
    private String planningId = null; // Pour la modification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_planning);

        etPlanningText = findViewById(R.id.etPlanningText);
        btnAddPlanning = findViewById(R.id.btnAddPlanning);
        btnViewPlannings = findViewById(R.id.btnViewPlannings);
        db = FirebaseFirestore.getInstance();

        // Vérifier si on est en mode modification
        planningId = getIntent().getStringExtra("PLANNING_ID");
        if (planningId != null) {
            btnAddPlanning.setText("Modifier");
            etPlanningText.setText(getIntent().getStringExtra("PLANNING_TEXT"));
        }

        btnAddPlanning.setOnClickListener(v -> {
            if (planningId == null) {
                addPlanning();
            } else {
                updatePlanning();
            }
        });

        btnViewPlannings.setOnClickListener(v -> {
            startActivity(new Intent(this, PlanningActivity.class));
            finish();
        });
    }

    private void addPlanning() {
        String planningText = etPlanningText.getText().toString().trim();

        if (planningText.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un planning", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> planning = new HashMap<>();
        planning.put("text", planningText);
        planning.put("timestamp", System.currentTimeMillis());

        db.collection("plannings")
                .add(planning)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Planning ajouté avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                });
    }

    private void updatePlanning() {
        String planningText = etPlanningText.getText().toString().trim();

        if (planningText.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un planning", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("text", planningText);
        updates.put("timestamp", System.currentTimeMillis());

        db.collection("plannings").document(planningId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Planning modifié avec succès", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                });
    }
}