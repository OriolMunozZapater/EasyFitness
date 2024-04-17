package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditRoutineActivity extends AppCompatActivity {
    private EditText etEditRoutineName;
    private Button btnAddExercise;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        etEditRoutineName = findViewById(R.id.etEditRoutineName);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddExerciseScreen();
            }
        });
    }

    public void goToAddExerciseScreen() {
        Intent intent = new Intent(EditRoutineActivity.this, ChooseExerciseActivity.class);
        startActivity(intent);
    }
}