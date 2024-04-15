package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class TrainingRoutinesActivity extends AppCompatActivity {
    private TextView hola;
    private ImageView home, training_routines, training, profile;
    private Toolbar toolbar, appbar;
    private Button btnAddRoutine;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_routine);

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        btnAddRoutine = findViewById(R.id.btnAddRoutine);

        btnAddRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenMain();
            }
        });
    }

    public void ScreenMain() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
