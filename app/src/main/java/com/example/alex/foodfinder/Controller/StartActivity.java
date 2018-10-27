package com.example.alex.foodfinder.Controller;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.alex.foodfinder.Helper.Toaster;
import com.example.alex.foodfinder.Model.ControllerModel.User;
import com.example.alex.foodfinder.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {
    private static final String PATH_TOS = "";
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 200;
    private DatabaseReference usersRef;
    private DatabaseReference emailsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        emailsRef = database.getReference("emails");
        if (isUserLogin()) {
            loginUser();
        }
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setTosUrl(PATH_TOS)
                        .build(), RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                makeNewUserDatabaseRecord();
                loginUser();
            }
            if (resultCode == RESULT_CANCELED) {
                Toaster.makeLongToast(this, "Failed signin");

            }
            return;
        }
        Toaster.makeLongToast(this, "Known response");

    }

    private void makeNewUserDatabaseRecord() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(auth.getCurrentUser().getUid())) {
                    String uid = auth.getCurrentUser().getUid();
                    String username = auth.getCurrentUser().getDisplayName();
                    String email = auth.getCurrentUser().getEmail();
                    List<String> groups = new ArrayList<>();
                    User user = new User(uid, username, email, groups);
                    usersRef.child(uid).setValue(user);
                    emailsRef.child(uid).setValue(email);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private boolean isUserLogin() {
        if (auth.getCurrentUser() != null) {
            return true;
        }
        return false;
    }

    private void loginUser() {
        Intent loginIntent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }


}