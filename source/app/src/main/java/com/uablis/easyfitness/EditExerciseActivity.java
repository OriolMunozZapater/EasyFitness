package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditExerciseActivity extends AppCompatActivity {

    private EditText exName;
    private EditText exDesc;
    private EditText exTipo;
    private RatingBar exExerciseRatingBar;
    private ImageView home, training_routines, training, profile;
    private Button btnAddExercise;
    private Toolbar toolbar;
    private ImageView backArrow;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        exName = findViewById(R.id.exName);
        exDesc = findViewById(R.id.exDesc);
        exTipo = findViewById(R.id.exTipo);
        exExerciseRatingBar = findViewById(R.id.exExerciseRating);
        btnAddExercise = findViewById(R.id.btnSaveExeriseEdit);

        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.backArrow);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextScreen();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backPressed();
            }
        });
    }
    public void nextScreen() {
        String exerciseName = exName.getText().toString();
        String exerciseDescription = exDesc.getText().toString();
        String exerciseType = exTipo.getText().toString();
        float exerciseRating = exExerciseRatingBar.getRating();

        finish();
    }

    public void backPressed() {
        finish();
    }
}