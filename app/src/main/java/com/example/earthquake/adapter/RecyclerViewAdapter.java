package com.example.earthquake.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.earthquake.R;
import com.example.earthquake.model.EarthQuake;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String LOCATION_SEPARATOR = " of ";
    private Context context;
    private List<EarthQuake> quakeArrayList;

    public RecyclerViewAdapter(Context context, List<EarthQuake> quakeArrayList) {
        this.context = context;
        this.quakeArrayList = quakeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_row, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        EarthQuake earthQuake = quakeArrayList.get(position);

        String formattedMagnitude = formatMagnitude(earthQuake.getMagnitude());
        viewHolder.mag.setText(formattedMagnitude);
        GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.mag.getBackground();
        int magnitudeColor = getMagnitudeColor(earthQuake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        Date dateObject = new Date(earthQuake.getTime());
        String formattedDate = formatDate(dateObject);
        viewHolder.date.setText(formattedDate);

        String formattedTime = formatTime(dateObject);
        viewHolder.time.setText(formattedTime);

        String place = earthQuake.getPlace();
        String location;
        String locationOffset;

        // Check whether the place string contains the " of " text
        if (place.contains(LOCATION_SEPARATOR)) {
            // Split the string into different parts (as an array of Strings)
            String[] parts = place.split(LOCATION_SEPARATOR);
            // Location offset should be "5km N " + " of " --> "5km N of"
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            // Primary location should be "Cairo, Egypt"
            location = parts[1];
        } else {
            locationOffset = context.getString(R.string.near_the);
            location = place;
        }
        viewHolder.location.setText(location);
        viewHolder.locationOffset.setText(locationOffset);
    }

    @Override
    public int getItemCount() {
        return quakeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mag;
        private TextView time;
        private TextView date;
        private TextView locationOffset;
        private TextView location;
        private LinearLayout item;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            mag = view.findViewById(R.id.magnitude);
            time = view.findViewById(R.id.time);
            date = view.findViewById(R.id.date);
            locationOffset = view.findViewById(R.id.location_offset);
            location = view.findViewById(R.id.primary_location);

            item = view.findViewById(R.id.item_id);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = ((Activity)context).getIntent();

            int position = getAdapterPosition();
            EarthQuake earthQuake = quakeArrayList.get(position);
            double lat = earthQuake.getLat();
            double lon = earthQuake.getLon();

            intent.putExtra("lat", lat);
            intent.putExtra("lon", lon);

            //show on map;
            ((Activity) context).setResult(((Activity) context).RESULT_OK, intent);
            ((Activity) context).finish();
        }
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }
}
