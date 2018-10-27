package com.example.alex.foodfinder.Controller.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class GroupActivity extends AppCompatActivity {

    private Button makeNewGroupbtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseUserGroups;
    private List<String> groupList;
    private ArrayAdapter<String> adapter;
    private List<String> listItems;
    private ListView groupListview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        makeNewGroupbtn = findViewById(R.id.make_new_group);
        groupListview = findViewById(R.id.listViewGroups);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseUserGroups = database.getReference().child("users").child(auth.getUid());
        listItems = new ArrayList<>();
        groupList = new ArrayList<>();


        databaseUserGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("groups")) {


                    for (DataSnapshot child : dataSnapshot.child("groups").getChildren()) {
                        String groupName = child.getValue().toString();
                        String gid = child.getKey();
                        groupList.add(gid);
                        listItems.add(groupName);

                    }

                    adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    groupListview.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        makeNewGroupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, GroupAddActivity.class);
                startActivity(intent);
            }
        });

        groupListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String gid = groupList.get(i);
                String groupname = listItems.get(i);


                Intent intent = new Intent(GroupActivity.this, GroupDetailActivity.class);
                intent.putExtra("gid", gid);
                intent.putExtra("groupname",groupname);
                startActivity(intent);


            }
        });

    }
}