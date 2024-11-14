package com.example.appgestionpass.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appgestionpass.R;
import com.example.appgestionpass.adapters.PasswordAdapter;
import com.example.appgestionpass.models.Password;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class VerPassActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PasswordAdapter passwordAdapter;
    private List<Password> passwordList;
    private String userId;  // Añadimos la variable userId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_pass);

        // Inicialización de Firestore
        db = FirebaseFirestore.getInstance();


        userId = getIntent().getStringExtra("USER_ID");


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lista de contraseñas
        passwordList = new ArrayList<>();
        passwordAdapter = new PasswordAdapter(this, passwordList, userId);  // Pasamos el userId al adaptador
        recyclerView.setAdapter(passwordAdapter);

        // Cargar las contraseñas de la base de datos
        loadPasswords();
    }

    private void loadPasswords() {
        if (userId != null) {
            db.collection("users").document(userId).collection("passwords")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            passwordList.clear();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Password password = documentSnapshot.toObject(Password.class);
                                passwordList.add(password);
                            }
                            passwordAdapter.notifyDataSetChanged();  // Actualiza el RecyclerView con los nuevos datos
                        } else {
                            Toast.makeText(VerPassActivity.this, "No se encontraron contraseñas", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(VerPassActivity.this, "Error al cargar las contraseñas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
