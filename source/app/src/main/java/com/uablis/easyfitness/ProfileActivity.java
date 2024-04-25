package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView home, training_routines, training, profile, profile_image;

    private Toolbar toolbar, appbar;

    private ImageButton edit_profile;

    private TextView pesObjectiu, pesPropi, nomUser, sexe, altura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        recuperarDatosPerfil();

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        toolbar = findViewById(R.id.toolbar);
        edit_profile = findViewById(R.id.btnEditPersonalData);

        edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditData();
                }
            }
        );

    }
    public void recuperarDatosPerfil() {
        // Obtener referencias a los elementos de la interfaz
        TextView pesoObjetivoTextView = findViewById(R.id.pesObjectiu);
        TextView pesoActualTextView = findViewById(R.id.pesPropi);
        TextView nombreTextView = findViewById(R.id.nomUser);
        TextView alturaTextView = findViewById(R.id.altura);
        TextView sexoTextView = findViewById(R.id.sexe);

        // Recuperar datos de BD

        // Peso objetivo
        /*int pesoObjetivo = preferencias.getInt("peso_objetivo", 0);
        pesoObjetivoTextView.setText(String.valueOf(pesoObjetivo) + " Kg");

        // Peso actual
        int pesoActual = preferencias.getInt("peso_actual", 0);
        pesoActualTextView.setText(String.valueOf(pesoActual) + " Kg");

        // Nombre
        String nombre = preferencias.getString("nombre", "");
        nombreTextView.setText(nombre);

        // Altura
        int altura = preferencias.getInt("altura", 0);
        alturaTextView.setText(String.valueOf(altura) + " cm");

        // Sexo
        String sexo = preferencias.getString("sexo", "");
        sexoTextView.setText(sexo);*/
    }

    public void goToEditData() {
        Intent intent = new Intent(ProfileActivity.this, /*CAMBIAR LO SIGUIENTE POR EDITAR_DETALLS_PERFIL*/EditRoutineActivity.class);
        startActivity(intent);
    }
}

