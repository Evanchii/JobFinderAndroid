package com.jobfinder.jobfinderandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class JobView extends AppCompatActivity {

    private String jobKey;
    private DatabaseReference dbRef;

    private TextView jobName, compName, loc, salary, nature, desc, skills, exp;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Information");
        setContentView(R.layout.activity_job_view);

        jobKey = getIntent().getStringExtra("jobKey");

        jobName = findViewById(R.id.jobView_txtJobName);
        compName = findViewById(R.id.jobView_companyName);
        loc = findViewById(R.id.jobView_txtLoc);
        salary = findViewById(R.id.jobView_txtSalary);
        nature = findViewById(R.id.jobView_txtNature);
        skills = findViewById(R.id.jobView_txtSkills);
        desc = findViewById(R.id.jobView_txtDesc);
        exp = findViewById(R.id.jobView_txtExp);
        iv = findViewById(R.id.jobView_imgLogo);

//        fetch data
        dbRef = FirebaseDatabase.getInstance().getReference("jobs/"+jobKey);
        dbRef.get().addOnCompleteListener(task -> {
            if(task.isComplete() && task.isSuccessful()) {
                DataSnapshot data = task.getResult();
                jobName.setText(data.child("jobTitle").getValue().toString());
                compName.setText(data.child("companyName").getValue().toString());
                loc.setText(data.child("jobLocation").getValue().toString());
                salary.setText(data.child("salary").getValue().toString());
                nature.setText(data.child("jobNature").getValue().toString());
                skills.setText("-Skills: " + data.child("reqSkill").getValue().toString());
                desc.setText(data.child("description").getValue().toString());
                exp.setText("-Experience: " + data.child("reqExperience").getValue().toString());

                new DownloadImageTask(iv)
                        .execute("https://www.jobfinder.gq/uploads/jobs/"+jobKey+".png");

            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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

    public void btnApply(View view) {
        startActivity(new Intent(JobView.this, JobApply.class).putExtra("jobKey", jobKey));
    }
}