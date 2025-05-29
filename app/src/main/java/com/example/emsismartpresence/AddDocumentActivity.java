package com.example.emsismartpresence;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class AddDocumentActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private EditText etTitle;
    private RadioGroup rgType;
    private Button btnSelectFile, btnSave;
    private Uri fileUri;
    private String documentId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        etTitle = findViewById(R.id.etTitle);
        rgType = findViewById(R.id.rgType);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Vérifier si on est en mode édition
        documentId = getIntent().getStringExtra("DOCUMENT_ID");
        if (documentId != null) {
            etTitle.setText(getIntent().getStringExtra("DOCUMENT_TITLE"));
            int selection = getIntent().getStringExtra("DOCUMENT_TYPE").equals("pdf") ? R.id.rbPdf : R.id.rbImage;
            rgType.check(selection);
        }

        btnSelectFile.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveDocument());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            btnSelectFile.setText("Fichier sélectionné");
        }
    }

    private void saveDocument() {
        String title = etTitle.getText().toString().trim();
        int selectedId = rgType.getCheckedRadioButtonId();
        RadioButton selectedRadio = findViewById(selectedId);
        String type = selectedRadio.getText().toString().toLowerCase();

        if (title.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un titre", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUri == null && documentId == null) {
            Toast.makeText(this, "Veuillez sélectionner un fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fileUri != null) {
            uploadFile(title, type);
        } else {
            updateDocument(title, type, getIntent().getStringExtra("DOCUMENT_URL"));
        }
    }

    private void uploadFile(String title, String type) {
        StorageReference fileRef = storage.getReference()
                .child("documents/" + System.currentTimeMillis() + "_" + title);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveToFirestore(title, uri.toString(), type)))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Échec de l'upload", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Upload failed", e);
                });
    }

    private void saveToFirestore(String title, String fileUrl, String type) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("url", fileUrl);
        doc.put("type", type);
        doc.put("timestamp", System.currentTimeMillis());

        if (documentId == null) {
            db.collection("documents")
                    .add(doc)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "Document ajouté", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur d'ajout", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Add failed", e);
                    });
        } else {
            db.collection("documents").document(documentId)
                    .update(doc)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Document modifié", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur de modification", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Update failed", e);
                    });
        }
    }

    private void updateDocument(String title, String type, String fileUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("type", type);
        updates.put("timestamp", System.currentTimeMillis());

        db.collection("documents").document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Document modifié", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de modification", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update failed", e);
                });
    }
}























