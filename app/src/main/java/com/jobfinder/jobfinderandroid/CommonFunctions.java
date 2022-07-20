package com.jobfinder.jobfinderandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class CommonFunctions {


    @SuppressLint("NonConstantResourceId")
    public static boolean applicantMenu(Context con, MenuItem item, String src) {
        FirebaseAuth mAuth;
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
                    Toast.makeText(con, "Logout", Toast.LENGTH_SHORT).show();

                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    i = new Intent(con, ApplicantSignIn.class);

                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    public static boolean employerMenu(Context con, MenuItem item, String src) {
        FirebaseAuth mAuth;
        Intent i = null;
//        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_home:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerDashboard.class);
                break;
            case R.id.action_post_job:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerPostedJobs.class);
                break;
            case R.id.action_applicants:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerDashboard.class);
                break;
            case R.id.action_soi:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerDashboard.class);
                break;
            case R.id.action_profile:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerDashboard.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerSettings.class);
                break;
            case R.id.action_logout:
                if(!src.equals(item.getTitle()))
                    Toast.makeText(con, "Logout", Toast.LENGTH_SHORT).show();

                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                i = new Intent(con, ApplicantSignIn.class);

                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

}
