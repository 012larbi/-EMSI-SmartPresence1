package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private String name;
    private Button btnLogin;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;


    private final String validEmail = "user@example.com";
    private final String validPassword = "123456";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
// Récupération des éléments UI
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);

        // Set up password toggle
        btnTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });



// Gestion du clic sur le bouton
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });

        name = getIntent().getStringExtra("name");
        // Affichage simple
        Toast.makeText(this, "Bienvenue: " + name,
                Toast.LENGTH_SHORT).show();
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        }

        // Move cursor to end of text
        etPassword.setSelection(etPassword.getText().length());
    }

    private void authenticateUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
                /*      if (email.equals(validEmail) && password.equals(validPassword)) {
                        Toast.makeText(this, "Authentification réussie !", Toast.LENGTH_SHORT).show();
                } else {

                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }*/
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Connexion réussie!",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(Signin.this, MainActivity.class);
                        i.putExtra("name", name);
                        startActivity(i);
                        finish();
                    }else {
                        Toast.makeText(this, "Erreur: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

}