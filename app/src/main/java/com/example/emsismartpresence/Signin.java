package com.example.emsismartpresence;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class Signin extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageButton btnTogglePassword;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;

    private static final String ADMIN_EMAIL = "admin@emsi_edu.ma";
    private static final String ADMIN_PASSWORD = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérification de session admin dans SharedPreferences
        SharedPreferences prefs = getSharedPreferences("admin_prefs", MODE_PRIVATE);
        if (prefs.contains("admin_email")) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Vérification de session utilisateur Firebase
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signin);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);

        findViewById(R.id.tv_register).setOnClickListener(v -> {
            startActivity(new Intent(Signin.this, Register.class));
            finish();
        });

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnLogin.setOnClickListener(v -> authenticateUser());
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
        etPassword.setSelection(etPassword.getText().length());
    }

    private void authenticateUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            // Sauvegarde des infos admin dans SharedPreferences
            SharedPreferences prefs = getSharedPreferences("admin_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("admin_email", email);
            editor.putString("admin_name", "Administrateur EMSI");
            editor.apply();

            Toast.makeText(this, "Connexion administrateur réussie!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Signin.this, AdminActivity.class));
            finish();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userName = email.split("@")[0];
                        Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Signin.this, MainActivity.class);
                        intent.putExtra("name", userName);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
