package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class TrainingRoutinesActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private TextView hola;
    private ImageView home, training_routines, training, profile, training_session;
    private Toolbar toolbar, appbar;
    private ImageButton menu;
    private Button btnAddRoutine;
    private static final int ADD_EXERCISE_REQUEST = 1; // Constante de código de solicitud

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_routine);
        loadUserRoutines();

        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        //TODO: QUITAR COSA TRAINING_ROUTINES PROVISIONAL
        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingRoutinesActivity.this, RecommendedActivitiesActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingRoutinesActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingRoutinesActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(TrainingRoutinesActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadUserRoutines() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "rutinas/usuario/" + userId;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] routineNames = new String[jsonArray.length()];
                            String[] routineIDs = new String[jsonArray.length()];
                            boolean[] isPublic = new boolean[jsonArray.length()]; // Array to hold the public state of each routine

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                routineNames[i] = jsonObject.getString("nombre");
                                routineIDs[i] = jsonObject.getString("rutinaID");
                                isPublic[i] = jsonObject.getBoolean("publico"); // Assuming 'publico' is the JSON field
                            }
                            updateUIWithRoutines(routineNames, routineIDs, isPublic);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TrainingRoutinesActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(TrainingRoutinesActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void updateUIWithRoutines(String[] routineNames, String[] routineIDs, boolean[] isPublic) {
        LinearLayout routinesLayout = findViewById(R.id.routinesContainer);
        routinesLayout.removeAllViews();
        for (int i = 0; i < routineNames.length; i++) {
            View routineView = getLayoutInflater().inflate(R.layout.routine_item2, routinesLayout, false);
            TextView textView = routineView.findViewById(R.id.textViewRoutineName);

            // Inside updateUIWithRoutines method
            View statusIndicator = routineView.findViewById(R.id.status_indicator);
            textView.setText(routineNames[i]);
            statusIndicator.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), isPublic[i] ? R.color.sharedGreen : R.color.notSharedRed));

            ImageButton menuButton = routineView.findViewById(R.id.menu_button_routine);
            ImageButton shareButton = routineView.findViewById(R.id.share_button_routine);
            ImageButton unshareButton = routineView.findViewById(R.id.unshare_button_routine);

            menuButton.setTag(routineIDs[i]);
            shareButton.setTag(routineIDs[i]);
            unshareButton.setTag(routineIDs[i]);

            shareButton.setVisibility(isPublic[i] ? View.GONE : View.VISIBLE);
            unshareButton.setVisibility(isPublic[i] ? View.VISIBLE : View.GONE);

            shareButton.setOnClickListener(v -> shareRoutine(v.getTag().toString()));
            unshareButton.setOnClickListener(v -> unshareRoutine(v.getTag().toString()));
            menuButton.setOnClickListener(v -> menuPopUpRoutine(v));

            routinesLayout.addView(routineView);
        }
    }


    private void shareRoutine(String routineID) {
        String path = "rutinas/compartir/" + routineID;
        String url = urlBase.buildUrl(path);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    // La rutina ahora es pública y puede ser compartida
                    Toast.makeText(getApplicationContext(), "Routine shared successfully!", Toast.LENGTH_SHORT).show();
                    loadUserRoutines();
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Failed to share the routine", Toast.LENGTH_SHORT).show();
                    Log.e("Volley", error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(putRequest);
    }

    private void unshareRoutine(String routineID) {
        String path = "rutinas/noCompartir/" + routineID;
        String url = urlBase.buildUrl(path);
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Routine is now private!", Toast.LENGTH_SHORT).show();
                    loadUserRoutines();
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Failed to change the routine status", Toast.LENGTH_SHORT).show();
                    Log.e("Volley", error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(putRequest);
    }
    public void menuPopUpRoutine(View view) {
        final String routineID = view.getTag().toString();
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_type1, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.edit) {
                    goToEditRoutine(routineID);
                    return true;
                } else if (item.getItemId() == R.id.delete_option) {
                    showAlert(routineID);
                } else if (item.getItemId() == R.id.start_option){
                    goToStartRoutine(routineID);
                }
                return false;
            }
        });
    }

    public void goToStartRoutine(String rutinaID) {
        Intent intent = new Intent(TrainingRoutinesActivity.this, StartingRoutineActivity.class);
        intent.putExtra("ROUTINE_ID", rutinaID);
        startActivity(intent);
    }


    public void menuPopUpAddRoutine(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_type2, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.from0) {
                    goToNewRoutine();
                    return true;
                } else if (item.getItemId() == R.id.precreated) {
                    goToPreCreatedRoutine();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void showAlert(String rutinaID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this routine?");
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

    public void goToEditRoutine(String rutinaID) {
        Integer rutinaId=Integer.parseInt(rutinaID);
        Intent intent = new Intent(TrainingRoutinesActivity.this, EditRoutineActivity.class);
        intent.putExtra("ROUTINE_ID", rutinaId);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {

                // Aquí puedes hacer lo que necesitas después de volver de ChooseExerciseActivity
                LinearLayout routinesLayout = findViewById(R.id.routinesContainer);
                routinesLayout.removeAllViews();
                loadUserRoutines();
            }
        }
    }

    public void goToNewRoutine() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, NewRoutineActivity.class);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }

    public void goToPreCreatedRoutine() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, ChoosePreselectedRoutineActivity.class);
        startActivity(intent);
    }

    public void ScreenMain() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}