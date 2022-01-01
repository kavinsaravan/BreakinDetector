package com.kavinsaravanan.breakindetector;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.sensorapp.impactlib.SinglePlot;

import java.io.IOException;

public class ImpactActivity extends AppCompatActivity implements ImpactMotionNotifier {
    private ImpactAudioClassifier impactAudioClassifier;
    private ImpactMotionClassifier impactMotionClassifier;
    private SinglePlot plotXAxis;
    private SinglePlot plotYAxis;
    private SinglePlot plotZAxis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impact);
        setTitle("Detection");
        LineChart chartXAxis = findViewById(R.id.lc_x_axis);
        plotXAxis = new SinglePlot(chartXAxis, "X Axis Motion", "X-Axis", Color.RED);
        LineChart chartYAxis = findViewById(R.id.lc_y_axis);
        plotYAxis = new SinglePlot(chartYAxis, "Y Axis Motion", "Y-Axis", Color.GREEN);
        LineChart chartZAxis = findViewById(R.id.lc_z_axis);
        plotZAxis = new SinglePlot(chartZAxis, "Z Axis Motion", "Z-Axis", Color.BLUE);
        impactAudioClassifier = new ImpactAudioClassifier(this, R.id.tv_sound_category);
        impactMotionClassifier = new ImpactMotionClassifier(this, this);
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
    protected void onStop() {
        super.onStop();
        impactAudioClassifier.stopAudioClassifier();
        impactMotionClassifier.stopImpactClassifier();
        plotXAxis.stop();
        plotYAxis.stop();
        plotZAxis.stop();
    }

    @Override
    public void reportMotionEvent(MotionData motionData) {
        Log.d("ImpactActivity", "Motion detected: X = " +  motionData.deltaX + ", Y = " + motionData.deltaY + ", Z = " + motionData.deltaZ);
        plotXAxis.addData(motionData.deltaX);
        plotYAxis.addData(motionData.deltaY);
        plotZAxis.addData(motionData.deltaZ);
    }
}