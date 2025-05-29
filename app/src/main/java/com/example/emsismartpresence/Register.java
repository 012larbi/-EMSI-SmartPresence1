package com.example.emsismartpresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmePassword, etName;
    private Button btnRegister;
    private final String validEmail = "user@example.com";
    private final String validPassword = "123456";
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
// Récupération des éléments UI
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmePassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_Register);
        etName = findViewById(R.id.name);


// Gestion du clic sur le bouton
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String nameted = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confpassword = etConfirmePassword.getText().toString().trim();
        if (nameted.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confpassword.equals(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(this, "Connexion réussie!",Toast.LENGTH_SHORT).show();
                            userId=mAuth.getCurrentUser().getUid();
                            store_user_firestore(userId,email,nameted);
                            Intent i=new Intent(Register.this, Signin.class);
                            i.putExtra("name", nameted);
                            startActivity(i);
                        }else {
                            Toast.makeText(this, "Erreur: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {

            Toast.makeText(this, "mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }

                /*      if (email.equals(validEmail) && password.equals(validPassword)) {
                        Toast.makeText(this, "Authentification réussie !", Toast.LENGTH_SHORT).show();
                } else {

                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }*/
    }

    private void store_user_firestore(String uid, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("user_email", email);
        userData.put("date_inscription", new Timestamp(new Date()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Register.this, "Inscription réussie, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Erreur lors de l'enregistrement: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

}