package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class TutoStep3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuto_step_3);

        ImageButton forwardArrow = findViewById(R.id.forwardArrow);
        forwardArrow.setOnClickListener(v -> {
            Intent intent = new Intent(TutoStep3Activity.this, TutoStep4Activity.class);
            startActivity(intent);
        });
    }
}
