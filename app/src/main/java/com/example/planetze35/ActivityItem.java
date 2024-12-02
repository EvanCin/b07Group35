package com.example.planetze35;

public class ActivityItem {
    private String id;
    private String activityName;
    private String co2Value;

    // Constructor
    public ActivityItem(String activityName, String co2Value) {
        this.activityName = activityName;
        this.co2Value = co2Value;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(String co2Value) {
        this.co2Value = co2Value;
    }
}
