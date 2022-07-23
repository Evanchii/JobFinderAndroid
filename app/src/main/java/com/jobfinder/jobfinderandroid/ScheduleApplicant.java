package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ScheduleApplicant extends AppCompatActivity {


    private FirebaseDatabase dbref;

    private String jobKey,uid;

    private EditText time,date,link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Schedule Applicant");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_schedule_applicant);

        jobKey = getIntent().getStringExtra("jobKey");
        uid = getIntent().getStringExtra("uid");

        dbref = FirebaseDatabase.getInstance();
        time =(EditText) findViewById(R.id.schedApp_inputTime);
        date = (EditText) findViewById(R.id.schedApp_inputDate);
        link = (EditText) findViewById(R.id.schedApp_inputLink);


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

    public void _btnSchedule(View view){
        dbref.getReference("jobs/"+jobKey+"/applicants/"+uid).child("interviewData").child("time").setValue(time.getText().toString());
        dbref.getReference("jobs/"+jobKey+"/applicants/"+uid).child("interviewData").child("date").setValue(date.getText().toString());
        dbref.getReference("jobs/"+jobKey+"/applicants/"+uid).child("interviewData").child("link").setValue(link.getText().toString());

        Toast.makeText(ScheduleApplicant.this,"Schedule Set",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ScheduleApplicant.this,EmployerDashboard.class));
    }
}