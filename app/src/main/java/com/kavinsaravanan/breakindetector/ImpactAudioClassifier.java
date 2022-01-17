package com.kavinsaravanan.breakindetector;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.widget.TextView;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImpactAudioClassifier {
    private static final String MODEL_FILE = "yamnet.tflite";
    private AudioClassifier audioClassifier;
    private AudioRecord audioRecord;
    private Activity activity;
    private TextView categoryTextView;
    private TextView amplitudeTextView;
    private final Handler handler = new Handler();
    private MediaRecorder mediaRecorder;

    public ImpactAudioClassifier(Activity activity, int categoryTextViewId, int amplitudeTextViewId) {
        this.activity = activity;
        this.categoryTextView = activity.findViewById(categoryTextViewId);
        this.amplitudeTextView = activity.findViewById(amplitudeTextViewId);
        this.mediaRecorder = new MediaRecorder();
        this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        String outputFile = new String(activity.getFilesDir().getAbsolutePath() + "/test.3gp");
        this.mediaRecorder.setOutputFile(outputFile);
        this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }

    public void startAudioClassifier() throws IOException {
        // run only once
        if (audioClassifier != null) {
            return;
        }
        // start media recorder
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
                categoryTextView.setText(selected.toString());
                amplitudeTextView.setText(mediaRecorder.getMaxAmplitude() + " db");

                //add a delay of 1 second
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();
    }

    public void stopAudioClassifier() {
        handler.removeCallbacksAndMessages(null);
        audioRecord.stop();
        audioRecord = null;
        audioClassifier = null;
    }
}
