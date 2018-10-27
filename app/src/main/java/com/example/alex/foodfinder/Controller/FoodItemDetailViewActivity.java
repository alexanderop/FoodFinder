package com.example.alex.foodfinder.Controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.foodfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodItemDetailViewActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.nameTextView)
    TextView name;

    @BindView(R.id.detailsTextView)
    TextView detail;

    @BindView(R.id.addressTextView)
    TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_detail_view);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        String intentName = extra.getString("name");
        String intentDetail = extra.getString("detail");
        String intentAddress = extra.getString("address");
        int intentImage = extra.getInt("imageDrawable");

        name.setText(intentName);
        detail.setText(intentDetail);
        address.setText(intentAddress);
        imageView.setImageResource(intentImage);
    }
}
