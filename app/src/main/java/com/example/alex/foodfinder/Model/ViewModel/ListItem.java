package com.example.alex.foodfinder.Model.ViewModel;

import java.util.List;

public class ListItem {

    private int imageDrawable;
    private String name;
    private String details;
    private String address;
    private Long voteCount;
    private String gid;
    private String foodItemId;


    public ListItem() {

    }

    public ListItem(int imageDrawable, String name, String details, String address, String foodItemId) {
        this.imageDrawable = imageDrawable;
        this.name = name;
        this.details = details;
        this.address = address;
        this.foodItemId = foodItemId;
    }

    public ListItem(int imageDrawable, String name, String details, String address, Long voteCount, String gid, String foodItemId) {
        this.imageDrawable = imageDrawable;
        this.name = name;
        this.details = details;
        this.address = address;
        this.voteCount = voteCount;
        this.gid = gid;
        this.foodItemId = foodItemId;
    }

    public Long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(String foodItemId) {
        this.foodItemId = foodItemId;
    }
}


