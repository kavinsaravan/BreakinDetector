package com.kavinsaravanan.breakindetector;

public interface ImpactAudioNotifier {
    void reportAudioEvent(String category, int maxAmplitude);
}
