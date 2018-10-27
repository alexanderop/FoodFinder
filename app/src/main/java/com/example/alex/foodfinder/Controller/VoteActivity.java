package com.example.alex.foodfinder.Controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.alex.foodfinder.Helper.CustomListViewArrayAdapter;
import com.example.alex.foodfinder.Helper.CustomListViewVoteArrayAdapter;
import com.example.alex.foodfinder.Helper.Toaster;
import com.example.alex.foodfinder.Model.ControllerModel.FoodItem;
import com.example.alex.foodfinder.Model.ControllerModel.Group;
import com.example.alex.foodfinder.Model.ControllerModel.Vote;
import com.example.alex.foodfinder.Model.ViewModel.ListItem;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoteActivity extends AppCompatActivity {

    @BindView(R.id.groupSpinner)
    Spinner groupSpinner;

    @BindView(R.id.foodItemListView)
    ListView listView;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference foodItemsReference;
    private DatabaseReference userReference;
    private DatabaseReference voteReference;
    private DatabaseReference groupVoteRef;
    private DatabaseReference groupRef;
    private ArrayList<Group> groupsList;
    private ArrayList<FoodItem> foodItems;
    private ArrayList<FoodItem> allFoodItems;
    private ArrayList<ListItem> listItems;
    private ArrayList<String> spinnerNameList;
    private CustomListViewVoteArrayAdapter customArrayAdapter;
    private Boolean isChecked;
    private List<View> views;
    private ImageButton btnAddNewFoodItem;
    private Button submitButton;
    private Button home;
    private Group selectedGroup;
    private Vote activeVote;
    private String gid;
    private String selectedFoodItem;
    private Map<String, Long> foodMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        foodItemsReference = database.getReference("users").child(auth.getCurrentUser().getUid()).child("foodItems");
        userReference = database.getReference("users").child(auth.getUid());
        groupRef = database.getReference("groups");
        groupsList = new ArrayList<>();
        foodItems = new ArrayList<>();
        allFoodItems = new ArrayList<>();
        listItems = new ArrayList<>();
        views = new ArrayList<>();
        spinnerNameList = new ArrayList<>();
        foodMap = new HashMap<>();
        isChecked = false;
        btnAddNewFoodItem = findViewById(R.id.addFoodItemButton);
        submitButton = findViewById(R.id.submitButton);
        home = findViewById(R.id.homeButton);

        initGroup();
        initFoodItems();
        setSpinner();
        setSpinnerItemClickListener();

        btnAddNewFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodItemButtonClick();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonClick();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedFoodItem = customArrayAdapter.getItem(position).getFoodItemId();
                for (View view1 : views) {
                    view1.setBackgroundColor(Color.WHITE);
                }
                view.setBackgroundColor(getColor(R.color.colorAccent));
                if (!views.contains(view)) {
                    views.add(view);
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(VoteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initGroup() {
        gid = getIntent().getStringExtra("gid");
        groupVoteRef = database.getReference("groups").child(gid).child("vote");
        voteReference = database.getReference("groups").child(gid).child("foodItems");
        groupRef.child(gid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    Group group = new Group();
                    group.setCreationUserId((String) objectMap.get("creationUserId"));
                    group.setGid((String) objectMap.get("gid"));
                    group.setMembers((HashMap<String, String>) objectMap.get("members"));
                    group.setName((String) objectMap.get("name"));
                    group.setFoodMap((HashMap<String, Long>) objectMap.get("foodItems"));
                    selectedGroup = group;
                    foodMap = selectedGroup.getFoodMap() == null ? new HashMap<String, Long>() : selectedGroup.getFoodMap();
                    for (Map.Entry<String, Long> entry : foodMap.entrySet()) {
                        String key = entry.getKey();
                        final Long value = entry.getValue();
                        foodItemsReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                                foodItem.setVoteCount(value);
                                addFoodItemToList(foodItem, gid);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(toString(), "loadFoodItem:onCancelled", databaseError.toException());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(toString(), "loadFoodItem:onCancelled", databaseError.toException());
            }
        });
    }


    private void addFoodItemToList(FoodItem foodItem, String gid) {
        int imageDrawable = R.drawable.baseline_fastfood_black_24dp;
        String name = foodItem.getName();
        String details = foodItem.getDetails();
        String address = foodItem.getAddress();
        Long voteCount = foodItem.getVoteCount();
        String foodItemId = foodItem.getId();
        ListItem listItem = new ListItem(imageDrawable, name, details, address, voteCount, gid, foodItemId);
        if (hasItem(listItems, listItem)) {
            Toaster.makeShortToast(VoteActivity.this, "Steht bereits zur Wahl");
        } else {
            foodMap.put(foodItemId, voteCount);
            voteReference.setValue(foodMap);
            listItems.add(listItem);
            fillListView();
        }
    }

    private void foodItemButtonClick() {
        Intent intent = new Intent(VoteActivity.this, FoodItemListActivity.class);
        intent.putExtra("parent", "VoteActivity");
        intent.putExtra("gid", gid);
        startActivityForResult(intent, 1);
    }

    private void submitButtonClick() {
//        if (selectedFoodItem != null && !selectedFoodItem.equals("")) {
//            foodItemsReference.child(selectedFoodItem).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
//                    String foodId = foodItem.getId();
//                    Long count = foodMap.get(foodId);
//                    foodMap.remove(foodId);
//                    count += 1;
//                    foodMap.put(foodId, count);
//                    voteReference.setValue(foodMap);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.w(toString(), "loadFoodItem:onCancelled", databaseError.toException());
//                }
//            });
//        } else {
//            Toaster.makeShortToast(VoteActivity.this, "Please select an Item");
//        }
        Long count = foodMap.get(selectedFoodItem);
        foodMap.remove(selectedFoodItem);
        count += 1;
        foodMap.put(selectedFoodItem, count);
        voteReference.setValue(foodMap);

        for (ListItem item : listItems) {
            if (item.getFoodItemId().equals(selectedFoodItem)) {
                item.setVoteCount(count);
            }
        }
        fillListView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String foodItemId = data.getStringExtra("foodItemId");
                foodItemsReference.child(foodItemId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FoodItem foodItem = dataSnapshot.getValue(FoodItem.class);
                        addFoodItemToList(foodItem, gid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(toString(), "loadFoodItem:onCancelled", databaseError.toException());
                    }
                });
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    //grabs all FoodItems from DB and puts them into a List
    public void initFoodItems() {
        foodItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = postSnapshot.getValue(FoodItem.class);
                    allFoodItems.add(foodItem);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(toString(), "loadFoodItem:onCancelled", databaseError.toException());
            }
        });
    }

    public void fillListView() {
        customArrayAdapter = new CustomListViewVoteArrayAdapter(getApplicationContext(), listItems);
        listView.setAdapter(customArrayAdapter);
    }

    public boolean hasItem(List<ListItem> items, ListItem item) {
        for (ListItem tmpItem : items) {
            if (item.getFoodItemId().equals(tmpItem.getFoodItemId())) {
                return true;
            }
        }
        return false;
    }


    private void setSpinner() {
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("groups")) {
                    for (DataSnapshot child : dataSnapshot.child("groups").getChildren()) {
                        String groupname = (String) child.getValue();
                        String gid = (String) child.getKey();
                        Group group = new Group(gid, groupname);
                        groupsList.add(group);
                        spinnerNameList.add(groupname);
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, spinnerNameList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    groupSpinner.setAdapter(dataAdapter);
                } else {
                    //maybe toast something
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setSpinnerItemClickListener() {
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String groupId = groupsList.get(i).getGid();
                if (!groupId.isEmpty() && groupId != null) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

//****** GETTER/SETTER *****

    public Group getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(Group selectedGroup) {
        selectedGroup = selectedGroup;
    }
}