package com.example.alex.foodfinder.Controller.Group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.foodfinder.Controller.MainActivity;
import com.example.alex.foodfinder.Model.ControllerModel.Enum.VoteStatus;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailActivity extends AppCompatActivity {

    private Button addMemberBtn, voteBtn, leaveBtn, deleteBtn;
    private TextView groupnameText;
    private String gid, groupname, memberSearchInput;
    private ArrayAdapter<String> adapter;
    private List<String> listItems;
    private ListView memberListView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference groupMembersRef;
    private DatabaseReference groupVoteRef;
    private DatabaseReference rootRef;
    private Boolean emailIsInDatabase, isUserAlreadyInGroup;
    private FirebaseAuth firebaseAuth;
    private Boolean isMemberListEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        //get elements from layout
        addMemberBtn = findViewById(R.id.addMember_button);
        voteBtn = findViewById(R.id.vote_button);
        leaveBtn = findViewById(R.id.leave_button);
        deleteBtn = findViewById(R.id.delete_button);
        groupnameText = findViewById(R.id.groupName_textView);
        memberListView = findViewById(R.id.memberList_listView);
        listItems = new ArrayList<>();

        // get gid and groupname from groupActivity and set it in view
        Bundle extra = getIntent().getExtras();
        gid = extra.getString("gid");
        groupname = extra.getString("groupname");
        groupnameText.setText(groupname);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupMembersRef = firebaseDatabase.getReference().child("groups").child(gid);
        groupVoteRef = firebaseDatabase.getReference().child("groups").child(gid).child("vote");
        rootRef = FirebaseDatabase.getInstance().getReference();

        setMembersToList();


        // add new user from the firebase database to group
        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAddAction();
            }
        });


        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleVoteAction();

            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ifMemberListIsOneDeleteGroup();


            }
        });


        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveGroupFromFirebase();


            }
        });


    }

    private void ifMemberListIsOneDeleteGroup() {

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("groups");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datasnapshot", dataSnapshot.toString());
                Log.d("dataSnapshot", gid);

                if (dataSnapshot.hasChild(gid)) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.child(gid).getValue();
                    HashMap<String, String> members = new HashMap<String, String>();
                    members = (HashMap<String, String>) objectMap.get("members");


                    if (members.size() == 1) {
                        Context context = getApplicationContext();
                        CharSequence text = "The group is deleted";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        //delete group

                        deleteGroup();
                        deleteGroupInUser();
                        backToMainActivity();

                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "You are the not the only member you can not delte the group";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void backToMainActivity() {
        Intent intent = new Intent(GroupDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void leaveGroupFromFirebase() {
        deleteGroupInUser();
        deleteUserInGroup();
        backToMainActivity();

    }

    private void deleteUserInGroup() {
        firebaseDatabase.getReference().child("groups").child(gid).child("member").child(firebaseAuth.getUid()).removeValue();

    }

    private void deleteGroup() {
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("groups").child(gid);
        databaseReference.setValue(null);

    }

    private void deleteGroupInUser() {
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("groups").child(gid);
        databaseReference.setValue(null);

    }


    private void setMembersToList() {
        //show all members in group in a simple list view
        groupMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("members")) {

                    for (DataSnapshot child : dataSnapshot.child("members").getChildren()) {
                        String memberName = child.getValue().toString();
                        listItems.add(memberName);

                    }

                    adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    memberListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void handleVoteAction() {
        //first check if vote is already started if so go directly to vote activity

        groupVoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String voteStatus = (String) dataSnapshot.child("voteStatus").getValue();
                if (voteStatus.equals(VoteStatus.NOTINITIALIZED.toString())) {
                    makeNewDialogVote();
                } else {
                    toast("there is already a vote active");
                    startNewVoteActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void makeNewDialogVote() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupDetailActivity.this);
        builder1.setMessage("Do you want to start a new Vote for the Group.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        changeVoteStatusinFirebaseToActive();
                        startNewVoteActivity();
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

    private void changeVoteStatusinFirebaseToActive() {
        groupVoteRef.child("voteStatus").setValue(VoteStatus.ACTIVE);
    }

    private void startNewVoteActivity() {
        Intent intent = new Intent(GroupDetailActivity.this, GroupVoteActivity.class);
        intent.putExtra("gid", gid);
        startActivity(intent);
    }

    private void handleAddAction() {

        LayoutInflater layoutInflater = LayoutInflater.from(GroupDetailActivity.this);
        View promptView = layoutInflater.inflate(R.layout.layout_input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GroupDetailActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        memberSearchInput = editText.getText().toString();
                        if (memberSearchInput.matches("")) {
                            String message = "Please enter a valid email";
                            toast(message);
                            dialog.cancel();
                        } else {
                            addMembertoFirebaseIfExist();

                        }


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


    //TODO maybe use a diffrent database system because here we download all the data to check if "emails" exist and REFACTOR
    private void addMembertoFirebaseIfExist() {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("emails")) {
                    emailIsInDatabase = false;
                    String uid = null, emailName = null;

                    for (DataSnapshot child : dataSnapshot.child("emails").getChildren()) {
                        emailName = child.getValue().toString();
                        if (emailName.equals(memberSearchInput)) {
                            uid = child.getKey();
                            emailIsInDatabase = true;
                            break;
                        }
                    }
                    if (!emailIsInDatabase) {
                        String message = "Email is not in Database";
                        toast(message);
                    }


                    if (emailIsInDatabase) {
                        HashMap<String, Object> userSnapshot = (HashMap<String, Object>) dataSnapshot.child("users").child(uid).child("groups").getValue();
                        isUserAlreadyInGroup = userSnapshot.containsKey(gid);
                        if (isUserAlreadyInGroup) {
                            String message = "User is already in group";
                            toast(message);
                        } else if (emailIsInDatabase && !isUserAlreadyInGroup) {
                            HashMap<String, Object> usernameHashMap = (HashMap<String, Object>) dataSnapshot.child("users").child(uid).getValue();
                            String username = usernameHashMap.get("username").toString();

                            //save new Member in Group
                            DatabaseReference groupmembersRef = FirebaseDatabase.getInstance().getReference().child("groups").child(gid).child("members").child(uid);
                            groupmembersRef.setValue(username);

                            //save new Member in Users
                            DatabaseReference usergroupRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("groups").child(gid);
                            usergroupRef.setValue(groupname);

                            String message = "New Member was added";
                            toast(message);

                            finish();
                            startActivity(getIntent());


                        }


                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void toast(String message) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }


}
