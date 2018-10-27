package com.example.alex.foodfinder.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.alex.foodfinder.Controller.Group.GroupVoteActivity;
import com.example.alex.foodfinder.Helper.CustomListViewArrayAdapter;
import com.example.alex.foodfinder.Helper.FirebaseHelper;
import com.example.alex.foodfinder.Model.ControllerModel.FoodItem;
import com.example.alex.foodfinder.Model.ViewModel.ListItem;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodItemListActivity extends AppCompatActivity {

    @BindView(R.id.ListView)
    ListView listView;

    private ArrayList<ListItem> listItems;
    private ArrayList<FoodItem> foodItems;
    private CustomListViewArrayAdapter customArrayAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference foodItemsReference;
    private String parentActivity;
    private String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_list);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        foodItemsReference = database.getReference("users").child(auth.getCurrentUser().getUid()).child("foodItems");
        listItems = new ArrayList<>();
        foodItems = new ArrayList<>();
        parentActivity = getIntent().getStringExtra("parent");
        initFoodItems();
        initListener();
    }

    private void initFoodItems() {
        foodItemsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = postSnapshot.getValue(FoodItem.class);
                    listItems.add(new ListItem(R.drawable.baseline_fastfood_black_24dp, foodItem.getName(), foodItem.getDetails(), foodItem.getAddress(), foodItem.getId()));
                    foodItems.add(foodItem);
                }
                customArrayAdapter = new CustomListViewArrayAdapter(getApplicationContext(), listItems);
                listView.setAdapter(customArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(this.toString(), "loadFoodItem:onCancelled", databaseError.toException());
            }
        });
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem listItem = customArrayAdapter.getItem(position);
                String name = listItem.getName();
                String detail = listItem.getDetails();
                String address = listItem.getAddress();
                int imageDrawable = listItem.getImageDrawable();
                String foodItemId = listItem.getFoodItemId();
                Intent intent;

                FoodItem foodItem = foodItems.get(position);
                String gid = getIntent().getStringExtra("gid");
                switch (parentActivity) {
                    case "MainActivity":
                        intent = new Intent(FoodItemListActivity.this, FoodItemDetailViewActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("detail", detail);
                        intent.putExtra("address", address);
                        intent.putExtra("imageDrawable", imageDrawable);
                        startActivity(intent);
                        break;

                    case "VoteActivity":
                        intent = new Intent(FoodItemListActivity.this, VoteActivity.class);
                        intent.putExtra("foodItemId", foodItemId);
                        intent.putExtra("gid", getIntent().getStringExtra("gid"));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        break;
                    case "GroupVoteActivity":


                        FirebaseDatabase database;
                        DatabaseReference groupVote;

                        database = FirebaseDatabase.getInstance();
                        groupVote = database.getReference("groups").child(gid).child("vote").child("foodItemList").child(foodItem.getPlaceId());

                        //groupVote.push().setValue(foodItem);
                        foodItem.setId(foodItem.getPlaceId());

                        groupVote.setValue(foodItem);


                        intent = new Intent(FoodItemListActivity.this, GroupVoteActivity.class);
                        intent.putExtra("gid", gid);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                        break;

                }

            }
        });
    }
}
