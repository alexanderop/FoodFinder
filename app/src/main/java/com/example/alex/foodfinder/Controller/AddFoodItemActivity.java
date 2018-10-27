package com.example.alex.foodfinder.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFoodItemActivity extends AppCompatActivity {

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.detailsEditText)
    EditText detailsEditText;

    @BindView(R.id.addressEditText)
    EditText addressEditText;

    private int PLACE_PICKER_REQUEST = 1;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference foodItemReference;
    private String placeId;
    private double latitude;
    private double longitude;
    private long voteCount;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        placeId = "";
        latitude = 0;
        longitude = 0;
        voteCount = 0;
        toolbar = (Toolbar) findViewById(R.id.done_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createFoodItem() {
        if (nameEditText.getText().toString().equals("")) {
            Toaster.makeLongToast(this, "Missing Name");
        } else {
            FoodItem foodItem;
            String uid = auth.getCurrentUser().getUid();
            foodItemReference = database.getReference("users").child(uid).child("foodItems");
            String id = foodItemReference.push().getKey();
            String details = detailsEditText.getText().toString();
            String name = nameEditText.getText().toString();
            String address = addressEditText.getText().toString();
            String user = auth.getCurrentUser().getDisplayName();
            Date date = new java.util.Date();
            foodItem = new FoodItem(id, name, user, date, details, address, placeId, latitude, longitude, voteCount);
            foodItemReference.child(id).setValue(foodItem);
            Toaster.makeLongToast(this, "Food Item created:  " + name);
            Intent intent = new Intent(AddFoodItemActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.mapsButton)
    public void openPlacePicker(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toaster.makeLongToast(this, toastMsg);
                nameEditText.setText(place.getName());
                addressEditText.setText(place.getAddress().toString());
                placeId = place.getId();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbar.inflateMenu(R.menu.done);
        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                createFoodItem();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
