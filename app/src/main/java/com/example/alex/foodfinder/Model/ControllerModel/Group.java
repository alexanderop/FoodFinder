package com.example.alex.foodfinder.Model.ControllerModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private String gid;
    private String name;
    private String creationUserId;
    private Date creationDate;
    private HashMap<String, String> members;
    private HashMap<String, Long> foodMap;

    public Group() {
    }

    public Group(String gid, String name, String creationUserId, Date creationDate, HashMap<String, String> members, HashMap<String, Long> foodMap) {
        this.gid = gid;
        this.name = name;
        this.creationUserId = creationUserId;
        this.creationDate = creationDate;
        this.members = members;
        this.foodMap = foodMap;
    }

    public Group(String gid, String name, String creationUserId, Date creationDate) {
        this.gid = gid;
        this.name = name;
        this.creationUserId = creationUserId;
        this.creationDate = creationDate;
    }

    public Group(String gid, String name) {
        this.gid = gid;
        this.name = name;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationUserId() {
        return creationUserId;
    }

    public void setCreationUserId(String creationUserId) {
        this.creationUserId = creationUserId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, String> members) {
        this.members = members;
    }

    public HashMap<String, Long> getFoodMap() {
        return foodMap;
    }

    public void setFoodMap(HashMap<String, Long> foodMap) {
        this.foodMap = foodMap;
    }
}
