package com.jobfinder.jobfinderandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

public class CommonFunctions {

    @SuppressLint("NonConstantResourceId")
    public static boolean menu(Context con, MenuItem item, String src) {
        Intent i = null;
//        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_home:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, ApplicantDashboard.class);
                break;
            case R.id.action_find_job:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, ApplicantJobList.class);
                break;
            case R.id.action_profile:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, ApplicantProfile.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, ApplicantSettings.class);
                break;
            case R.id.action_logout:
                if(!src.equals(item.getTitle()))
                    Toast.makeText(con, "Function To Follow: Logout", Toast.LENGTH_SHORT).show();
//                    i = new Intent(con, Location.class);
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

}
