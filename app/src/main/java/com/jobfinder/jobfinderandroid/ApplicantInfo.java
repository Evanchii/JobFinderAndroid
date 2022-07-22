package com.jobfinder.jobfinderandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ApplicantInfo extends AppCompatActivity {

    private String mode, jobKey;
    private TextView cardName, name, contact, gender, birthday, address, specialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Applicant Information");
        getSupportActionBar().setSubtitle("Employer JobFinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_applicant_info);

        mode = getIntent().getStringExtra("mode");
        jobKey = getIntent().getStringExtra("jobKey");

        if(mode.equals("applicantList")) {
            ((LinearLayout) findViewById(R.id.appInfo_actionAppList)).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) findViewById(R.id.appInfo_actionSOI)).setVisibility(View.VISIBLE);
        }
    }
}