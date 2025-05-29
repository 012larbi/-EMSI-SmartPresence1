package com.example.emsismartpresence;

public class PlanningItem {
    private String text;
    private long timestamp;

    public PlanningItem() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}