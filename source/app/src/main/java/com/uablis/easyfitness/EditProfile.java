package com.uablis.easyfitness;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import com.uablis.easyfitness.R;
import com.uablis.easyfitness.UsuarioActual;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Prefiero no decirlo", "Croissant"};
    private CharSequence[] SocialOptions = {"Instagram", "Facebook", "X"};
    private EditText nameUser, cognomUser, pesActualUser, alturaUser, descripcioUser;
    private TextView DateOfBirthUser;
    private ImageView imagePerfilUser, backArrow;
    private Button btnSexSelect, btnSaveUserChanges, btnSocialUser, btnSelectGimnas;

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
        btnSocialUser = findViewById(R.id.btnSocialUser);
        btnSelectGimnas = findViewById(R.id.btnSelectGimnas);
        btnSaveUserChanges = findViewById(R.id.btnSaveUserChanges);
        backArrow = findViewById(R.id.back_arrow);

        // Set click listener for image selection
        imagePerfilUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        // Set click listener for gender selection
        btnSexSelect.setOnClickListener(this::showSexoOptions);
        btnSexSelect.setOnClickListener(this::showSocialOptions);

        DateOfBirthUser.setOnClickListener(v -> showDatePickerDialog());

        // Set click listener for save button
        btnSaveUserChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement save functionality here
                updateUserData(pesActualUser.toString(), alturaUser.toString(), imagePerfilUser.toString(),
                        nameUser.toString(), btnSexSelect.getText().toString(), cognomUser.toString(), descripcioUser.toString(), DateOfBirthUser.toString());
            }
        });
        getUserData();

    }

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you don't want to save the changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void getUserData(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();

        String url = "http://172.17.176.1:8080/api/usuario/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String userName = jsonObject.getString("nombre");
                            String userSurname = jsonObject.getString("apellido");
                            String userEmail = jsonObject.getString("correo");
                            String userGender = jsonObject.getString("sexo");
                            String userActualWeight = jsonObject.getString("peso_actual");
                            String userHeight = jsonObject.getString("altura");
                            String userFoto = jsonObject.getString("foto");
                            String userDescription = jsonObject.getString("descripcion");
                            String userSocialMedia = jsonObject.getString("redes_sociales");
                            String userObjective = jsonObject.getString("objetivo");
                            String userFirstLogin = jsonObject.getString("firslLogin");



                            updateUIWithUserData(userName, userSurname, userEmail, userGender, userActualWeight,
                                    userHeight, userFoto, userDescription, userSocialMedia, userObjective, userFirstLogin);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditProfile.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(EditProfile.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void updateUIWithUserData(String userName, String userSurname, String userEmail, String userGender, String userActualWeight,
                                      String userHeight, String userFoto, String userDescription, String userSocialMedia, String userObjective, String userFirstLogin) {
        // Actualiza las vistas con la información del usuario
        nameUser.setText(userName);
        cognomUser.setText(userSurname);
        pesActualUser.setText(userActualWeight);
        alturaUser.setText(userHeight);
        descripcioUser.setText(userDescription);
        DateOfBirthUser.setText(userObjective); // No está claro qué vista debería mostrar el objetivo del usuario
        // Puedes manejar la foto del usuario si lo deseas, por ejemplo, cargándola en el ImageView
        // imagePerfilUser.setImageURI(Uri.parse(userFoto));

        // Resto del código...
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
    /*TODO: linkar redes sociales del usuario*/
    public void showSocialOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu RED SOCIAL");
        builder.setItems(SocialOptions, (dialog, which) -> btnSexSelect.setText(SocialOptions[which]));
        builder.show();
    }
    /*TODO: linkar localizacion maps del gimnasio*/

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

    private void updateUserData(String pesoActual, String altura, String foto, String nombre, String sexo, String cognom, String descripcio, String neixement) {
        // URL de la API para actualizar el usuario
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://172.17.176.1:8080/api/usuarios/" + userId;

        // Construye el cuerpo de la solicitud en formato JSON
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("nombre", nombre);
            requestBody.put("peso_actual", pesoActual);
            requestBody.put("altura", altura);
            //requestBody.put("peso_objetivo", pesoObjetivo); Esto se tiene que modificar en la tabla objetivo
            requestBody.put("foto", foto);
            requestBody.put("sexo", sexo);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Sale de la función si hay un error al construir el JSON
        }

        // Crea una solicitud PUT
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Aquí puedes manejar la respuesta exitosa, si es necesario
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // Aquí puedes manejar el error de la solicitud, si es necesario
                    }
                });

        // Añade la solicitud a la cola de solicitudes

        queue.add(jsonObjectRequest);
    }
}