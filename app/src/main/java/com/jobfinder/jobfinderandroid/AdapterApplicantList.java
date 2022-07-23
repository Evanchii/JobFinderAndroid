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

public class AdapterApplicantList extends RecyclerView.Adapter<AdapterApplicantList.ViewHolder> {

    private HashMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private Context con;
    private String jobKey, mode;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView appName;
        private CardView cv;

        public ViewHolder(View view) {
            super(view);
            appName = view.findViewById(R.id.itemApp_name);
            cv = view.findViewById(R.id.itemApp_card);
        }
    }

    public AdapterApplicantList(Context context, HashMap<String, HashMap<String, String>> mData, String jobKey, String mode) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.con = context;
        this.jobKey = jobKey;
        this.mode = mode;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_applicant, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String key = (String) (mData.keySet().toArray())[position];
        Log.d("JLAdap(48)", key);
        Log.d("JLAdap(49)", (String) (mData.get(key).keySet().toArray())[0]);

        viewHolder.appName.setText(mData.get(key).get("name"));

        viewHolder.cv.setOnClickListener(view -> {
            con.startActivity(new Intent(con, ApplicantInfo.class)
                    .putExtra("jobKey", jobKey)
                    .putExtra("uid", mData.get(key).get("uid"))
                    .putExtra("mode", mode));
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}