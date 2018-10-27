package com.example.alex.foodfinder.Controller.Group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alex.foodfinder.Controller.GroupFragment;
import com.example.alex.foodfinder.Controller.MainActivity;
import com.example.alex.foodfinder.Model.ControllerModel.Group;
import com.example.alex.foodfinder.Model.ControllerModel.Vote;
import com.example.alex.foodfinder.Model.ControllerModel.Enum.VoteStatus;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;

public class GroupAddActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference groupsRef;
    private DatabaseReference databaseUsersGroups;
    private DatabaseReference userRef;
    private EditText groupNameEditText;
    private Button addNewGroupbtn;
    private String groupNameUserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        addNewGroupbtn = findViewById(R.id.add_new_group);
        groupNameEditText = findViewById(R.id.groupName_input);
        groupsRef = database.getReference("groups");
        userRef = database.getReference().child("users").child(auth.getUid());


        addNewGroupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupNameUserInput = groupNameEditText.getText().toString();
                // listener on firbase to check if the new group is already saved for the user
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("groups")) {
                            Boolean gropunameIsOnUserDatabase = false;


                            for (DataSnapshot child : dataSnapshot.child("groups").getChildren()) {
                                String groupNameDatabase = child.getValue().toString();
                                Log.d("groupname", "groupNameDatabase " + groupNameDatabase + " groupnameUserInput " + groupNameUserInput);
                                if (groupNameDatabase.equals(groupNameUserInput)) {
                                    gropunameIsOnUserDatabase = true;
                                    toastGroupisAlreadyInDatabase();
                                    break;

                                }


                            }
                            if (gropunameIsOnUserDatabase == false) {
                                toastNewGroupWasCreated();
                                sendNewGroupAndsendGrouptoUser();
                                backToGroupActivity();
                            }


                        } else {
                            toastNewGroupWasCreated();
                            //TODO split up and make user group and vote in a seperate method
                            sendNewGroupAndsendGrouptoUser();
                            //sendVoteToGroup();
                            backToGroupActivity();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    private void sendVoteToGroup() {
        Vote vote;
        vote = new Vote(VoteStatus.NOTINITIALIZED);



    }


    private void toastGroupisAlreadyInDatabase() {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Groupname is already in the Database", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void toastNewGroupWasCreated() {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "A new group was created", Toast.LENGTH_SHORT);
        toast.show();
    }


    private void backToGroupActivity() {
        Intent intent = new Intent(GroupAddActivity.this, MainActivity.class);
        startActivity(intent);
    }


    // sends the new group to firebase and makes a new entry in groups for the user

    private void sendNewGroupAndsendGrouptoUser() {
        String uid = auth.getCurrentUser().getUid();
        java.util.Date date = new java.util.Date();
        String userName = auth.getCurrentUser().getDisplayName();


        String gid = groupsRef.push().getKey();

        Group group = new Group(gid, groupNameUserInput, uid, date);
        groupsRef.child(gid).setValue(group);
        groupsRef.child(gid).child("members").child(uid).setValue(userName);

        //set vote into group
        Vote vote;
        vote = new Vote(VoteStatus.NOTINITIALIZED);
        groupsRef.child(gid).child("vote").setValue(vote);

        databaseUsersGroups = database.getReference("users").child(uid).child("groups").child(gid);
        databaseUsersGroups.setValue(groupNameUserInput);


    }


}