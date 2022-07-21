package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class JobApply extends AppCompatActivity {

    private String jobKey;
    private DatabaseReference dbJob, dbUser, dbNotifs, dbLogs;
    private FirebaseAuth mAuth;

    private TextInputEditText fname, lname, contact, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Information");
        setContentView(R.layout.activity_job_apply);

        jobKey = getIntent().getStringExtra("jobKey");
        mAuth = FirebaseAuth.getInstance();

        dbJob = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey);
        dbUser = FirebaseDatabase.getInstance().getReference("user/applicant/"+mAuth.getUid());
        dbNotifs = FirebaseDatabase.getInstance().getReference("notification");
        dbLogs = FirebaseDatabase.getInstance().getReference("userlogs");

        fname = findViewById(R.id.jobApply_inputFName);
        lname = findViewById(R.id.jobApply_inputLName);
        contact = findViewById(R.id.jobApply_inputContact);
        email = findViewById(R.id.jobApply_inputEmail);

        dbUser.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();

                fname.setText(data.child("fname").getValue().toString());
                lname.setText(data.child("lname").getValue().toString());
                contact.setText(data.child("phone").getValue().toString());
                email.setText(data.child("email").getValue().toString());
            }
        });
    }

    public void resumeUpload(View view) {
        // Place file picker code here tnx.
    }

    public void uploadApplication(View view) {
        if(true/*Check if user has uploaded resume*/) {
            if(checkData(fname) && checkData(lname) &&
                    checkData(contact) && checkData(email)) {
                String ts = (new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new java.util.Date())).toString();
                String uid = mAuth.getUid();
                dbJob.child(uid+"/applicantID").setValue(uid);
                dbJob.child(uid+"/applicantName").setValue(fname.getText() + " " + lname.getText());
                dbJob.child(uid+"/appliedAt").setValue(ts);
                dbJob.child(uid+"/status").setValue("Processing");
                dbJob.child(uid+"/phone").setValue(contact.getText());
                dbJob.child(uid+"/email").setValue(email.getText());
                dbJob.child(uid+"/resume").setValue(""); //To update!

                dbUser.child("jobsApplied/"+ts).setValue(ts);
            } else {
                Toast.makeText(view.getContext(), "Please fill all required data!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(view.getContext(), "Please upload your resume", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkData(TextInputEditText view) {
        TextInputLayout parent = (TextInputLayout) view.getParent();

        if(view.getText().equals("")) {
            parent.setError("Please input data.");
            parent.setErrorEnabled(true);
            return false;
        }

        parent.setErrorEnabled(false);
        return true;
    }
}