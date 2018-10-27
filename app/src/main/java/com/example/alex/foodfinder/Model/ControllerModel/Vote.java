package com.example.alex.foodfinder.Model.ControllerModel;




import com.example.alex.foodfinder.Model.ControllerModel.Enum.VoteStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vote {


    private Date startTime;
    private Date endTime;
    private VoteStatus voteStatus;
    private Map<FoodItem, Integer> foodItemList;
    private User creationUser;
    private List<User> alreadyVotedUsers;

    public Vote() {
    }

    public Vote(Date startTime, Date endTime, VoteStatus voteStatus, Map<FoodItem, Integer> foodItemList, User creationUser, List<User> alreadyVotedUsers) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.voteStatus = voteStatus;
        this.foodItemList = foodItemList;
        this.creationUser = creationUser;
        this.alreadyVotedUsers = alreadyVotedUsers;
    }

    public Vote(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public VoteStatus getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(VoteStatus voteStatus) {
        this.voteStatus = voteStatus;
    }

    public Map<FoodItem, Integer> getFoodItemList() {
        return foodItemList;
    }

    public void setFoodItemList(Map<FoodItem, Integer> foodItemList) {
        this.foodItemList = foodItemList;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }

    public List<User> getAlreadyVotedUsers() {
        return alreadyVotedUsers;
    }

    public void setAlreadyVotedUsers(List<User> alreadyVotedUsers) {
        this.alreadyVotedUsers = alreadyVotedUsers;
    }
}
