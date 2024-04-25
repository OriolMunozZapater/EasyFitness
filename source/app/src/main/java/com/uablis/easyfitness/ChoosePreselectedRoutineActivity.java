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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class ChoosePreselectedRoutineActivity extends AppCompatActivity {
    private ImageView home, training_routines, training, profile, backArrow;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_preselected_routine);

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        backArrow = findViewById(R.id.back_arrow);
        String[] routineNames = {"superaniol", "aniolpeirna"}; String[] routineID = {"1", "2"};


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });

        updateUIWithRoutines(routineNames, routineID, "1234");
    }

    private void loadPredifinedRoutine() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://192.168.1.97:8080/api/rutinas/usuario/" + 0;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] routineIDs = new String[jsonArray.length()];
                            String[] routineNames = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                routineIDs[i] = jsonObject.getString("rutinaID");
                                routineNames[i] = jsonObject.getString("nombre");
                            }
                            updateUIWithRoutines(routineNames, routineIDs, userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }
    private void updateUIWithRoutines(String[] routineNames, String[] routineIDs, String userID) {
        LinearLayout routinesLayout = findViewById(R.id.preRoutineContainer);

        for (String name : routineNames) {
            View routineView = getLayoutInflater().inflate(R.layout.routine_item, routinesLayout, false);
            TextView textView = routineView.findViewById(R.id.textViewRoutineName);
            ImageButton menuButton = routineView.findViewById(R.id.menu_button_routine);

            textView.setText(name);
            //eto ns
            int finalI = 0;
            int finalI1 = finalI;
            routineView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyRoutine(routineIDs[finalI1], userID);
                }
            });
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuPopUpRoutine(v);
                }
            });
            routinesLayout.addView(routineView);

            finalI++;
        }
    }

    protected void copyRoutine(String copiedRoutineID, String userID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.97:8080/api/rutinas/" + copiedRoutineID;

        // Primera solicitud GET para obtener los detalles de la rutina
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String routineName = jsonObject.getString("nombre");
                                String routineDescription = jsonObject.getString("descripcion");
                                String publicRoutine = jsonObject.getString("publico");
                                insertNewRoutine(routineName, routineDescription, publicRoutine, userID);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void insertNewRoutine(String routineName, String routineDescription, String publicRoutine, String userID) {
                        // Construye el cuerpo de la solicitud en formato JSON
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("nombre", routineName);
                            jsonBody.put("descripcion", routineDescription);
                            jsonBody.put("user_ID", userID);
                            jsonBody.put("publico", publicRoutine);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // URL para la solicitud POST
                        String postUrl = "http://192.168.1.97:8080/api/rutinas/";

                        // Crea una solicitud POST
                        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonBody,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // La inserción fue exitosa
                                        showAlert("Éxito", "Rutina creada exitosamente y datos guardados en la API.");
                                        String idNewRoutine = getNewRoutineID(routineName, userID);
                                        String postUrl = "http://192.168.1.97:8080/api/rutinas/";
                                    }

                                    private String getNewRoutineID(String routineName, String userID) {

                                        return "";
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Imprime detalles del error
                                        error.printStackTrace();
                                        if (error.networkResponse != null) {
                                            String body;
                                            //Obtiene el cuerpo de la respuesta
                                            final String statusCode = String.valueOf(error.networkResponse.statusCode);
                                            try {
                                                body = new String(error.networkResponse.data, "UTF-8");
                                                showAlert("Error de la API", "Error al guardar los datos de la rutina en la API. Código de estado: " + statusCode + " Cuerpo: " + body);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            showAlert("Error de la API", "Error al guardar los datos de la rutina en la API. Error: " + error.toString());
                                        }
                                    }
                                }
                        );

                        // Añade la solicitud a la cola
                        queue.add(postRequest);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(getRequest);
    }

    private void showAlert(String title, String message) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChoosePreselectedRoutineActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Redirige al usuario a la pantalla de inicio de sesión
                        navigateToLogin();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ChoosePreselectedRoutineActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el back stack
        startActivity(intent);
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

}
