package com.kavinsaravanan.breakindetector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;

public class ImpactActivity extends AppCompatActivity {
    private ImpactAudioClassifier impactAudioClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impact);
        setTitle("Detection");
        impactAudioClassifier = new ImpactAudioClassifier(this, R.id.tv_sound_category);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            impactAudioClassifier.startAudioClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        impactAudioClassifier.stopAudioClassifier();
    }
}

