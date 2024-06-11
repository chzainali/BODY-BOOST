package com.example.bodyboost.model;

import java.io.Serializable;

public class CaloriesModel implements Serializable {
    String id, userId, title, date, targetCalories, currentCalories, status;

    public CaloriesModel() {
    }

    public CaloriesModel(String id, String userId, String title, String date, String targetCalories, String currentCalories, String status) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.date = date;
        this.targetCalories = targetCalories;
        this.currentCalories = currentCalories;
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

    public String getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(String targetCalories) {
        this.targetCalories = targetCalories;
    }

    public String getCurrentCalories() {
        return currentCalories;
    }

    public void setCurrentCalories(String currentCalories) {
        this.currentCalories = currentCalories;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}