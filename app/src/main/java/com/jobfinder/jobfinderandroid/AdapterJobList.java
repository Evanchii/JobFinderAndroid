package com.jobfinder.jobfinderandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.HashMap;

public class AdapterJobList extends RecyclerView.Adapter<AdapterJobList.ViewHolder> {

    private HashMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private Context con;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView companyName, jobTitle;
        private ImageView logo;
        private CardView cv;

        public ViewHolder(View view) {
            super(view);
            companyName = view.findViewById(R.id.itemJob_txtCompany);
            jobTitle = view.findViewById(R.id.itemJob_txtTitle);
            logo = view.findViewById(R.id.itemJob_jobIcon);
            cv = view.findViewById(R.id.itemJob_parentCard);
        }
    }

    public AdapterJobList(Context context, HashMap<String, HashMap<String, String>> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.con = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_job_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String key = (String) (mData.keySet().toArray())[position];
        Log.d("JLAdap(48)", key);
        Log.d("JLAdap(49)", (String) (mData.get(key).keySet().toArray())[0]);

        viewHolder.jobTitle.setText(mData.get(key).get("jobTitle"));
        viewHolder.companyName.setText(mData.get(key).get("companyName"));
        viewHolder.logo.setImageResource(R.drawable.ic_briefcase);

        new DownloadImageTask(viewHolder.logo)
                .execute("https://www.jobfinder.gq/uploads/jobs/"+key+".png");

        View.OnClickListener click = null;
        switch(mData.get(key).get("mode")) {
            case "appJobView":
                click = view -> {
                    con.startActivity(new Intent(con, JobView.class).putExtra("jobKey", key));
                };
                break;
            case "jobList":
                click = view -> {
                    con.startActivity(new Intent(con, EmployerEditJob.class).putExtra("jobKey", key));
                };
                break;
            case "applicantList":
                click = view -> {
                    con.startActivity(new Intent(con, ApplicantList.class).putExtra("jobKey", key).putExtra("mode", "applicantList"));
                };
                break;
            case "SOI":
                click = view -> {
                    con.startActivity(new Intent(con, ApplicantList.class).putExtra("jobKey", key).putExtra("mode", "SOI"));
                };
                break;
        }
        viewHolder.cv.setOnClickListener(click);
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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}