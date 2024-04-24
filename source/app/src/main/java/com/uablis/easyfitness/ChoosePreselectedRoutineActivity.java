package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import org.w3c.dom.Text;

public class ChoosePreselectedRoutineActivity extends AppCompatActivity {
    private TextView hola;
    private ImageView home, training_routines, training, profile, backArrow;
    private Toolbar toolbar, appbar;
    private ImageButton menu;
    private RelativeLayout routine;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_preselected_routine);

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        menu = findViewById(R.id.menu_button);
        backArrow = findViewById(R.id.back_arrow);
        routine = findViewById(R.id.preSelectedRoutine);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuPopUpRoutine(v);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });

        routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoutine();
            }
        });
    }

    public void addRoutine() {
        //logica per afegir rutina com a seleccionada i mostrarla a view_trainnig_routine
        finish();
    }

    public void backScreen() {
        finish();
    }

    public void menuPopUpRoutine(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_type3, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    goToEditRoutine();
                    return true;
                } else if (item.getItemId() == R.id.delete_option) {
                    showAlert();
                } else {
                    finish();
                }
                return false;
            }
        });
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this exercise?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //borrar ex de la bd
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

    public void goToEditRoutine() {
        Intent intent = new Intent(ChoosePreselectedRoutineActivity.this, EditRoutineActivity.class);
        startActivity(intent);
    }

    public void ScreenMain() {
        Intent intent = new Intent(ChoosePreselectedRoutineActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
