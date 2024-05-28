package com.uablis.easyfitness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewVideosActivity extends AppCompatActivity {
    private LinearLayout videosContainer;
    private ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_videos);

        videosContainer = findViewById(R.id.videosContainer);
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(v -> finish());

        int userId = getIntent().getIntExtra("userID", -1);
        if (userId != -1) {
            fetchVideos(userId);
        } else {
            Toast.makeText(this, "Error: No se pudo recuperar el ID de usuario.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchVideos(int userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("videos");
        storageRef.listAll().addOnSuccessListener(listResult -> {
            boolean found = false;  // Agregamos un flag para verificar si se encontraron videos
            for (StorageReference item : listResult.getItems()) {
                if (item.getName().startsWith(userId + "_")) {
                    found = true;  // Marcamos que encontramos al menos un video
                    displayVideo(item);
                }
            }
            if (!found) {  // Si no se encontraron videos, mostramos un mensaje
                Toast.makeText(this, "No se encontraron videos para este usuario.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener videos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void displayVideo(StorageReference videoRef) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View videoView = inflater.inflate(R.layout.row_video, null, false);  // Asegúrate de que no pase 'videosContainer' como el root aquí.

        EditText commentInput = videoView.findViewById(R.id.commentInput);
        Button downloadButton = videoView.findViewById(R.id.downloadButton);
        Button sendCommentButton = videoView.findViewById(R.id.sendCommentButton);

        downloadButton.setOnClickListener(v -> redirectToVideo(videoRef));
        sendCommentButton.setOnClickListener(v -> {
            String comment = commentInput.getText().toString();
            Toast.makeText(ViewVideosActivity.this, "Comentario enviado", Toast.LENGTH_SHORT).show();
            commentInput.setText(""); // Limpiar el campo después del envío
        });

        videosContainer.addView(videoView);  // Asegúrate de que esto se hace para cada video
    }


    private void redirectToVideo(StorageReference videoRef) {
        videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }).addOnFailureListener(e -> Toast.makeText(ViewVideosActivity.this, "Error al abrir el video: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
