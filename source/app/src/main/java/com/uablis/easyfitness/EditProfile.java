package com.uablis.easyfitness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Prefiero no decirlo"};
    private EditText nameUser, cognomUser, pesActualUser, alturaUser, descripcioUser, gimnasioUser;
    private ImageView imagePerfilUser, back_arrow;
    private Button btnSexSelect, btnSaveUserChanges, btnSocialUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initializeViews();
        getUserData();
    }

    private void initializeViews() {
        nameUser = findViewById(R.id.nameUser);
        cognomUser = findViewById(R.id.cognomUser);
        pesActualUser = findViewById(R.id.pesActualUser);
        alturaUser = findViewById(R.id.alturaUser);
        descripcioUser = findViewById(R.id.descripcioUser);
        gimnasioUser = findViewById(R.id.descripcioGimnas);
        imagePerfilUser = findViewById(R.id.imagePerfilUser);
        btnSexSelect = findViewById(R.id.btnSexSelect);
        btnSocialUser = findViewById(R.id.btnSocialUser);
        btnSaveUserChanges = findViewById(R.id.btnSaveUserChanges);

        imagePerfilUser.setOnClickListener(v -> selectImageFromGallery());
        btnSexSelect.setOnClickListener(this::showGenderOptions);
        btnSocialUser.setOnClickListener(this::showSocialOptions);
        btnSaveUserChanges.setOnClickListener(v -> uploadImageToFirebaseStorage(null));
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(v -> volver());
    }

    private void getUserData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "usuarios/" + userId;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        updateUIWithUserData(jsonObject);
                    } catch (JSONException e) {
                        Toast.makeText(EditProfile.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(EditProfile.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        queue.add(stringRequest);
    }

    private void updateUIWithUserData(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.optString("nombre").isEmpty()) {
            nameUser.setText(jsonObject.optString("nombre", ""));
        }
        if (!jsonObject.optString("apellido").isEmpty()) {
            cognomUser.setText(jsonObject.optString("apellido", ""));
        }
        if (!jsonObject.optString("peso_actual").isEmpty()) {
            pesActualUser.setText(jsonObject.optString("peso_actual", ""));
        }
        if (!jsonObject.optString("altura").isEmpty()) {
            alturaUser.setText(jsonObject.optString("altura", ""));
        }
        if (!jsonObject.optString("descripcion").isEmpty()){
            descripcioUser.setText(jsonObject.optString("descripcion", "Introduce una descripción"));
        }
        if (!jsonObject.optString("gimnasio").isEmpty()) {
            gimnasioUser.setText(jsonObject.optString("gimnasio", "Introduce el nombre"));
        }
        if (!jsonObject.optString("sexo").isEmpty()) {
            btnSexSelect.setText(jsonObject.optString("sexo", ""));
        }
        if (!jsonObject.optString("redes_sociales").isEmpty()) {
            btnSocialUser.setText(jsonObject.optString("redes_sociales", "Select"));
        }

        if (nameUser.getText().toString().equals("null")) {
            nameUser.setText("");
        }
        if (cognomUser.getText().toString().equals("null")) {
            cognomUser.setText("");
        }
        if (pesActualUser.getText().toString().equals("null")) {
            pesActualUser.setText("");
        }
        if (alturaUser.getText().toString().equals("null")) {
            alturaUser.setText("");
        }
        if (descripcioUser.getText().toString().equals("null")) {
            descripcioUser.setText("");
        }
        if (gimnasioUser.getText().toString().equals("null")) {
            gimnasioUser.setText("");
        }
        if (btnSexSelect.getText().toString().equals("null")) {
            btnSexSelect.setText("");
        }
        if (btnSocialUser.getText().toString().equals("null")) {
            btnSocialUser.setText("");
        }

        loadImageFromFirebase();
    }

    private void loadImageFromFirebase() {
        String userId = UsuarioActual.getInstance().getUserId();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/userID" + userId);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.default_image)
                    .into(imagePerfilUser);
        }).addOnFailureListener(e -> {
            imagePerfilUser.setImageResource(R.drawable.default_image);
        });
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
            if (which == 0) btnSocialUser.setText("Instagram");
            else if (which == 1) btnSocialUser.setText("Facebook");
            else btnSocialUser.setText("Twitter");
        });
        builder.show();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imagePerfilUser.setImageURI(uri); // Set image temporarily
            btnSaveUserChanges.setOnClickListener(v -> uploadImageToFirebaseStorage(uri));
        }
    }

    private void uploadImageToFirebaseStorage(Uri fileUri) {
        if (fileUri != null) {
            String userId = UsuarioActual.getInstance().getUserId();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/userID" + userId);

            storageReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        imagePerfilUser.setImageURI(Uri.parse(imageUrl));
                        saveProfileData(imageUrl);  // Llamar a la función para guardar los datos
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfile.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error uploading image", e);
                    });
        } else {
            saveProfileData(null);
        }
    }

    private void saveProfileData(String imageUrl) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "usuarios/" + userId;
        String url = urlBase.buildUrl(path);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nombre", nameUser.getText().toString());
            requestBody.put("apellido", cognomUser.getText().toString());
            requestBody.put("peso_actual", pesActualUser.getText().toString());
            requestBody.put("altura", alturaUser.getText().toString());
            requestBody.put("descripcion", descripcioUser.getText().toString());
            requestBody.put("sexo", btnSexSelect.getText().toString());
            requestBody.put("gimnasio", gimnasioUser.getText().toString());
            requestBody.put("redes_sociales", btnSocialUser.getText().toString());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                    response -> {
                        Toast.makeText(EditProfile.this, "Perfil actualizado correctamente.", Toast.LENGTH_SHORT).show();
                        volver();
                    },
                    error -> Toast.makeText(EditProfile.this, "Error updating profile: " + error.toString(), Toast.LENGTH_SHORT).show());

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void volver(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
