package com.kavinsaravanan.breakindetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.sensorapp.impactlib.HelperActivity;
import com.sensorapp.impactlib.PrefItem;
import com.sensorapp.impactlib.SinglePlot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImpactActivity extends HelperActivity implements ImpactMotionNotifier, ImpactAudioNotifier {
    private ImpactAudioClassifier impactAudioClassifier;
    private ImpactMotionClassifier impactMotionClassifier;
    private SinglePlot plotXAxis;
    private SinglePlot plotYAxis;
    private SinglePlot plotZAxis;
    private TextView categoryTextView;
    private TextView amplitudeTextView;
    private TextView motionDataTextView;
    private TextView alertTextView;
    private TextView alertTimeTextView;
    private AmplitudeQueue amplitudeQueue;
    private boolean startedMonitoring = false;
    private ArrayList<PrefItem> prefItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impact);
        setTitle("Detection");
        categoryTextView = findViewById(R.id.tv_sound_category);
        amplitudeTextView = findViewById(R.id.tv_amplitude);
        motionDataTextView = findViewById(R.id.tv_motion_data);
        alertTextView = findViewById(R.id.tv_alert);
        alertTimeTextView = findViewById(R.id.tv_alert_time);
        LineChart chartXAxis = findViewById(R.id.lc_x_axis);
        plotXAxis = new SinglePlot(chartXAxis, "X Axis Motion", "X-Axis", Color.RED);
        LineChart chartYAxis = findViewById(R.id.lc_y_axis);
        plotYAxis = new SinglePlot(chartYAxis, "Y Axis Motion", "Y-Axis", Color.GREEN);
        LineChart chartZAxis = findViewById(R.id.lc_z_axis);
        plotZAxis = new SinglePlot(chartZAxis, "Z Axis Motion", "Z-Axis", Color.BLUE);
        impactAudioClassifier = new ImpactAudioClassifier(this, this);
        impactMotionClassifier = new ImpactMotionClassifier(this, this);
        amplitudeQueue = new AmplitudeQueue(3, 1000);
        setPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            impactAudioClassifier.startAudioClassifier();
            impactMotionClassifier.startImpactClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        }
        plotXAxis.start();
        plotYAxis.start();
        plotZAxis.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        impactAudioClassifier.stopAudioClassifier();
        impactMotionClassifier.stopImpactClassifier();
        plotXAxis.stop();
        plotYAxis.stop();
        plotZAxis.stop();
    }

    @Override
    public void reportMotionEvent(MotionData motionData) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        Log.d("ImpactActivity", "Motion detected: X = " +  motionData.deltaX + ", Y = " + motionData.deltaY + ", Z = " + motionData.deltaZ);
        motionDataTextView.setText(motionData.toString());
        plotXAxis.addData(motionData.deltaX);
        plotYAxis.addData(motionData.deltaY);
        plotZAxis.addData(motionData.deltaZ);

        reportImpactAlert();
    }

    @Override
    public void reportAudioEvent(String category, int maxAmplitude) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        amplitudeQueue.add(maxAmplitude);
        categoryTextView.setText(category);
        amplitudeTextView.setText(maxAmplitude + " dB");
    }

    private void reportImpactAlert() {
        if (!startedMonitoring) {
            startedMonitoring = true;
            return;
        }
        if (amplitudeQueue.allAboveThreshold()) {
            alertTextView.setVisibility(View.VISIBLE);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            alertTimeTextView.setText(currentTime);
            alertTimeTextView.setVisibility(View.VISIBLE);
        }
    }


    private void setPreferences() {
        prefItems.add(new PrefItem("amp-threshold", "Amplitude Threshold", (float) impactAudioClassifier.amplitudeThreshold));
        prefItems.add(new PrefItem("noise-threshold", "Noise Threshold", impactMotionClassifier.noiseThreshold));
        prefItems.add(new PrefItem("reporting-threshold", "Reporting Threshold", impactMotionClassifier.reportingThreshold));
        getPrefItems().addAll(prefItems);

        String amplitudeThreshold = PreferenceManager.getDefaultSharedPreferences(this).getString("amp-threshold",
                Long.toString(impactAudioClassifier.amplitudeThreshold));
        impactAudioClassifier.amplitudeThreshold = Long.parseLong(amplitudeThreshold);

        String noiseThresold = PreferenceManager.getDefaultSharedPreferences(this).getString("noise-threshold",
                Float.toString(impactMotionClassifier.noiseThreshold));
        impactMotionClassifier.noiseThreshold = Float.parseFloat(noiseThresold);

        String reportingThreshold = PreferenceManager.getDefaultSharedPreferences(this).getString("reporting-threshold",
                Float.toString(impactMotionClassifier.reportingThreshold));
        impactMotionClassifier.reportingThreshold = Float.parseFloat(reportingThreshold);
    }
}