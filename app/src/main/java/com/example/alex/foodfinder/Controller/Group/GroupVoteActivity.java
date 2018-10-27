package com.example.alex.foodfinder.Controller.Group;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alex.foodfinder.Controller.FoodItemListActivity;
import com.example.alex.foodfinder.Controller.MainActivity;
import com.example.alex.foodfinder.Helper.CustomListViewVoteArrayAdapter;
import com.example.alex.foodfinder.Model.ControllerModel.Enum.VoteStatus;
import com.example.alex.foodfinder.Model.ControllerModel.FoodItem;
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
import java.util.List;

public class GroupVoteActivity extends AppCompatActivity {

    private ImageButton btnAddNewFoodItem;
    private ImageButton btnAddFavoritNewFoodItem;
    private ImageButton deleteVoteButton;
    private ListView listViewFoodItems;
    private String gid;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference groupVoteRef;
    private DatabaseReference userVotedList;
    private DatabaseReference groupRef;

    private ArrayList<FoodItem> foodItemList;
    private ArrayList<String> foodItemListIds;
    private ArrayList<ListItem> listItems;
    private CustomListViewVoteArrayAdapter customListViewVoteArrayAdapter;
    private long counter;


    private String selectedFoodItem;

    private List<View> views;


    private Bundle extra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_vote);
        extra = getIntent().getExtras();
        views = new ArrayList<>();


        setGidFromLastActivity();


        initList();

        initFirebase();

        initLayout();

        setFoodListViewItems();


        //listeners
        btnAddNewFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // also send new FoodItem to the Database if user wants to
                startActivityVoteAddFoodItem();

            }
        });

        btnAddFavoritNewFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // also send new FoodItem to the Database if user wants to
                startActivityFoodItemList();
            }
        });

        deleteVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete vote if every member has voted

                checkIfEveryMemberHasVotedAndDeleteVote();
            }
        });


        // set the selected items green and saves the items in firebase
        listViewFoodItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedFoodItem = foodItemList.get(i).getId();


                for (View view1 : views) {
                    view1.setBackgroundColor(Color.WHITE);
                }
                view.setBackgroundColor(getColor(R.color.colorAccent));
                if (!views.contains(view)) {
                    views.add(view);
                }

                //TODO check if user has already voted and delete old vote
                //send to firbase where user has voted
                sendToFirebase();

                Intent intent = getIntent();
                finish();
                startActivity(intent);

                //getCounterForListItemAndSetGlobalCounter();


            }
        });


    }

    private void checkIfEveryMemberHasVotedAndDeleteVote() {

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("vote").hasChild("userVotedForFoodItem")) {

                    long sizeOfUserVotedForFoodItem = 0;
                    long sizeOfMembersInGroup = 0;
                    sizeOfUserVotedForFoodItem = dataSnapshot.child("vote").child("userVotedForFoodItem").getChildrenCount();
                    sizeOfMembersInGroup = dataSnapshot.child("members").getChildrenCount();


                    if (sizeOfMembersInGroup == sizeOfUserVotedForFoodItem) {
                        //say every member has voted and ask user if he wants to delete
                        makeDialogEveryMemberHasVoted();
                    } else {
                        //say how many members has not voted and ask if he wants to delete anyway
                        long sizeOFMembersWhoHaveNotVoted = sizeOfMembersInGroup - sizeOfUserVotedForFoodItem;

                        makeDialogNotEveryMemberHasVoted(sizeOFMembersWhoHaveNotVoted);
                    }


                } else {
                    //toast no member has voted yet

                    Context context = getApplicationContext();
                    CharSequence text = "No member has voted yet can't delete vote";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void makeDialogEveryMemberHasVoted() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupVoteActivity.this);
        builder1.setMessage("Every Member has Voted do you want to delete the vote?.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteVoteInFirebase();
                        goBackToGroupDetailActivity();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void deleteVoteInFirebase() {
        Vote vote;
        vote = new Vote(VoteStatus.NOTINITIALIZED);
       // groupRef.child(gid).child("vote").removeValue();
        groupRef.child("vote").setValue(vote);

    }

    private void makeDialogNotEveryMemberHasVoted(long sizeOFMembersWhoHaveNotVoted) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupVoteActivity.this);
        builder1.setMessage(sizeOFMembersWhoHaveNotVoted + " Have not Voted yet are you sure you want to delete the vote?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteVoteInFirebase();
                        goBackToGroupDetailActivity();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void goBackToGroupDetailActivity() {
        Intent intent = new Intent(GroupVoteActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void incCounterAndSaveInFirebase() {
        counter = counter + 1;
        DatabaseReference databaseReference = database.getReference("groups").child(gid).child("vote").child("foodItemList").child(selectedFoodItem).child("voteCount");
        databaseReference.setValue(counter);

        Intent intent = new Intent(GroupVoteActivity.this, GroupVoteActivity.class);
        intent.putExtra("gid", gid);
        startActivity(intent);


    }

    private void getCounterForListItemAndSetGlobalCounter() {
        groupVoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("foodItemList")) {

                    for (DataSnapshot child : dataSnapshot.child("foodItemList").getChildren()) {
                        FoodItem foodItem = child.getValue(FoodItem.class);
                        addFoodItemToList(foodItem);
                        String id = child.getKey();

                        if (id.equals(selectedFoodItem)) {
                            counter = foodItem.getVoteCount();
                            makeToast(String.valueOf(counter));

                            incCounterAndSaveInFirebase();
                        }


                    }


                } else {
                    //maybe toast something
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendToFirebase() {
        DatabaseReference databaseReference = database.getReference("groups").child(gid).child("vote").child("userVotedForFoodItem");
        //HashMap hashMap = new HashMap();
        //hashMap.put(auth.getUid(), selectedFoodItem);
        databaseReference.child(auth.getUid()).setValue(selectedFoodItem);
    }

    private void makeToast(String click) {
        Context context = getApplicationContext();
        CharSequence text = click;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }


    private void handleFoodItemActivity() {

    }

    private void startActivityFoodItemList() {
        Intent intent = new Intent(GroupVoteActivity.this, FoodItemListActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("parent", "GroupVoteActivity");
        startActivity(intent);
    }


    private void initList() {
        foodItemList = new ArrayList<>();
        listItems = new ArrayList<>();
        foodItemListIds = new ArrayList<>();
    }

    private void setGidFromLastActivity() {
        gid = extra.getString("gid");
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        groupVoteRef = database.getReference("groups").child(gid).child("vote");
        userVotedList = database.getReference("userVotedList").child(gid).child(auth.getUid());
        groupRef = database.getReference("groups").child(gid);
    }

    private void startActivityVoteAddFoodItem() {
        Intent intent = new Intent(GroupVoteActivity.this, GroupVoteAddFoodItemActivity.class);
        intent.putExtra("gid", gid);
        startActivity(intent);
    }

    private void initLayout() {
        btnAddNewFoodItem = findViewById(R.id.addFoodItemButton);
        btnAddFavoritNewFoodItem = findViewById(R.id.addFavoritFoodItemButton);
        listViewFoodItems = findViewById(R.id.foodItemListView);
        deleteVoteButton = findViewById(R.id.deleteVoteButton);

    }

    private void setFoodListViewItems() {

        groupVoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("foodItemList")) {

                    for (DataSnapshot child : dataSnapshot.child("foodItemList").getChildren()) {
                        FoodItem foodItem = child.getValue(FoodItem.class);
                        //String id = child.getKey();
                        //foodItemListIds.add(id);
                        foodItemList.add(foodItem);
                        //Log.d("fooditems",foodItem.getName());
                    }


                    // only do if a user has voted
                    if (dataSnapshot.hasChild("userVotedForFoodItem")) {
                        //set every counter to 0

                        for (FoodItem foodItem1 : foodItemList) {
                            foodItem1.setVoteCount((long) 0);

                        }


                        // get userVotedForFoodItem
                        ArrayList<String> votedFoodItem = new ArrayList<>();

                        for (DataSnapshot child1 : dataSnapshot.child("userVotedForFoodItem").getChildren()) {
                            votedFoodItem.add((String) child1.getValue());
                        }

                        //inc counter for every item that a user has voted for

                        for (String votedItem : votedFoodItem) {
                            for (FoodItem foodItem1 : foodItemList) {
                                if (foodItem1.getId().equals(votedItem)) {
                                    foodItem1.setVoteCount(foodItem1.getVoteCount() + 1);
                                }
                            }

                        }

                    }

                    //add fooditems to list for view


                    for (FoodItem foodItem1 : foodItemList) {
                        addFoodItemToList(foodItem1);

                    }


                    customListViewVoteArrayAdapter = new CustomListViewVoteArrayAdapter(getApplicationContext(), listItems);
                    listViewFoodItems.setAdapter(customListViewVoteArrayAdapter);


                } else {
                    //maybe toast something
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addFoodItemToList(FoodItem foodItem) {
        int imageDrawable = R.drawable.baseline_fastfood_black_24dp;
        String name = foodItem.getName();
        String details = foodItem.getDetails();
        String address = foodItem.getAddress();
        Long voteCount = foodItem.getVoteCount();
        String foodItemId = foodItem.getId();
        ListItem listItem = new ListItem(imageDrawable, name, details, address, voteCount, gid, foodItemId);
        listItems.add(listItem);
    }
}
