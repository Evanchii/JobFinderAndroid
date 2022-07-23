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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Applicant Information");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_applicant_info);

        dbRef =FirebaseDatabase.getInstance();
        dbJob = FirebaseDatabase.getInstance().getReference();

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

        dbRef.getReference().child("user").child("applicant").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardName.setText(snapshot.child("fname").getValue().toString()+" "+ snapshot.child("lname").getValue().toString());
                name.setText(snapshot.child("fname").getValue().toString()+" "+ snapshot.child("lname").getValue().toString());
                contact.setText(snapshot.child("phone").getValue().toString());
                gender.setText(snapshot.child("gender").getValue().toString());
                birthday.setText(snapshot.child("birthday").getValue().toString());
                address.setText(snapshot.child("address").getValue().toString());
                specialization.setText(snapshot.child("specialization").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

    public void _btnReject(View view){
        dbJob.setValue(null);
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