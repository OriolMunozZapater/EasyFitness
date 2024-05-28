package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class TutoStep2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuto_step_2);

        ImageButton forwardArrow = findViewById(R.id.forwardArrow);
        forwardArrow.setOnClickListener(v -> {
            Intent intent = new Intent(TutoStep2Activity.this, TutoStep3Activity.class);
            startActivity(intent);
        });
    }
}
