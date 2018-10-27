package com.example.alex.foodfinder.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.foodfinder.Model.ViewModel.ListItem;
import com.example.alex.foodfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewVoteArrayAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    private List<ListItem> itemsList;
    private Boolean mSwitch;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference countRef;

    public CustomListViewVoteArrayAdapter(@NonNull Context context, ArrayList<ListItem> list) {
        super(context, 0, list);
        mContext = context;
        itemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.layout_list_item_vote, parent, false);

//TODO implement vote logic

        final ListItem currentItem = itemsList.get(position);

        String gid = currentItem.getGid();
        //countRef = database.getReference().child("groups").child(gid).child("vote");


        ImageView image = listItem.findViewById(R.id.imageView_image);
        image.setImageResource(currentItem.getImageDrawable());

        TextView name = listItem.findViewById(R.id.textView_name);
        name.setText(currentItem.getName());

        TextView address = listItem.findViewById(R.id.textView_address);
        address.setText(currentItem.getAddress());

        TextView details = listItem.findViewById(R.id.textView_details);
        details.setText(currentItem.getDetails());

        TextView count = listItem.findViewById(R.id.textView_Count);
        count.setText(String.valueOf(currentItem.getVoteCount()));


        ImageButton buttonInc = listItem.findViewById(R.id.buttonIncCount);

        ImageButton buttonDec = listItem.findViewById(R.id.buttonDecCount);

        buttonInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return listItem;
    }


}

