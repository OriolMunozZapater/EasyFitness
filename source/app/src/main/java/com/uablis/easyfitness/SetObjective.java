package com.uablis.easyfitness;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SetObjective extends AppCompatActivity {
    private TextView objectiveDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_objective);

        bindViews();
        setupListeners();
    }

    private void bindViews() {
        objectiveDate = findViewById(R.id.objectiveDate);
    }

    private void setupListeners() {
        objectiveDate.setOnClickListener(v -> showDatePickerDialog());
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
}
