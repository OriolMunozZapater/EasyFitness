package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class StartingRoutineActivity extends AppCompatActivity {
    private Chronometer trainingDuration;
    LinearLayout seriesContainer;
    ImageButton saveSerie;
    private Button btnAddSerie, endTraining;
    private int serieCount = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_routine);

        trainingDuration = findViewById(R.id.chronometer);
        seriesContainer = findViewById(R.id.seriesContainer);
        btnAddSerie = findViewById(R.id.btnAddSerie);
        endTraining = findViewById(R.id.btnEndTraining);
        saveSerie = findViewById(R.id.saveSerie);

        saveSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSerie1();
            }
        });

        endTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRoutine();
            }
        });
        btnAddSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSerie();
            }
        });

        trainingDuration.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                int minutes = (int) (elapsedMillis / 60000);
                int seconds = (int) ((elapsedMillis % 60000) / 1000);
                chronometer.setText(String.format("%02dmin %02ds", minutes, seconds));
            }
        });

        // Iniciar el cronómetro
        trainingDuration.setBase(SystemClock.elapsedRealtime());
        trainingDuration.start();
    }

    public void saveSerie1() {
        // Cambiar el color de fondo del LinearLayout newRow
        int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
        LinearLayout serierow = findViewById(R.id.serieRow);
        serierow.setBackgroundColor(newColor);
    }

    public void endRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to end this training session?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //guardar les vaines a la bd
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

    private void addNewSerie() {
        // Inflar la vista de fila de serie desde XML
        LinearLayout newRow = (LinearLayout) getLayoutInflater().inflate(R.layout.row_serie, null);

        // Alternar el color de fondo
        int bgColor = ContextCompat.getColor(this, serieCount % 2 == 0 ? R.color.layout_type2 : R.color.layout_type1);
        newRow.setBackgroundColor(bgColor);

        // Establecer ID único para la nueva fila
        newRow.setId(View.generateViewId());

        // Asignar un margen superior a la nueva fila
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0);
        newRow.setLayoutParams(layoutParams);

        // Obtener el EditText para el número de serie y asignarle el valor del contador de serie
        EditText etNumSerie = newRow.findViewById(R.id.etNumSerie);
        etNumSerie.setText(String.valueOf(serieCount));

        // Incrementar el contador de serie
        serieCount++;

        ImageButton saveSerie = newRow.findViewById(R.id.saveSerie);
        saveSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar el color de fondo del LinearLayout newRow
                int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                newRow.setBackgroundColor(newColor);
            }
        });

        // Agregar la nueva fila al contenedor
        seriesContainer.addView(newRow);
    }
}