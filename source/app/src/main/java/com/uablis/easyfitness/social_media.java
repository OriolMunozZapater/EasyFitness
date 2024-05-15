package com.uablis.easyfitness;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class social_media extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);
    }

    private void updateUIWithUsers(String[] exerciseNames, Integer[] exerciseID) {
        LinearLayout routinesLayout = findViewById(R.id.usersContainer);
        int pos=0;

        for (String name : exerciseNames) {

            View userView = getLayoutInflater().inflate(R.layout.row_user, routinesLayout, false);
            TextView textView = userView.findViewById(R.id.user_name);
            Button followButton = userView.findViewById(R.id.follow_button);

            textView.setText(name);

            pos++;
        }
    }
}
