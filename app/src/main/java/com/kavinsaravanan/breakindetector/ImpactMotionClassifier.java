package com.kavinsaravanan.breakindetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ImpactMotionClassifier implements SensorEventListener {
    private Sensor accelerometerSensor;
    private SensorManager sensorManager;
    private float lastX = 0f;
    private float lastY = 0f;
    private float lastZ = 0f;
    private float deltaX = 0f;
    private float deltaY = 0f;
    private float deltaZ = 0f;
    private ImpactMotionNotifier impactMotionNotifier;
    float noiseThreshold = 0.05f;
    float reportingThreshold = 0.0f;

    public ImpactMotionClassifier(Context context, ImpactMotionNotifier impactMotionNotifier) {
        this.impactMotionNotifier = impactMotionNotifier;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        deltaX = lastX - sensorEvent.values[0];
        deltaY = lastY - sensorEvent.values[1];
        deltaZ = lastZ - sensorEvent.values[2];

        // store current values for next run
        lastX = sensorEvent.values[0];
        lastY = sensorEvent.values[1];
        lastZ = sensorEvent.values[2];

        //if the change is below noise threshold we should ignore
        if (Math.abs(deltaX) < noiseThreshold) {
            deltaX = 0;
        }
        if (Math.abs(deltaY) < noiseThreshold) {
            deltaY = 0;
        }
        if (Math.abs(deltaZ) < noiseThreshold) {
            deltaZ = 0;
        }
        if (deltaX > reportingThreshold || deltaY > reportingThreshold || deltaZ > reportingThreshold) {
            MotionData motionData = new MotionData();
            motionData.deltaX = deltaX;
            motionData.deltaY = deltaY;
            motionData.deltaZ = deltaZ;
            impactMotionNotifier.reportMotionEvent(motionData);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void startImpactClassifier() {
        sensorManager.registerListener(this, accelerometerSensor, 30000000);
    }

    public void stopImpactClassifier() {
        sensorManager.unregisterListener(this);
    }
}
