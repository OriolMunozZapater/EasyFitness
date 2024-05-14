package com.uablis.easyfitness;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AppReviewActivity extends AppCompatActivity {
    private EditText nameUser, correuElectronic, description;
    private ImageButton removeInfo;
    private Button btnEnviarReview;
    private ImageView backArrow;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        nameUser = findViewById(R.id.nameUser);
        correuElectronic = findViewById(R.id.correuElectronic);
        description = findViewById(R.id.description);
        removeInfo = findViewById(R.id.removeInfo);
        btnEnviarReview = findViewById(R.id.btnEnviarReview);
        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);

        btnEnviarReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReview();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Terminar la actividad y volver a la anterior
            }
        });

        removeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.warning_init).setVisibility(View.GONE);
            }
        });

        // Configurar la barra de herramientas
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void enviarReview() {
        String userName = nameUser.getText().toString();
        String userEmail = correuElectronic.getText().toString();
        String reviewDescription = description.getText().toString();

        // Aquí puedes agregar lógica para validar los campos (nombre de usuario, correo electrónico, descripción)

        // Simplemente mostraremos un mensaje de toast para demostrar que la revisión se ha enviado
        Toast.makeText(this, "Revisión enviada por: " + userName, Toast.LENGTH_SHORT).show();
    }
}
