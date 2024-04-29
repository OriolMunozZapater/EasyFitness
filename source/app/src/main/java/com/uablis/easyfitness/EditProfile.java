package com.uablis.easyfitness;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Prefiero no decirlo", "Croissant"};

    private EditText nameUser, cognomUser, pesActualUser, alturaUser, descripcioUser;
    private TextView DateOfBirthUser;
    private ImageView imagePerfilUser;
    private Button btnSexSelect, btnSaveUserChanges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        nameUser = findViewById(R.id.nameUser);
        cognomUser = findViewById(R.id.cognomUser);
        pesActualUser = findViewById(R.id.pesActualUser);
        alturaUser = findViewById(R.id.alturaUser);
        descripcioUser = findViewById(R.id.descripcioUser);
        DateOfBirthUser = findViewById(R.id.DateOfBirthUser);
        imagePerfilUser = findViewById(R.id.imagePerfilUser);
        btnSexSelect = findViewById(R.id.btnSexSelect);
        btnSaveUserChanges = findViewById(R.id.btnSaveUserChanges);

        // Set click listener for image selection
        imagePerfilUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        // Set click listener for gender selection
        btnSexSelect.setOnClickListener(this::showSexoOptions);

        DateOfBirthUser.setOnClickListener(v -> showDatePickerDialog());

        // Set click listener for save button
        btnSaveUserChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement save functionality here
                saveUserChanges();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditProfile.this,
                (view, year, month, dayOfMonth) -> DateOfBirthUser.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void showSexoOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu sexo");
        builder.setItems(genderOptions, (dialog, which) -> btnSexSelect.setText(genderOptions[which]));
        builder.show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Aquí puedes setear el bitmap en tu ImageView
                imagePerfilUser.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserChanges() {
        //TODO: implementar logica de guardar cambios
    }
}

