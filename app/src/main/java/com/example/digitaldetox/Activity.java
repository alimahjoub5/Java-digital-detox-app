package com.example.digitaldetox;

public class Activity {
    private int id;
    private String name;
    private String description;
    private boolean completed;

    public Activity() {
    }

    public Activity(String name, String description) {
        this.name = name;
        this.description = description;
        this.completed = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

