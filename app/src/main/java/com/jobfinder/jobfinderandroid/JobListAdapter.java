package com.jobfinder.jobfinderandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.ViewHolder> {

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

    public JobListAdapter(Context context, HashMap<String, HashMap<String, String>> mData) {
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

        URL url = null;
        try {
            url = new URL("http://192.168.1.4/uploads/jobs/"+key+".png");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            viewHolder.logo.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
            viewHolder.logo.setImageResource(R.drawable.ic_briefcase);
        }

        viewHolder.cv.setOnClickListener(view -> {
            con.startActivity(new Intent(con, JobView.class).putExtra("jobKey", key));
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}