package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ApplicantList extends AppCompatActivity {

    private String jobKey, mode;
    private DatabaseReference dbJobs;
    private HashMap<String, HashMap<String, String>> applicants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Employer JobFinder");
        setContentView(R.layout.activity_applicant_list);

        jobKey = getIntent().getStringExtra("jobKey");
        mode = getIntent().getStringExtra("mode");

        switch (mode) {
            case "applicantList":
                getSupportActionBar().setTitle("Applicant List");
                break;
            case "SOI":
                getSupportActionBar().setTitle("Scheduled for Interview");
                break;
        }

        dbJobs = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey+"/applicants");

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
        applicants = new HashMap<>();
        dbJobs.get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.isComplete()) {
                for(DataSnapshot data : task.getResult().getChildren()) {
                    if((data.hasChild("interviewData") && mode.equals("applicantList")) ||
                            (!data.hasChild("interviewData") && mode.equals("SOI"))) continue;
                    HashMap<String, String> tmp = new HashMap<>();
                    tmp.put("name", data.child("applicantName").getValue().toString());
                    tmp.put("uid", data.getKey());
                    applicants.put(data.getKey(), tmp);
                }
                inflateData();
            }
        });
    }

    public void inflateData() {
        TextView empty = findViewById(R.id.txtNoData);
        RecyclerView rv = findViewById(R.id.appList_recView);
        if(!applicants.isEmpty()) {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            AdapterApplicantList adapter = new AdapterApplicantList(this, applicants, jobKey, mode);
            rv.setAdapter(adapter);
        } else {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
    }
}