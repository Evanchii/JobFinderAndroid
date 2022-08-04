package com.jobfinder.jobfinderandroid;

import android.app.job.JobInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApplicantDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private TextView welcomeUser;
    private String spec, user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setSubtitle("Applicant JobFinder");
        setContentView(R.layout.activity_applicant_dashboard);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view), "applicant");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference().child("user").child("applicant").child(user);
        welcomeUser = (TextView) findViewById(R.id.textView);

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isComplete() && task.isSuccessful()) {
                    DataSnapshot data = task.getResult();
                    welcomeUser.setText("Welcome "+data.child("fname").getValue()+" "+data.child("lname").getValue());

                    spec = data.child("specialization").getValue().toString();

                    getData();
                }
            }
        });


    }

    public void _viewAll(View view){
        startActivity(new Intent(ApplicantDashboard.this, ApplicantJobList.class));
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (CommonFunctions.applicantMenu(this, item, "Home"))
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

    public void getData() {
        HashMap<String, Integer> jobCountUnsort = new HashMap<>();
        ArrayList<String> recKeyJobs = new ArrayList<String>();
        DatabaseReference dbJobs = FirebaseDatabase.getInstance().getReference("jobs");
        dbJobs.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                for(DataSnapshot jobs : data.getChildren()) {
                    String tmpCat = jobs.child("category").getValue().toString();
                    if(tmpCat.equals(spec)) {
                        recKeyJobs.add(jobs.getKey());
                    }
                    if(!jobCountUnsort.containsKey(tmpCat))
                        jobCountUnsort.put(tmpCat, 0);
                    jobCountUnsort.put(tmpCat, jobCountUnsort.get(tmpCat)+1);
                }

                Map<String, Integer> jobCountSorted = sortByComparator(jobCountUnsort, false);

                CardView[] topCategories = {findViewById(R.id.appDash_topCat1), findViewById(R.id.appDash_topCat2), findViewById(R.id.appDash_topCat3), findViewById(R.id.appDash_topCat4), findViewById(R.id.appDash_topCat5), findViewById(R.id.appDash_topCat6)};
                int x = 0;
                for (Map.Entry<String, Integer> entry : jobCountSorted.entrySet()) {
                    RelativeLayout childLayout = (RelativeLayout) topCategories[x].getChildAt(0);
                    TextView jobTitle = (TextView) childLayout.getChildAt(0),
                            jobCount = (TextView) childLayout.getChildAt(2);

                    jobTitle.setText(entry.getKey());
                    jobCount.setText(String.valueOf(entry.getValue()));
                    topCategories[x++].setOnClickListener(view -> {
                        startActivity(new Intent(view.getContext(), ApplicantJobList.class).putExtra("search", entry.getKey()));
                    });

                    if(x > 5) break;
                }

                int limit = recKeyJobs.size();
                LinearLayout llRecJob = findViewById(R.id.appDash_layoutRecJob);
                if(limit > 5) {
                    for (String jobKey : recKeyJobs.subList(limit-5, limit-1)) {
                        DataSnapshot jobInfo = data.child(jobKey);

                        CardView parentCard = (CardView) LayoutInflater.from(this)
                                .inflate(R.layout.item_recommended_job, null);
                        LinearLayout childLL = (LinearLayout) parentCard.getChildAt(0);
                        TextView jobName = (TextView) childLL.getChildAt(0),
                                comp = (TextView) childLL.getChildAt(1),
                                loc = (TextView) childLL.getChildAt(2);

                        jobName.setText(jobInfo.child("jobTitle").getValue().toString());
                        comp.setText(jobInfo.child("companyName").getValue().toString());
                        loc.setText(jobInfo.child("jobLocation").getValue().toString());

                        parentCard.setOnClickListener(view ->
                                startActivity(new Intent(view.getContext(), JobInfo.class).putExtra("jobKey", jobKey)));

                        llRecJob.addView(parentCard);
                    }
                } else if( limit > 0) {
                    for (String jobKey : recKeyJobs) {
                        DataSnapshot jobInfo = data.child(jobKey);

                        CardView parentCard = (CardView) LayoutInflater.from(this)
                                .inflate(R.layout.item_recommended_job, null);
                        LinearLayout childLL = (LinearLayout) parentCard.getChildAt(0);
                        TextView jobName = (TextView) childLL.getChildAt(0),
                                comp = (TextView) childLL.getChildAt(1),
                                loc = (TextView) childLL.getChildAt(2);

                        jobName.setText(jobInfo.child("jobTitle").getValue().toString());
                        comp.setText(jobInfo.child("companyName").getValue().toString());
                        loc.setText(jobInfo.child("jobLocation").getValue().toString());

                        parentCard.setOnClickListener(view ->
                                startActivity(new Intent(view.getContext(), JobView.class).putExtra("jobKey", jobKey)));

                        llRecJob.addView(parentCard);
                    }
                } else {
                    CardView parentCard = (CardView) LayoutInflater.from(this)
                            .inflate(R.layout.item_recommended_job, null);
                    LinearLayout childLL = (LinearLayout) parentCard.getChildAt(0);
                    TextView jobName = (TextView) childLL.getChildAt(0),
                            comp = (TextView) childLL.getChildAt(1),
                            loc = (TextView) childLL.getChildAt(2);

                    jobName.setText("No jobs found!");
                    comp.setText("");
                    loc.setText("");

                    llRecJob.addView(parentCard);
                }
            }
        });
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}