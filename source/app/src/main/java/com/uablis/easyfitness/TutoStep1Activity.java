package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageButton;

public class TutoStep1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuto_step_1);

        ImageButton forwardArrow = findViewById(R.id.forwardArrow);
        forwardArrow.setOnClickListener(v -> {
            Intent intent = new Intent(TutoStep1Activity.this, TutoStep2Activity.class);
            startActivity(intent);
        });
    }
}
