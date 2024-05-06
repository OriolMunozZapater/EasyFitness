package com.uablis.easyfitness;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

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

public class TrainingRoutinesActivity extends AppCompatActivity {
    private TextView hola;
    private ImageView home, training_routines, training, profile, training_session;
    private Toolbar toolbar, appbar;
    private ImageButton menu;
    private Button btnAddRoutine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_training_routine);
        loadUserRoutines();
        // String[] routineNames = {"superaniol", "aniolpeirna"};
        // updateUIWithRoutines(routineNames);

        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);

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
    }

    private void loadUserRoutines() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://172.17.176.1:8080/api/rutinas/usuario/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] routineNames = new String[jsonArray.length()];
                            String[] routineIDs = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                routineNames[i] = jsonObject.getString("nombre");
                                routineIDs[i] = jsonObject.getString("rutinaID");
                            }
                            updateUIWithRoutines(routineNames, routineIDs);
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

    private void updateUIWithRoutines(String[] routineNames, String[] routineIDs) {
        LinearLayout routinesLayout = findViewById(R.id.routinesContainer);
        for (int i = 0; i < routineNames.length; i++) {
            View routineView = getLayoutInflater().inflate(R.layout.routine_item, routinesLayout, false);
            TextView textView = routineView.findViewById(R.id.textViewRoutineName);
            ImageButton menuButton = routineView.findViewById(R.id.menu_button_routine);

            textView.setText(routineNames[i]);
            // Guardar el ID de la rutina como un tag en el ImageButton
            menuButton.setTag(routineIDs[i]);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuPopUpRoutine(v);
                }
            });
            routinesLayout.addView(routineView);
        }
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
        Intent intent = new Intent(TrainingRoutinesActivity.this, EditRoutineActivity.class);
        intent.putExtra("ROUTINE_ID", rutinaID);
        startActivity(intent);
    }

    public void goToNewRoutine() {
        Intent intent = new Intent(TrainingRoutinesActivity.this, NewRoutineActivity.class);
        startActivity(intent);
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