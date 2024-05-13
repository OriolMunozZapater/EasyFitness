package com.uablis.easyfitness;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddComent extends AppCompatActivity {

    private ImageView imagePublic;
    private ImageButton dislike, like;
    private EditText comment;
    private ImageView backArrow;
    private Button btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coment);

        bindViews();
        setupListeners();
    }

    private void bindViews() {

        imagePublic = findViewById(R.id.imagePublic);
        dislike = findViewById(R.id.dislike);
        like = findViewById(R.id.like);
        comment = findViewById(R.id.comment);
        backArrow = findViewById(R.id.back_arrow);
        btnPublish = findViewById(R.id.btnPublish);
    }

    private void setupListeners() {

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });
    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to publish this?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //afegir rutina a la bd
                finish();
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

    public void backToScreen() {
        finish();
    }
}
