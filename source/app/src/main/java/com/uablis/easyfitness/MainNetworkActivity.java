package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainNetworkActivity extends AppCompatActivity {
    ImageButton botonSeguidos,botonSeguidores, botonConsultaRutinas, botonUsersSeguir, botonReforzarPartes, botonPublicarVideos, botonComentarioApp, botonValorarEjercicio, botonMostrarRanking;
    private ImageView home, trainingRoutinesButton, profile, training_session;
    private static final int PICK_VIDEO_REQUEST = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network);

        botonSeguidores = findViewById(R.id.botonSeguidores);
        botonSeguidos = findViewById(R.id.botonSeguidos);
        botonConsultaRutinas = findViewById(R.id.botonConsultaRutinas);
        botonUsersSeguir = findViewById(R.id.botonUsersSeguir);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);
        botonReforzarPartes = findViewById(R.id.botonReforzarPartes);
        botonPublicarVideos = findViewById(R.id.botonPublicarVideos);
        botonComentarioApp = findViewById(R.id.botonComentarioApp);
        botonValorarEjercicio = findViewById(R.id.ValorarEjercicio);
        botonMostrarRanking = findViewById(R.id.botonMostrarRanking);

        botonMostrarRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de mostrar ranking
                Intent intent = new Intent(MainNetworkActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        botonComentarioApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, AppReviewActivity.class);
                startActivity(intent);
            }
        });

        botonValorarEjercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, ViewExercisesActivity.class);
                startActivity(intent);
            }
        });

        botonPublicarVideos.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
        });

        botonReforzarPartes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainNetworkActivity.this, RecommendedActivitiesActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainNetworkActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainNetworkActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(MainNetworkActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        botonSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, FollowersUsersActivity.class);
                startActivity(intent);
            }
        });

        botonSeguidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, FollowedUsersActivity.class);
                startActivity(intent);
            }
        });

        botonConsultaRutinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de consultar rutinas
                Intent intent = new Intent(MainNetworkActivity.this, ViewRoutinesActivity.class);
                startActivity(intent);
            }
        });

        botonUsersSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, DiscoverUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri videoUri = data.getData();
            uploadVideoToFirebase(videoUri); // Cargar el video seleccionado a Firebase
        }
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        if (videoUri != null) {
            String userId = UsuarioActual.getInstance().getUserId();
            String timestamp = String.valueOf(System.currentTimeMillis());
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("videos/" + userId + "_" + timestamp);

            storageRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String videoUrl = downloadUri.toString();
                        // Aquí puedes guardar la URL del video en tu base de datos de Firebase o realizar alguna acción adicional
                        Toast.makeText(MainNetworkActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(MainNetworkActivity.this, "Error uploading video: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}