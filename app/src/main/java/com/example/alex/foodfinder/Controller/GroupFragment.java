package com.example.alex.foodfinder.Controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alex.foodfinder.Controller.Group.GroupActivity;
import com.example.alex.foodfinder.Controller.Group.GroupAddActivity;
import com.example.alex.foodfinder.Controller.Group.GroupDetailActivity;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class GroupFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button makeNewGroupbtn;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseUserGroups;
    private List<String> groupList;
    private ArrayAdapter<String> adapter;
    private List<String> listItems;
    private ListView groupListview;

    public GroupFragment() {
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
        View resultView = inflater.inflate(R.layout.fragment_group, container, false);
        makeNewGroupbtn = resultView.findViewById(R.id.make_new_group);
        groupListview = resultView.findViewById(R.id.listViewGroups);
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

                    adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, listItems);
                    groupListview.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        groupListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String gid = groupList.get(i);
                String groupname = listItems.get(i);

                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
                intent.putExtra("gid", gid);
                intent.putExtra("groupname", groupname);
                startActivity(intent);
            }
        });
        return resultView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
