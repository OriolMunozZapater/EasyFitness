package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.search.SearchBar;

public class ChooseExerciseActivity extends AppCompatActivity {
    private SearchBar exerciseSearch;
    LinearLayout exerciseContainer;
    private Button selectMuscle, newExercise;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_exercise);

        //fer la logica de busca del searchbar
        exerciseSearch = findViewById(R.id.exerciseSearch);
        selectMuscle = findViewById(R.id.selectMuscle);
        backArrow = findViewById(R.id.back_arrow);
        newExercise = findViewById(R.id.btnNewExercise);

        String[] routineNames = {"bench press", "hack squad"};
        updateUIWithRoutines(routineNames);

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

    private void updateUIWithRoutines(String[] routineNames) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);

        for (String name : routineNames) {
            View routineView = getLayoutInflater().inflate(R.layout.exercise_row2, routinesLayout, false);
            TextView textView = routineView.findViewById(R.id.tvExerciseName);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton addToRoutine = routineView.findViewById(R.id.addToRoutine);

            textView.setText(name);

            addToRoutine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton button = (ImageButton) v;
                    button.setImageResource(R.drawable.green_check);
                }
            });
            routineView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            routinesLayout.addView(routineView);
        }
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
