package com.kavinsaravanan.breakindetector;

import com.sensorapp.impactlib.HelperActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends HelperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMicrophonePermission();
    }

    @Override
    public void onMicrophonePermissionGranted() {
        super.onMicrophonePermissionGranted();
        //Toast.makeText(this, "Permission granted!!!", Toast.LENGTH_LONG).show();
        launchActivityOnButtonClick(this, R.id.btn_start, ImpactActivity.class);
    }

    @Override
    public void onMicrophonePermissionDenied() {
        super.onMicrophonePermissionDenied();
        Toast.makeText(this, "Sorry, permission denied!!!", Toast.LENGTH_LONG).show();
    }
}