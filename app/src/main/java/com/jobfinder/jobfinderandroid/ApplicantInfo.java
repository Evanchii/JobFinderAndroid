package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ApplicantInfo extends AppCompatActivity {

    private String mode, jobKey, resume, uid;
    private TextView cardName, name, contact, gender, birthday, address, specialization;
    private DatabaseReference dbJob;
    private FirebaseDatabase dbRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Applicant Information");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_applicant_info);

        dbRef =FirebaseDatabase.getInstance();
        dbJob = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey+"/applicants/"+uid);
        mAuth = FirebaseAuth.getInstance();

        mode = getIntent().getStringExtra("mode");
        jobKey = getIntent().getStringExtra("jobKey");
        uid = getIntent().getStringExtra("uid");

        cardName = (TextView)findViewById(R.id.appInfo_txtFullName);
        name = (TextView)findViewById(R.id.appInfo_txtName);
        contact = (TextView)findViewById(R.id.appInfo_txtContact);
        gender = (TextView)findViewById(R.id.appInfo_txtGender);
        birthday = (TextView)findViewById(R.id.appInfo_txtBDay);
        address = (TextView)findViewById(R.id.appInfo_txtAddress);
        specialization=(TextView)findViewById(R.id.appInfo_txtSpec);

        dbRef.getReference().child("jobs/"+jobKey+"/applicants/"+uid).get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                resume = task.getResult().child("resume").getValue().toString();
            }
        });
        dbRef.getReference().child("user").child("applicant").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isComplete() && task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    cardName.setText(snapshot.child("fname").getValue().toString()+" "+ snapshot.child("lname").getValue().toString());
                    name.setText(snapshot.child("fname").getValue().toString()+" "+ snapshot.child("lname").getValue().toString());
                    contact.setText(snapshot.child("phone").getValue().toString());
                    gender.setText(snapshot.child("gender").getValue().toString());
                    birthday.setText(snapshot.child("birthday").getValue().toString());
                    address.setText(snapshot.child("address").getValue().toString());
                    specialization.setText(snapshot.child("specialization").getValue().toString());
                }
            }
        });

        if(mode.equals("applicantList")) {
            ((LinearLayout) findViewById(R.id.appInfo_actionAppList)).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) findViewById(R.id.appInfo_actionSOI)).setVisibility(View.VISIBLE);
        }

    }

    public void _btnReject(View view){
        dbJob.setValue(null);

        new CommonFunctions().createNotification(uid, "applicant", "Application Rejected",
                "Your application for " + jobKey + " has been rejected!",
                "danger");

        new CommonFunctions().createLog(view.getContext(), "Applicant Rejected",
                mAuth.getUid() + " has rejected the application of "+uid, "Application Response",
                "", mAuth.getUid());

        startActivity(new Intent(ApplicantInfo.this, EmployerDashboard.class));
        finish();
    }

    public void _btnAccept(View view){
        dbJob.setValue(null);
        dbJob = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey);
        dbJob.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                dbJob.child("vacancy").setValue(Integer.parseInt(data.child("vacancy").getValue().toString()) - 1);
            }
        });

        new CommonFunctions().createNotification(uid, "applicant", "Application Accepted!",
                "Your application for " + jobKey + " has been accepted! \\nCongratulations!",
                "success");

        new CommonFunctions().createLog(view.getContext(), "Applicant Accepted",
                mAuth.getUid() + " has accepted the application of "+uid, "Application Response",
                "", mAuth.getUid());

        startActivity(new Intent(ApplicantInfo.this, EmployerDashboard.class));
        finish();
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jobfinder.gq/uploads/resume/"+resume));
        startActivity(browserIntent);
    }

    public void schedApp(View view) {
        startActivity(new Intent(ApplicantInfo.this, ScheduleApplicant.class)
                .putExtra("jobKey", jobKey).putExtra("uid", uid));
        finish();
    }
}