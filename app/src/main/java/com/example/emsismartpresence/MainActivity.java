package com.example.emsismartpresence;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView ivProfile;
    private TextView tvWelcome, tvAdminName;
    private Button btnLogout;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivProfile = findViewById(R.id.ivProfile);
        tvWelcome = findViewById(R.id.myDashboard);
        tvAdminName = findViewById(R.id.dashboard_adminName);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ivProfile.setOnClickListener(v -> openImageChooser());

        // Ouvrir Maps au clic sur la carte Maps
        CardView cardMaps = findViewById(R.id.card5);
        cardMaps.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Maps.class);
            startActivity(intent);
        });

        // Ouvrir AssistantVirtuelActivity au clic sur le bouton Gemini
        Button btnGemini = findViewById(R.id.btnGemini);
        btnGemini.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AssistantVirtuelActivity.class);
            startActivity(intent);
        });

        // Ouvrir AbsenceActivity au clic sur la carte Absence
        CardView cardAbsence = findViewById(R.id.card2);
        cardAbsence.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AbsenceActivity.class);
            startActivity(intent);
        });

        // Ouvrir PlanningActivity au clic sur la carte Planning
        CardView cardPlanning = findViewById(R.id.card6);
        cardPlanning.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlanningActivity.class);
            startActivity(intent);
        });

        // Ouvrir DocumentActivity au clic sur la carte Document
        CardView cardDocument = findViewById(R.id.card1);
        cardDocument.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DocumentActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, Signin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            tvWelcome.setText("Welcome");

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = user.getEmail();

                            if (name == null || name.trim().isEmpty()) {
                                if (email != null && email.contains("@")) {
                                    name = email.substring(0, email.indexOf("@"));
                                } else {
                                    name = "Utilisateur";
                                }
                            }

                            tvAdminName.setText(name);
                            loadProfileImage(user.getUid());

                        } else {
                            String email = user.getEmail();
                            String fallbackName = "Utilisateur";
                            if (email != null && email.contains("@")) {
                                fallbackName = email.substring(0, email.indexOf("@"));
                            }
                            tvAdminName.setText(fallbackName);
                            ivProfile.setImageResource(R.drawable.persone);
                        }
                    })
                    .addOnFailureListener(e -> {
                        String email = user.getEmail();
                        String fallbackName = "Utilisateur";
                        if (email != null && email.contains("@")) {
                            fallbackName = email.substring(0, email.indexOf("@"));
                        }
                        tvAdminName.setText(fallbackName);
                        ivProfile.setImageResource(R.drawable.persone);
                    });
        } else {
            tvAdminName.setText("Utilisateur");
            ivProfile.setImageResource(R.drawable.persone);
        }
    }

    private void loadProfileImage(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("profileImageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.persone)
                                    .error(R.drawable.persone)
                                    .into(ivProfile);
                        } else {
                            ivProfile.setImageResource(R.drawable.persone);
                        }
                    }
                })
                .addOnFailureListener(e -> ivProfile.setImageResource(R.drawable.persone));
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(ivProfile);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || imageUri == null) {
            Toast.makeText(this, "Utilisateur non connecté ou image invalide.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileRef = storageRef.child("profile_images/" + user.getUid() + ".jpg");

        UploadTask uploadTask = fileRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            updateUserProfileImageUrl(downloadUrl, user.getUid());
        })).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Échec du téléversement: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateUserProfileImageUrl(String imageUrl, String userId) {
        db.collection("users").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Photo de profil mise à jour.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erreur mise à jour photo.", Toast.LENGTH_SHORT).show());
    }
}
