package com.example.planetze35;

public class Habit {
    private final String name;
    private final String category;
    private final String impact;

    public Habit(String name, String category, String impact) {
        this.name = name;
        this.category = category;
        this.impact = impact;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImpact() {
        return impact;
    }
}
