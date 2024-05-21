package com.uablis.easyfitness;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SetObjective extends AppCompatActivity {
    private TextView objectiveDate, currentWieght;
    private EditText objectiveWeight;
    private ImageView backArrow;
    private Button buttonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_objective);

        bindViews();
        setupListeners();
    }

    private void bindViews() {

        objectiveDate = findViewById(R.id.objectiveDate);
        currentWieght = findViewById(R.id.currentWeight);
        objectiveWeight = findViewById(R.id.objectiveWeight);
        backArrow = findViewById(R.id.back_arrow);
        buttonSave = findViewById(R.id.btnSave);
    }

    private void setupListeners() {

        objectiveDate.setOnClickListener(v -> showDatePickerDialog());
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                SetObjective.this,
                (view, year, month, dayOfMonth) -> objectiveDate.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to change your objective weight?");
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

    public void backToScreen() {
        finish();
    }
}