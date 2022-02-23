package com.kavinsaravanan.breakindetector;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImpactAudioClassifier {
    private static final String MODEL_FILE = "yamnet.tflite";
    private AudioClassifier audioClassifier;
    private AudioRecord audioRecord;
    private Activity activity;
    private final Handler handler = new Handler();
    private MediaRecorder mediaRecorder;
    private ImpactAudioNotifier impactAudioNotifier;
    long amplitudeThreshold = 1000;

    public ImpactAudioClassifier(Activity activity, ImpactAudioNotifier impactAudioNotifier) {
        this.activity = activity;
        this.mediaRecorder = new MediaRecorder();
        this.impactAudioNotifier = impactAudioNotifier;
    }

    public void startAudioClassifier() throws IOException {
        // run only once
        if (audioClassifier != null) {
            return;
        }

        // start media recorder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        String outputFile = this.activity.getApplicationContext().getFilesDir().getAbsolutePath() + "/test.3gp";
        mediaRecorder.setOutputFile(outputFile);

        mediaRecorder.prepare();
        mediaRecorder.start();

        // initialize audio classifier
        audioClassifier = AudioClassifier.createFromFile(activity, MODEL_FILE);
        TensorAudio audioTensor = audioClassifier.createInputTensorAudio();

        // initialize audio recorder
        audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                audioTensor.load(audioRecord);

                List<Classifications> classifications = audioClassifier.classify(audioTensor);
                List<Category> categories = classifications.get(0).getCategories();

                ArrayList<String> selected = new ArrayList<String>();
                for (Category category: categories) {
                    float score = category.getScore();
                    if (score > 0.3) {
                        selected.add(category.getLabel());
                    }
                }
                impactAudioNotifier.reportAudioEvent(selected.toString(), mediaRecorder.getMaxAmplitude());

                //add a delay of 1 second
                handler.postDelayed(this, amplitudeThreshold);
            }
        };
        runnable.run();
    }

    public void stopAudioClassifier() {
        mediaRecorder.reset();
        handler.removeCallbacksAndMessages(null);
        audioRecord.stop();
        audioRecord = null;
        audioClassifier = null;
    }
}
