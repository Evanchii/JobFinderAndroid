package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Notification extends AppCompatActivity {

    HashMap<String, HashMap<String, String>> notifList;
    DatabaseReference dbNotifs, userNotifs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        setContentView(R.layout.activity_notification);

        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", this.MODE_PRIVATE);
        String userType = sharedpreferences.getString("userType", "");

        dbNotifs = FirebaseDatabase.getInstance().getReference("notification");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userNotifs = FirebaseDatabase.getInstance().getReference("user/"+userType+"/"+mAuth.getUid()+"/notifications");

        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        notifList = new HashMap<>();

        userNotifs.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isComplete()) {
                for(DataSnapshot data : task.getResult().getChildren()) {
                    notifList.put(data.getKey().toString(), new HashMap<>());
                }

                dbNotifs.get().addOnCompleteListener(task1 -> {
                    if(task1.isComplete() && task1.isComplete()) {
                        if(!notifList.isEmpty()) {
                            for (DataSnapshot data : task1.getResult().getChildren()) {
                                if (notifList.containsKey(data.getKey())) {
                                    notifList.get(data.getKey()).put("title", data.child("title").getValue().toString());
                                    notifList.get(data.getKey()).put("message", data.child("message").getValue().toString());
                                    notifList.get(data.getKey()).put("type", data.child("messageType").getValue().toString());
                                }
                            }
                        }
                        inflateData();
                    }
                });
            }
        });
    }

    public void inflateData() {
        TextView empty = findViewById(R.id.txtNoData);
        RecyclerView rv = findViewById(R.id.notif_rv);
        if(!notifList.isEmpty()) {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            AdapterNotification adapter = new AdapterNotification(this, notifList);
            rv.setAdapter(adapter);
        } else {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
    }
}