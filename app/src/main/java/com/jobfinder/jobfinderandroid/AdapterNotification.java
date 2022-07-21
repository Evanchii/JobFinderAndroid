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
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder> {

    private HashMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private Context con;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, message, time;
        private ImageView icon;
        private CardView cv;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.itemNotif_title);
            message = view.findViewById(R.id.itemNotif_message);
            time = view.findViewById(R.id.itemNotif_date);
            icon = view.findViewById(R.id.itemNotif_icon);
        }
    }

    public AdapterNotification(Context context, HashMap<String, HashMap<String, String>> mData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.con = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_notification, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String key = (String) (mData.keySet().toArray())[position];
        Log.d("ANotif(48)", key);
        Log.d("ANotif(49)", (String) (mData.get(key).keySet().toArray())[0]);

        viewHolder.title.setText(mData.get(key).get("title"));
        viewHolder.message.setText(mData.get(key).get("message"));
        viewHolder.time.setText(getTiming(key));

        switch(mData.get(key).get("type")) {
            case "warning":
                viewHolder.icon.setImageResource(R.drawable.ic_warning);
                break;
            case "danger":
                viewHolder.icon.setImageResource(R.drawable.ic_danger);
                break;
            case "success":
                viewHolder.icon.setImageResource(R.drawable.ic_success);
                break;
            default:
                viewHolder.icon.setImageResource(R.drawable.ic_information);
        }
    }

    private String getTiming(String ts) {
        Long ts1 = Long.valueOf(ts);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Long ts2 = timestamp.getTime();
        Long timeDiff = (ts2-ts1)/1000;
        HashMap<Integer, String> timeDuration = new HashMap<>();
        timeDuration.put(31536000, "year");
        timeDuration.put(2592000, "month");
        timeDuration.put(604800, "week");
        timeDuration.put(86400, "day");
        timeDuration.put(3600, "hour");
        timeDuration.put(60, "minute");
        timeDuration.put(1, "second");

        for (Map.Entry<Integer, String> entry : timeDuration.entrySet()) {
            Integer k = entry.getKey();
            String v = entry.getValue();
            if (timeDiff < k) continue;
            else {
                int nou = (int) Math.floor(timeDiff / k);
                return nou + " " + v + ((nou > 1) ? "s" : "");
            }

        }
        return "";
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}