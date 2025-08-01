package com.example.hesalaty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        VideoView videoView = findViewById(R.id.videoSplash);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_intro);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(mp -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        });

        videoView.setOnPreparedListener(mp -> videoView.start());
    }
}
