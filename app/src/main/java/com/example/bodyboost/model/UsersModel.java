package com.example.bodyboost.model;

public class UsersModel {
    String id, userName, email, password, age, gender, targetWeight, currentWeight, targetBodyFat, currentBodyFat, targetWaist, currentWaist;

    public UsersModel() {
    }

    public UsersModel(String id, String userName, String email, String password, String age, String gender, String targetWeight, String currentWeight, String targetBodyFat, String currentBodyFat, String targetWaist, String currentWaist) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.targetWeight = targetWeight;
        this.currentWeight = currentWeight;
        this.targetBodyFat = targetBodyFat;
        this.currentBodyFat = currentBodyFat;
        this.targetWaist = targetWaist;
        this.currentWaist = currentWaist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(String targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(String currentWeight) {
        this.currentWeight = currentWeight;
    }

    public String getTargetBodyFat() {
        return targetBodyFat;
    }

    public void setTargetBodyFat(String targetBodyFat) {
        this.targetBodyFat = targetBodyFat;
    }

    public String getCurrentBodyFat() {
        return currentBodyFat;
    }

    public void setCurrentBodyFat(String currentBodyFat) {
        this.currentBodyFat = currentBodyFat;
    }

    public String getTargetWaist() {
        return targetWaist;
    }

    public void setTargetWaist(String targetWaist) {
        this.targetWaist = targetWaist;
    }

    public String getCurrentWaist() {
        return currentWaist;
    }

    public void setCurrentWaist(String currentWaist) {
        this.currentWaist = currentWaist;
    }
}
