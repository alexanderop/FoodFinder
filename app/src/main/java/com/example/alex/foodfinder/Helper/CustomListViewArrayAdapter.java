package com.example.alex.foodfinder.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.foodfinder.Model.ViewModel.ListItem;
import com.example.alex.foodfinder.R;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewArrayAdapter extends ArrayAdapter<ListItem> {
    private Context mContext;
    private List<ListItem> itemsList;
    private Boolean mSwitch;

    public CustomListViewArrayAdapter(@NonNull Context context, ArrayList<ListItem> list) {
        super(context, 0, list);
        mContext = context;
        itemsList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.layout_vote_list, parent, false);

        ListItem currentItem = itemsList.get(position);

        ImageView image = listItem.findViewById(R.id.imageView_image);
        image.setImageResource(currentItem.getImageDrawable());

        TextView name = listItem.findViewById(R.id.textView_name);
        name.setText(currentItem.getName());

        TextView address = listItem.findViewById(R.id.textView_address);
        address.setText(currentItem.getAddress());

        TextView details = listItem.findViewById(R.id.textView_details);
        details.setText(currentItem.getDetails());

        return listItem;
    }
}

