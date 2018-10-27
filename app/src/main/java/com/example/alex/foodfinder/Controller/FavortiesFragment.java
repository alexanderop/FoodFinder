package com.example.alex.foodfinder.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.alex.foodfinder.Controller.Group.GroupVoteActivity;
import com.example.alex.foodfinder.Helper.CustomListViewArrayAdapter;
import com.example.alex.foodfinder.Helper.FirebaseHelper;
import com.example.alex.foodfinder.Model.ControllerModel.FoodItem;
import com.example.alex.foodfinder.Model.ViewModel.ListItem;
import com.example.alex.foodfinder.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavortiesFragment extends Fragment {

    private ListView listView;
    private OnFragmentInteractionListener mListener;
    private ArrayList<ListItem> listItems;
    private ArrayList<FoodItem> foodItems;
    private CustomListViewArrayAdapter customArrayAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference foodItemsReference;

    public FavortiesFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View resultView = inflater.inflate(R.layout.fragment_favorties, container, false);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        foodItemsReference = database.getReference("users").child(auth.getCurrentUser().getUid()).child("foodItems");
        listItems = new ArrayList<>();
        foodItems = new ArrayList<>();
        listView = (ListView) resultView.findViewById(R.id.favoritesListView);
        initFoodItems();
        initListener();
        FloatingActionButton fab = resultView.findViewById(R.id.addFoodItemFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFoodItemActivity.class);
                startActivity(intent);
            }
        });
        return resultView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
                customArrayAdapter = new CustomListViewArrayAdapter(getActivity().getApplicationContext(), listItems);
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
//                String gid = getIntent().getStringExtra("gid");
//                switch (parentActivity) {
//                    case "MainActivity":
//                intent = new Intent(FavortiesFragment.this, FoodItemDetailViewActivity.class);
//                intent.putExtra("name", name);
//                intent.putExtra("detail", detail);
//                intent.putExtra("address", address);
//                intent.putExtra("imageDrawable", imageDrawable);
//                startActivity(intent);
//                        break;

//                    case "VoteActivity":
//                        intent = new Intent(FoodItemListActivity.this, VoteActivity.class);
//                        intent.putExtra("foodItemId", foodItemId);
//                        intent.putExtra("gid", getIntent().getStringExtra("gid"));
////                        startActivity(intent);
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
//                        break;
//                    case "GroupVoteActivity":
//                        FirebaseHelper.sendFoodItemToGroup(foodItem, gid);
//                        intent = new Intent(FoodItemListActivity.this, GroupVoteActivity.class);
//                        intent.putExtra("gid", gid);
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
//                        break;
//
//                }

            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
