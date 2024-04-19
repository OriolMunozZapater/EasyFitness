package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.search.SearchBar;

public class ChooseExerciseActivity extends AppCompatActivity {
    private SearchBar exerciseSearch;
    private Button selectMuscle, newExercise;
    private ImageView backArrow;
    private LinearLayout exercise;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_exercise);

        //fer la logica de busca del searchbar
        exerciseSearch = findViewById(R.id.exerciseSearch);
        selectMuscle = findViewById(R.id.selectMuscle);
        backArrow = findViewById(R.id.back_arrow);
        newExercise = findViewById(R.id.btnNewExercise);
        exercise = findViewById(R.id.exercise);

        exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //afegir ex a la rutina
                finish();
            }
        });
        newExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewExercise();
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });

        selectMuscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMuscle();
            }
        });
    }

    public void goToNewExercise() {
        Intent intent = new Intent(ChooseExerciseActivity.this, CreateNewExercise.class);
        startActivity(intent);
    }

    public void backScreen() {
        finish();
    }


    public void selectMuscle() {
        // Obtenemos los nombres de los músculos desde los recursos de strings
        final String[] muscleNames = getResources().getStringArray(R.array.muscle_options);

        // Creamos un AlertDialog con la lista de músculos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un músculo")
                .setItems(muscleNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción a realizar cuando se selecciona un músculo
                        String selectedMuscle = muscleNames[which];
                        // Puedes hacer algo con el músculo seleccionado, como mostrar un Toast
                        Toast.makeText(getApplicationContext(), "Has seleccionado: " + selectedMuscle, Toast.LENGTH_SHORT).show();
                        selectMuscle.setText(selectedMuscle);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
