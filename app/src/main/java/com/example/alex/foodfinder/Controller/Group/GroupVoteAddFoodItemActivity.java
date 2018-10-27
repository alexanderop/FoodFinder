package com.example.alex.foodfinder.Controller.Group;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.alex.foodfinder.Controller.AddFoodItemActivity;
import com.example.alex.foodfinder.Controller.MainActivity;
import com.example.alex.foodfinder.Helper.Toaster;
import com.example.alex.foodfinder.Model.ControllerModel.FoodItem;
import com.example.alex.foodfinder.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupVoteAddFoodItemActivity extends AppCompatActivity {

    private String gid;
    private EditText nameEditText, adressEditText, detailEditText;
    private ImageButton mapsBtn;
    private Button sendBtn;
    private int PLACE_PICKER_REQUEST = 1;
    private String placeId;
    private double latitude;
    private double longitude;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference groupVote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_vote_add_food_item);

        Bundle extra = getIntent().getExtras();
        gid = extra.getString("gid");

        initLayout();
        initFirebase();

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startGooglePlacePicker();

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendNewFoodItemToFirebase();

            }
        });
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private void sendNewFoodItemToFirebase() {

        if (nameEditText.getText().toString().equals("")) {
            Toaster.makeLongToast(this, "Missing Name");
        } else {
            FoodItem foodItem;
            String uid = auth.getCurrentUser().getUid();
            groupVote = database.getReference("groups").child(gid).child("vote").child("foodItemList");
            String details = detailEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String address = adressEditText.getText().toString();
            Date date = new java.util.Date();
            Long voteCount = 1L;

            String foodId = groupVote.push().getKey();


            foodItem = new FoodItem(foodId, name, uid, date, details, address, placeId, latitude, longitude, voteCount, gid);

            groupVote.child(foodId).setValue(foodItem);

            Toaster.makeLongToast(this, "Food Item added:  " + name);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toaster.makeLongToast(this, toastMsg);
                nameEditText.setText(place.getName());
                adressEditText.setText(place.getAddress().toString());
                placeId = place.getId();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }
        }
    }

    private void startGooglePlacePicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void initLayout() {
        nameEditText = findViewById(R.id.nameEditText);
        adressEditText = findViewById(R.id.addressEditText);
        detailEditText = findViewById(R.id.detailsEditText);
        mapsBtn = findViewById(R.id.mapsButton);
        sendBtn = findViewById(R.id.sendButton);
    }
}
