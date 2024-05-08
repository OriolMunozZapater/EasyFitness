package com.uablis.easyfitness;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Prefiero no decirlo"};
    private EditText nameUser, cognomUser, pesActualUser, alturaUser, descripcioUser;
    private ImageView imagePerfilUser;
    private Button btnSexSelect, btnSaveUserChanges, btnSocialUser;
    private Bitmap selectedBitmap = null;  // To hold the image from gallery

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
        imagePerfilUser = findViewById(R.id.imagePerfilUser);
        btnSexSelect = findViewById(R.id.btnSexSelect);
        btnSocialUser = findViewById(R.id.btnSocialUser);
        btnSaveUserChanges = findViewById(R.id.btnSaveUserChanges);

        // Set click listeners
        imagePerfilUser.setOnClickListener(v -> selectImageFromGallery());
        btnSexSelect.setOnClickListener(this::showGenderOptions);
        btnSocialUser.setOnClickListener(this::showSocialOptions);
        btnSaveUserChanges.setOnClickListener(v -> saveUserData());

        getUserData();
    }

    private void getUserData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://172.17.176.1:8080/api/usuarios/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        nameUser.setText(jsonObject.getString("nombre"));
                        cognomUser.setText(jsonObject.getString("apellido"));
                        pesActualUser.setText(jsonObject.getString("peso_actual"));
                        alturaUser.setText(jsonObject.getString("altura"));
                        descripcioUser.setText(jsonObject.getString("descripcion"));
                        btnSexSelect.setText(jsonObject.getString("sexo"));
                        String encodedImage = jsonObject.optString("foto");
                        if (!encodedImage.isEmpty()) {
                            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imagePerfilUser.setImageBitmap(decodedByte);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(EditProfile.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(EditProfile.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void showGenderOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu sexo");
        builder.setItems(genderOptions, (dialog, which) -> btnSexSelect.setText(genderOptions[which]));
        builder.show();
    }

    private void showSocialOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu RED SOCIAL");
        builder.setItems(new CharSequence[]{"Instagram", "Facebook", "Twitter"}, (dialog, which) -> {
            // Handle selection
        });
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
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imagePerfilUser.setImageBitmap(selectedBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://172.17.176.1:8080/api/usuarios/" + userId;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nombre", nameUser.getText().toString());
            requestBody.put("apellido", cognomUser.getText().toString());
            requestBody.put("peso_actual", pesActualUser.getText().toString());
            requestBody.put("altura", alturaUser.getText().toString());
            requestBody.put("descripcion", descripcioUser.getText().toString());
            requestBody.put("sexo", btnSexSelect.getText().toString());
            requestBody.put("firstLogin", 0);
            if (selectedBitmap != null) {
                requestBody.put("foto", convertBitmapToString(selectedBitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                response -> Toast.makeText(EditProfile.this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(EditProfile.this, "Error updating profile: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
        goBack();
    }

    public void goBack() {
        Intent intent = new Intent(EditProfile.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}