package com.uablis.easyfitness;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EditRoutineActivity extends AppCompatActivity {
    private ImageView home, training_routines, training, profile;
    LinearLayout exerciseContainer;
    private EditText etEditRoutineName;
    private ImageView backArrow;
    private Button btnAddExercise, btnSave;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        backArrow = findViewById(R.id.back_arrow);
        btnAddExercise = findViewById(R.id.btnAddExercise1);
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        etEditRoutineName = findViewById(R.id.etEditRoutineName);
        btnSave = findViewById(R.id.btnSave);

        String[] routineNames = {"bench press", "hack squad"};
        updateUIWithRoutines(routineNames);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRoutines();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddExerciseScreen();
            }
        });
    }

    private void updateUIWithRoutines(String[] routineNames) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);

        for (String name : routineNames) {
            View routineView = getLayoutInflater().inflate(R.layout.exercise_row, routinesLayout, false);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = routineView.findViewById(R.id.tvExerciseName);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton eliminateCross = routineView.findViewById(R.id.eliminate_cross);

            textView.setText(name);

            eliminateCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteExercise();
                }
            });
            routinesLayout.addView(routineView);
        }
    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to modificate this routine?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //afegir rutina a la bd
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el diálogo
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void goToRoutines() {
        Intent intent = new Intent(EditRoutineActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
    }

    public void goToEditExercise(){
        Intent intent = new Intent(EditRoutineActivity.this, EditExerciseActivity.class);
        startActivity(intent);
    }
    public void backToScreen() {
        finish();
    }

    public void deleteExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de eliminar?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción a realizar si se elige "No"
                        dialog.cancel(); // Cierra la alerta
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToAddExerciseScreen() {
        Intent intent = new Intent(EditRoutineActivity.this, ChooseExerciseActivity.class);
        startActivity(intent);
    }
}