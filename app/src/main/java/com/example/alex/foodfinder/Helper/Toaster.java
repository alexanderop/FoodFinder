package com.example.alex.foodfinder.Helper;

import android.view.Gravity;
import android.widget.Toast;
import android.content.Context;

import com.google.firebase.database.ValueEventListener;


public class Toaster {

    public static void makeShortToast(Context context, String msg){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public static void makeLongToast(Context context, String msg){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


}