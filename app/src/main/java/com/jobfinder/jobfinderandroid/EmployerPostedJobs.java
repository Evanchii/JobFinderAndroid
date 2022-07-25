package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EmployerPostedJobs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseReference jobPool, userJobs;
    private FirebaseAuth mAuth;
    private HashMap<String, HashMap<String, String>> jobs;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setSubtitle("Employer JobFinder");
        setContentView(R.layout.activity_employer_posted_jobs);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view), "employer");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.empListJobs_fabAdd);

        mode = getIntent().getStringExtra("mode");
        switch(mode) {
            case "jobList":
                getSupportActionBar().setTitle("Posted Jobs");
                navigationView.getMenu().getItem(1).setChecked(true);
                fab.setVisibility(View.VISIBLE);
                break;
            case "applicantList":
                getSupportActionBar().setTitle("Applicant List");
                navigationView.getMenu().getItem(2).setChecked(true);
                fab.setVisibility(View.GONE);
                break;
            case "SOI":
                getSupportActionBar().setTitle("Scheduled for Interview");
                navigationView.getMenu().getItem(3).setChecked(true);
                fab.setVisibility(View.GONE);
                break;
        }

        jobPool = FirebaseDatabase.getInstance().getReference("jobs");
        mAuth = FirebaseAuth.getInstance();
        userJobs = FirebaseDatabase.getInstance().getReference("user/employer/"+mAuth.getUid()+"/postedJobs");

        getData();
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
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (CommonFunctions.employerMenu(this, item, mode))
            finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getData() {
        jobs = new HashMap<>();
        userJobs.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                for(DataSnapshot data : task.getResult().getChildren()) {
                    jobs.put(data.getValue().toString(), new HashMap<>());
                }

                jobPool.get().addOnCompleteListener(task1 -> {
                    if(task1.isComplete() && task1.isSuccessful()) {
                        if(!jobs.isEmpty()) {
                            for (DataSnapshot data : task1.getResult().getChildren()) {
                                if (jobs.containsKey(data.getKey())) {
                                    jobs.get(data.getKey()).put("jobTitle", data.child("jobTitle").getValue().toString());
                                    jobs.get(data.getKey()).put("companyName", data.child("companyName").getValue().toString());
                                    jobs.get(data.getKey()).put("mode", mode);
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
        RecyclerView rv = findViewById(R.id.empListJobs_recView);
        if(!jobs.isEmpty()) {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            AdapterJobList adapter = new AdapterJobList(this, jobs);
            rv.setAdapter(adapter);
        } else {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
    }

    public void fabNewJob(View view) {
        startActivity(new Intent(view.getContext(), EmployerEditJob.class));
    }
}