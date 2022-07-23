package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicantInfo extends AppCompatActivity {

    private String mode, jobKey, resume, uid;
    private TextView cardName, name, contact, gender, birthday, address, specialization;
    private DatabaseReference dbJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Applicant Information");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_applicant_info);

        mode = getIntent().getStringExtra("mode");
        jobKey = getIntent().getStringExtra("jobKey");
        uid = getIntent().getStringExtra("uid");

        if(mode.equals("applicantList")) {
            ((LinearLayout) findViewById(R.id.appInfo_actionAppList)).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) findViewById(R.id.appInfo_actionSOI)).setVisibility(View.VISIBLE);
        }

        dbJob = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey+"/applicants/"+uid);
        dbJob.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                resume = data.child("resume").getValue().toString();
            }
        });
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

    public void viewResume(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jobfinder.cf/uploads/resume/"+resume));
        startActivity(browserIntent);
    }

    public void schedApp(View view) {
        startActivity(new Intent(ApplicantInfo.this, ScheduleApplicant.class)
                .putExtra("jobKey", jobKey).putExtra("uid", uid));
        finish();
    }
}