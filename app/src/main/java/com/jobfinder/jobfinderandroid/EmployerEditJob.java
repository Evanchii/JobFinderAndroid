package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmployerEditJob extends AppCompatActivity {

    private String mode, jobKey;
    private DatabaseReference dbJob;
    private EditText title, loc, vacancy, salary, desc, skill, exp;
    private Spinner spec, nature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("Employer JobFinder");
        setContentView(R.layout.activity_employer_edit_job);

        jobKey = getIntent().getStringExtra("jobKey");
        if(jobKey != null) {
            getSupportActionBar().setTitle("Edit Job");
            mode = "update";
        } else {
            getSupportActionBar().setTitle("New Job");
            mode = "create";
        }

        dbJob = FirebaseDatabase.getInstance().getReference("jobs");
        title = findViewById(R.id.editJob_inputTitle);
        loc = findViewById(R.id.editJob_inputLoc);
        vacancy = findViewById(R.id.editJob_inputVacancy);
        salary = findViewById(R.id.editJob_inputSalary);
        desc = findViewById(R.id.editJob_inputDesc);
        skill = findViewById(R.id.editJob_inputSkills);
        exp = findViewById(R.id.editJob_inputExp);

        spec = findViewById(R.id.editJob_spnSpec);
        nature = findViewById(R.id.editJob_spnNature);

        if(mode.equals("update")) {
            dbJob = dbJob.child(jobKey);
            dbJob.get().addOnCompleteListener(task -> {
                if(task.isComplete() && task.isSuccessful()) {
                    DataSnapshot data = task.getResult();
                    title.setText(data.child("jobTitle").getValue().toString());
                    loc.setText(data.child("jobLocation").getValue().toString());
                    vacancy.setText(data.child("vacancy").getValue().toString());
                    salary.setText(data.child("salary").getValue().toString());
                    desc.setText(data.child("description").getValue().toString());
                    skill.setText(data.child("reqSkill").getValue().toString());
                    exp.setText(data.child("reqExperience").getValue().toString());

                    int posSpec = 0, posNature = 0;

                    switch(data.child("jobNature").getValue().toString()) {
                        case "Full Time":
                            posNature = 0;
                            break;
                        case "Part Time Job":
                            posNature = 1;
                            break;
                        case "Internship":
                            posNature = 2;
                            break;
                        case "Temporary Job":
                            posNature = 3;
                            break;
                        case "Summer Job":
                            posNature = 4;
                            break;
                    }

                    switch(data.child("category").getValue().toString()) {
                        case "Accounting/Finance":
                            posSpec = 0;
                            break;
                        case "Admin/Human Resource":
                            posSpec = 1;
                            break;
                        case "Sales/Marketing":
                            posSpec = 2;
                            break;
                        case "Arts/Media/Communication":
                            posSpec = 3;
                            break;
                        case "Hotel/Restaurant Services":
                            posSpec = 4;
                            break;
                        case "Education/Training":
                            posSpec = 5;
                            break;
                        case "Computer/Information Technology":
                            posSpec = 6;
                            break;
                        case "Engineering":
                            posSpec = 7;
                            break;
                        case "Manufacturing":
                            posSpec = 8;
                            break;
                        case "Building/Construction":
                            posSpec = 9;
                            break;
                        case "Science":
                            posSpec = 10;
                            break;
                        case "Healthcare":
                            posSpec = 11;
                            break;
                        case "Others":
                            posSpec = 12;
                            break;
                    }

                    spec.setSelection(posSpec);
                    nature.setSelection(posNature);
                }
            });
        }
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
}
