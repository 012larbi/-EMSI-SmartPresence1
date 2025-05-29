package com.example.emsismartpresence;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlanningActivity extends AppCompatActivity {

    private static final String TAG = "PlanningActivity";
    private TextView tvPlanningContent;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private CardView cardEmploiDuTemps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        // Initialisation
        tvPlanningContent = findViewById(R.id.tvPlanningContent);
        cardEmploiDuTemps = findViewById(R.id.cardEmploiDuTemps); // Assurez-vous d'avoir cette CardView dans votre layout
        db = FirebaseFirestore.getInstance();

        // Configurer le clic sur la card
        cardEmploiDuTemps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer vers EmploiDeTemp
                Intent intent = new Intent(PlanningActivity.this, EmploiDeTemp.class);
                startActivity(intent);
            }
        });

        // Configurer l'écouteur en temps réel
        setupRealTimeListener();
    }

    private void setupRealTimeListener() {
        registration = db.collection("plannings")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Erreur d'écoute", error);
                        tvPlanningContent.setText("Erreur de chargement");
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

                        for (QueryDocumentSnapshot document : value) {
                            String text = document.getString("text");
                            Long timestamp = document.getLong("timestamp");

                            if (text != null && timestamp != null) {
                                sb.append("• ").append(text)
                                        .append("\nDate: ")
                                        .append(sdf.format(new Date(timestamp)))
                                        .append("\n\n");
                            }
                        }
                        tvPlanningContent.setText(sb.toString());
                    } else {
                        tvPlanningContent.setText("voir l'emploi du temps ");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Désinscrire l'écouteur quand l'activité est détruite
        if (registration != null) {
            registration.remove();
        }
    }
}