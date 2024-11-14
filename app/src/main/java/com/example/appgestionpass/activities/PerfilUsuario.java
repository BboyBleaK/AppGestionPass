package com.example.appgestionpass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.appgestionpass.R;
import com.example.appgestionpass.models.Password;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

import java.util.concurrent.Executor;

public class PerfilUsuario extends AppCompatActivity {

    //Definición de variables

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference passwordsRef;

    private EditText siteEditText, usernameEditText, passwordEditText;
    private Button addPasswordButton, viewPasswordButton, logoutButton;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        // Tomando las variables

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        passwordsRef = db.collection("users").document(userId).collection("passwords");

        siteEditText = findViewById(R.id.siteEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addPasswordButton = findViewById(R.id.addPasswordButton);
        viewPasswordButton = findViewById(R.id.viewPasswordButton);
        logoutButton = findViewById(R.id.logoutButton);

        executor = ContextCompat.getMainExecutor(this);

        //Implementacion del biometric

        biometricPrompt = new BiometricPrompt(PerfilUsuario.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                openViewPasswordsActivity();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Toast.makeText(PerfilUsuario.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(PerfilUsuario.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ver contraseñas")
                .setSubtitle("Autenticación biométrica requerida")
                .setNegativeButtonText("Cancelar")
                .build();

        addPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPassword();
            }
        });

        viewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateAndViewPasswords();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        auth.signOut();
        startActivity(new Intent(PerfilUsuario.this, LoginActivity.class));
        finish();
    }

    private void authenticateAndViewPasswords() {
        biometricPrompt.authenticate(promptInfo);
    }

    private void openViewPasswordsActivity() {
        String userId = auth.getCurrentUser().getUid();
        Intent intent = new Intent(PerfilUsuario.this, VerPassActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    private void addPassword() {
        String site = siteEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(site) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(PerfilUsuario.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto Password sin ID aún
        Password newPassword = new Password(null, site, username, password);

        // Añadir el documento y obtener el ID generado por Firestore
        passwordsRef.add(newPassword)
                .addOnSuccessListener(documentReference -> {
                    newPassword.setId(documentReference.getId());
                    documentReference.update("id", newPassword.getId())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(PerfilUsuario.this, "Contraseña añadida", Toast.LENGTH_SHORT).show();
                                clearFields();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PerfilUsuario.this, "Error al guardar ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PerfilUsuario.this, "Error al añadir contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    //Limpia los campos una vez utilizados

    private void clearFields() {
        siteEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
    }
}
