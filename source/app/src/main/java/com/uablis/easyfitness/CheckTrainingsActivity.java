package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CheckTrainingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trainings);

        String[] routineNames = {"superaniol", "aniolpeirna"};
        updateUIWithRoutines(routineNames);
    }

    private void updateUIWithRoutines(String[] routineNames) {
        LinearLayout routinesLayout = findViewById(R.id.trainingContainer);

        for (String name : routineNames) {
            View routineView = getLayoutInflater().inflate(R.layout.check_routine, routinesLayout, false);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = routineView.findViewById(R.id.textViewRoutineName);

            textView.setText(name);

            routineView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CheckTrainingsActivity.this, TrainingRoutinesActivity.class);
                    startActivity(intent);
                }
            });
            routinesLayout.addView(routineView);
        }
    }
}
