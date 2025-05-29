package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {

    private TextView tvAdminWelcome;
    private Button btnLogout;
    private CardView cardAddPlanning;
    private CardView cardAddDocument;  // Nouvelle carte

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        btnLogout = findViewById(R.id.btnLogoutAdmin);
        cardAddPlanning = findViewById(R.id.cardAddPlanning);
        cardAddDocument = findViewById(R.id.cardAddDocument); // Récupérer la carte

        SharedPreferences prefs = getSharedPreferences("admin_prefs", MODE_PRIVATE);
        String adminName = prefs.getString("admin_name", "Administrateur");
        tvAdminWelcome.setText("Bienvenue " + adminName + " !");

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(AdminActivity.this, Signin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Ouvrir AddPlanningActivity au clic sur la carte "Ajouter Planning"
        cardAddPlanning.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddPlanningActivity.class);
            startActivity(intent);
        });

        // Ouvrir AddDocumentActivity au clic sur la carte "Ajouter Document"
        cardAddDocument.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddDocumentActivity.class);
            startActivity(intent);
        });
    }
}
