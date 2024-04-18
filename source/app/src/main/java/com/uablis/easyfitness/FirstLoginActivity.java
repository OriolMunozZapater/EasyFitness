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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class FirstLoginActivity extends AppCompatActivity {
    private EditText etWeight, etName, etSecondName, etHeight;
    private Button btnSexSelect;
    private ImageView forwardArrow;
    private TextView etDateOfBirth, textViewSex;
    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Croissant"};
    private Spinner genderSpinner;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        etWeight = findViewById(R.id.etWeight);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        btnSexSelect = findViewById(R.id.btnSexSelect);
        textViewSex = findViewById(R.id.textViewSex);
        forwardArrow = findViewById(R.id.forwardArrow);
        etHeight = findViewById(R.id.etHeight);
        etName = findViewById(R.id.etName);
        etSecondName = findViewById(R.id.etSecondName);

        toolbar = findViewById(R.id.toolbar);

        forwardArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextScreen();
            }
        });

        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la fecha actual
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Crear y mostrar el DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        FirstLoginActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Actualizar el texto del EditText con la fecha seleccionada
                                etDateOfBirth.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
    }

    public void showSexoOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu sexo");
        builder.setItems(genderOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedSexo = genderOptions[which].toString();
                btnSexSelect.setText(selectedSexo);
            }
        });
        builder.show();
    }

    public void nextScreen() {
        String aux_weight = etWeight.getText().toString().trim();
        double weight = Double.parseDouble(aux_weight);

        String birthDate = etDateOfBirth.getText().toString().trim();
        String sex = btnSexSelect.getText().toString().trim();

        Intent intent = new Intent(FirstLoginActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
    }
}
