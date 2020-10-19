package com.example.areameasureproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.entity.Measurement;

import java.util.List;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.date.setText(measurementList.get(position).getDate().toString());
        holder.area.setText(String.valueOf(measurementList.get(position).getArea()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + measurementList.get(position).toString());
                Toast.makeText(mContext, measurementList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return measurementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView area;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            area = itemView.findViewById(R.id.area);
            parentLayout = itemView.findViewById(R.id.recycler_view);
        }
    }
}
