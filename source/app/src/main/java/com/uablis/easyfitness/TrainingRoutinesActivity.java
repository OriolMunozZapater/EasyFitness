package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import org.w3c.dom.Text;

public class TrainingRoutinesActivity extends AppCompatActivity {
    private TextView hola;
    private ImageView home, training_routines, training, profile;
    private Toolbar toolbar, appbar;
    private ImageButton menu;
    private Button btnAddRoutine;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_routine);

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        btnAddRoutine = findViewById(R.id.btnAddRoutine);
        menu = findViewById(R.id.menu_button);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopUpRoutine(v);
            }
        });

        btnAddRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopUpAddExercise(v);
            }
        });
    }

    public void menuPopUpRoutine(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_type1, popupMenu.getMenu());
        popupMenu.show();
    }

    public void menuPopUpAddExercise(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_type2, popupMenu.getMenu());
        popupMenu.show();
    }

    public void ScreenMain() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
