package com.kavinsaravanan.breakindetector;

import java.util.LinkedList;

public class AmplitudeQueue extends LinkedList<Integer> {
    private final int maxSize;
    private final int threshold;

    public AmplitudeQueue(int maxSize, int threshold) {
        super();
        this.maxSize = maxSize;
        this.threshold = threshold;
    }

    public boolean add(Integer value) {
        while (size() >= this.maxSize) {
            super.removeFirst();
        }
        return super.add(value);
    }

    public boolean allAboveThreshold() {
        for (Integer value: this) {
            if (value < threshold) {
                return false;
            }
        }
        return true;
    }
}
