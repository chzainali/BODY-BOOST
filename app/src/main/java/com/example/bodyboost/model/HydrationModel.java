package com.example.bodyboost.model;

import java.io.Serializable;

public class HydrationModel implements Serializable {
    String  id, userId, title, date, goalHydration, currentHydration, status;
    public HydrationModel() {
    }

    public HydrationModel(String id, String userId, String title, String date, String goalHydration, String currentHydration, String status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.goalHydration = goalHydration;
        this.currentHydration = currentHydration;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGoalHydration() {
        return goalHydration;
    }

    public void setGoalHydration(String goalHydration) {
        this.goalHydration = goalHydration;
    }

    public String getCurrentHydration() {
        return currentHydration;
    }

    public void setCurrentHydration(String currentHydration) {
        this.currentHydration = currentHydration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
