package com.kavinsaravanan.breakindetector;

import java.util.ArrayList;
class Monitor {
  private ArrayList<Integer> values = new ArrayList<Integer>();
  private int size;
  private int threshold;

  public Monitor(int size, int threshold) {
    this.size = size;
    this.threshold = threshold;
  }

  public void addValue(int newValue) {
    // if the array list already has "limit" values
    // remove the oldest one before add this newValue
    if (values.size() == this.size) {
      values.remove(0);
    }
    values.add(newValue);
  }

  public boolean addAndCheck(int newValue) {
    addValue(newValue);
    int counter = 0;
    for (int i = 0; i < values.size(); i++) {
      if(values.get(i) > threshold) {
        counter++;
        if(counter == size) {
          return true;
        }
      }
    }
    return false;
  }

  public String toString() {
    return "Values: " + values;
  }
}