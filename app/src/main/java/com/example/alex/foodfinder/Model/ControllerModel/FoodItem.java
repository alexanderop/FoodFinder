package com.example.alex.foodfinder.Model.ControllerModel;

import java.util.Date;
import java.util.Map;

public class FoodItem {
    private String id;
    private String name;
    private String creationUser;
    private Date creationDate;
    private String details;
    private String address;
    private String placeId;
    private double latitude;
    private double longitude;
    private Long voteCount;
    private String groupId;


    public FoodItem() {

    }

    public FoodItem(String id, String name, String details, String creationUser, Date creationDate, String address, String placeId, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.creationUser = creationUser;
        this.creationDate = creationDate;
        this.details = details;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public FoodItem(String id, String name, String creationUser, Date creationDate, String details, String address, String placeId, double latitude, double longitude, Long voteCount) {
        this.id = id;
        this.name = name;
        this.creationUser = creationUser;
        this.creationDate = creationDate;
        this.details = details;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.voteCount = voteCount;
    }

    public FoodItem(String name, String creationUser, Date creationDate, String details, String address, String placeId, double latitude, double longitude, Long voteCount, String groupId) {
        this.name = name;
        this.creationUser = creationUser;
        this.creationDate = creationDate;
        this.details = details;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.voteCount = voteCount;
        this.groupId = groupId;
    }

    public FoodItem(String foodId, String name, String uid, Date date, String details, String address, String placeId, double latitude, double longitude, Long voteCount, String gid) {
        this.id = foodId;
        this.name = name;
        this.creationUser = uid;
        this.creationDate = date;
        this.details = details;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.voteCount = voteCount;
        this.groupId = gid;

    }

    //*****Getter/Setter*****


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(String creationUser) {
        this.creationUser = creationUser;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
