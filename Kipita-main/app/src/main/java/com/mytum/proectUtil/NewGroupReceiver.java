package com.mytum.proectUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NewGroupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("The group ", "GOT IN THE RECIVING");
//        Toast.makeText(this, "Working",Toast.LENGTH_SHORT).show();
    }

}