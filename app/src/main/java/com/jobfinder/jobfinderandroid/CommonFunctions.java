package com.jobfinder.jobfinderandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

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
                    i.putExtra("mode", "post_job");
                break;
            case R.id.action_applicants:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerPostedJobs.class);
                    i.putExtra("mode", "applicants");
                break;
            case R.id.action_soi:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerPostedJobs.class);
                    i.putExtra("mode", "soi");
                break;
            case R.id.action_profile:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, EmployerProfile.class);
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

    public void createNotification(String uid, String userType, String title, String message, String messageType) {
        /*
         * Docs: $messageType
         * warning - !
         * danger  - <!>
         * success - /
         * primary - (i)
         */

        String ts = (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date())).toString();

        DatabaseReference dbNotifPool = FirebaseDatabase.getInstance().getReference("notification/"+ts),
            userNotif = FirebaseDatabase.getInstance().getReference("user/"+userType+"/"+uid+"/notifications");

        dbNotifPool.child("title").setValue(title);
        dbNotifPool.child("message").setValue(message);
        dbNotifPool.child("messageType").setValue(messageType);

        userNotif.child(ts).setValue("Unread");
    }

    public void createLog(Context con, String eventName, String eventDescription, String eventType, String name, String uid) {
        String ts = (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date())).toString();

        Context context = con.getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        DatabaseReference dbLogs = FirebaseDatabase.getInstance().getReference("userlogs/"+ts);
        dbLogs.child("eventDescription").setValue(eventDescription);
        dbLogs.child("eventName").setValue(eventName);
        dbLogs.child("eventType").setValue(eventType);
        dbLogs.child("ip").setValue(ip);
        dbLogs.child("uid").setValue(uid);
        dbLogs.child("name").setValue(name);
    }

}
