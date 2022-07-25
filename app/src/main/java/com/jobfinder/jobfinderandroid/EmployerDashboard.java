package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EmployerDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private FirebaseDatabase dbRef;

    private TextView welcome;
    TextView jobs, applicants, soi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Dashboard");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        setContentView(R.layout.activity_employer_dashboard);

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view), "employer");
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getCurrentUser().getUid();
        dbRef = FirebaseDatabase.getInstance();
        welcome = (TextView) findViewById(R.id.textView3);

        dbRef.getReference("user").child("employer").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcome.setText("Welcome "+snapshot.child("company").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getData();
    }

    public void getData() {
        jobs = findViewById(R.id.empDash_txtJobNum);
        applicants = findViewById(R.id.empDash_txtApplicantNum);
        soi = findViewById(R.id.empDash_txtSOINum);

        FirebaseDatabase.getInstance().getReference(
            "user/employer/"+FirebaseAuth.getInstance().getUid()+"/postedJobs"
        ).get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                jobs.setText(String.valueOf(data.getChildrenCount()));
                Map<String, String> jobKeys = new HashMap<>();
                for(DataSnapshot keys : data.getChildren())
                    jobKeys.put(keys.getKey(), keys.getValue().toString());

                FirebaseDatabase.getInstance().getReference(
                        "jobs"
                ).get().addOnCompleteListener(task1 -> {
                    if(task1.isComplete() && task1.isSuccessful()) {
                        int cSOI = 0, app = 0;
                        for(DataSnapshot data1 : task1.getResult().getChildren()) {
                            if(jobKeys.containsKey(data1.getKey())) {
                                for (DataSnapshot data2 : data1.child("applicants").getChildren()) {
                                    if(data2.hasChild("interviewData")) cSOI++;
                                    else app++;
                                }
                            }
                        }

                        applicants.setText(String.valueOf(app));
                        soi.setText(String.valueOf(cSOI));

                    }
                });
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (CommonFunctions.employerMenu(this, item, "Profile"))
            finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void postedJob(View view) {
        startActivity(new Intent(view.getContext(), EmployerPostedJobs.class).putExtra("mode", "jobList"));
        finish();
    }

    public void applicants(View view) {
        startActivity(new Intent(view.getContext(), EmployerPostedJobs.class).putExtra("mode", "applicantList"));
        finish();
    }

    public void SOI(View view) {
        startActivity(new Intent(view.getContext(), EmployerPostedJobs.class).putExtra("mode", "SOI"));
        finish();
    }
}