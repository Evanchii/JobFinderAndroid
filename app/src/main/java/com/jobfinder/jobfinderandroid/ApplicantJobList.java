package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ApplicantJobList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private HashMap<String, HashMap<String, String>> jobList;
    private DatabaseReference dbJobs;
    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Find A Job");
        getSupportActionBar().setSubtitle("Applicant JobFinder");
        setContentView(R.layout.activity_applicant_job_list);

        dbJobs = FirebaseDatabase.getInstance().getReference("jobs");

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view), "applicant");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        getData();

        SearchView searchView = findViewById(R.id.appJobList_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                Log.d("AppJobList(62)", search);
                getData();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.equals("")) {
                    search = s;
                    Log.d("AppJobList(62)", search);
                    getData();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notification) {
            startActivity(new Intent(this, Notification.class));
        }
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (CommonFunctions.applicantMenu(this, item, "Find a Job"))
            finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getData() {
        jobList = new HashMap<>();

        dbJobs.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isComplete()) {
                for(DataSnapshot data : task.getResult().getChildren()) {
                    if(!search.equals("")) {
                        if(data.child("companyName").getValue().toString().contains(search) ||
                                data.child("jobTitle").getValue().toString().contains(search)) {
                            HashMap<String, String> tmp = new HashMap<>();
                            tmp.put("companyName", data.child("companyName").getValue().toString());
                            tmp.put("jobTitle", data.child("jobTitle").getValue().toString());
                            tmp.put("mode", "appJobView");
                            jobList.put(data.getKey(), tmp);
                        }
                    } else {
                        HashMap<String, String> tmp = new HashMap<>();
                        tmp.put("companyName", data.child("companyName").getValue().toString());
                        tmp.put("jobTitle", data.child("jobTitle").getValue().toString());
                        tmp.put("mode", "appJobView");
                        jobList.put(data.getKey(), tmp);
                    }
                }

                inflateData();
            }
        });
    }

    public void inflateData() {
        TextView empty = findViewById(R.id.txtNoData);
        RecyclerView rv = findViewById(R.id.appJobList_rv);
        if(!jobList.isEmpty()) {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            AdapterJobList adapter = new AdapterJobList(this, jobList);
            rv.setAdapter(adapter);
        } else {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
    }
}