package com.uablis.easyfitness;

import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartRoutineActivity extends AppCompatActivity {
    private Chronometer trainingDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_routine);

        trainingDuration = findViewById(R.id.chronometer);

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
}
