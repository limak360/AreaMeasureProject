package com.example.areameasureproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.entity.LatLngAdapter;
import com.example.areameasureproject.entity.Measurement;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<Measurement> measurementList;
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, List<Measurement> measurementList) {
        this.mContext = mContext;
        this.measurementList = measurementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.date.setText(measurementList.get(position).getDate());
        holder.area.setText(String.format("%.3f", measurementList.get(position).getArea()) + " m2");

        List<String> coordinates = new ArrayList<>();
        for (LatLngAdapter latLngAdapter : measurementList.get(position).getCoordinates()) {
            String coordinatesFormatter = "{" + latLngAdapter.getLatitude() + "; " + latLngAdapter.getLongitude() + "}";
            coordinates.add(coordinatesFormatter);
        }

        String formattedList = coordinates.toString().substring(1, coordinates.toString().length() - 1);
        holder.coordinates.setText(formattedList);
        holder.itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            menu.add(0, R.id.loadMeasurement, 0, R.string.load_measurement)
                    .setOnMenuItemClickListener(item -> {
                        Intent intent = new Intent(v.getContext(), MapActivity.class);
                        intent.putExtras(prepareIntentExtras(holder));
                        v.getContext().startActivity(intent); //TODO Activity Result Launcher ??? startActivityForResult
                        return true; //TODO przebudowac to onclick otwiera nowa aktywnosc tam 2 opcje gpx i zaladowanie widoku
                    });
            menu.add(0, R.id.exportMeasurement, 1, R.string.export_measurement_to)
                    .setOnMenuItemClickListener(item -> {
                        Toast.makeText(mContext, "Exporting ...", Toast.LENGTH_SHORT).show();
                        GPXGenerator.generateGFX(new File(""), "nazwa pomiaru", createLocationObject(holder));//TODO https://developer.android.com/training/data-storage#filesExternal
                        return true;
                    });
        });
    }

    private List<Location> createLocationObject(@NonNull ViewHolder holder) {
        List<Location> locations = new ArrayList<>();
        for (LatLngAdapter latLngAdapter : getLatLngAdapters(holder)) {
            Location location = new Location("");
            location.setLatitude(latLngAdapter.getLatitude());
            location.setLongitude(latLngAdapter.getLongitude());
            locations.add(location);
        }

        return locations;
    }

    private Bundle prepareIntentExtras(@NonNull ViewHolder holder) {
        Bundle bundle = new Bundle();
        List<LatLngAdapter> latLngAdapterList = getLatLngAdapters(holder);
        bundle.putSerializable("MeasurementCoordinates", (Serializable) latLngAdapterList);
        return bundle;
    }

    private List<LatLngAdapter> getLatLngAdapters(@NonNull ViewHolder holder) {
        Measurement measurement = measurementList.get(holder.getAdapterPosition());
        Collection<LatLngAdapter> latLngAdapters = measurement.getCoordinates();
        return new ArrayList<>(latLngAdapters);
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView area;
        TextView coordinates;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            area = itemView.findViewById(R.id.area);
            coordinates = itemView.findViewById(R.id.coordinates);
            parentLayout = itemView.findViewById(R.id.recycler_view);
        }
    }
}
