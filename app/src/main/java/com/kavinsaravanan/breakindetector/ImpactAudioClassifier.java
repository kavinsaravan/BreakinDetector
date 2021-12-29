package com.kavinsaravanan.breakindetector;

import android.app.Activity;
import android.media.AudioRecord;
import android.os.Handler;
import android.widget.TextView;

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
    private TextView textView;
    private final Handler handler = new Handler();

    public ImpactAudioClassifier(Activity activity, int textViewId) {
        this.activity = activity;
        this.textView = activity.findViewById(textViewId);
    }

    public void startAudioClassifier() throws IOException {
        // run only once
        if (audioClassifier != null) {
            return;
        }
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
                textView.setText(selected.toString());

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
