package com.example.areameasureproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.areameasureproject.entity.Measurement;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private List<Measurement> mMeasurements;
    private OnMeasurementListener mOnMeasurementListener;

    public RecyclerViewAdapter(Context mContext, List<Measurement> mMeasurements, OnMeasurementListener mOnMeasurementListener) {
        this.mContext = mContext;
        this.mMeasurements = mMeasurements;
        this.mOnMeasurementListener = mOnMeasurementListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view, mOnMeasurementListener);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(mMeasurements.get(position).getName());
        holder.date.setText(mMeasurements.get(position).getDate());
        holder.area.setText(String.format("%.3f", mMeasurements.get(position).getArea()) + " m2");
    }

    @Override
    public int getItemCount() {
        return mMeasurements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;
        TextView area;
        TextView name;
        ConstraintLayout parentLayout;
        OnMeasurementListener onMeasurementListener;

        public ViewHolder(@NonNull View itemView, OnMeasurementListener onMeasurementListener) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            area = itemView.findViewById(R.id.area);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.recycler_view);
            this.onMeasurementListener = onMeasurementListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMeasurementListener.onMeasurementClick(getAdapterPosition());
        }
    }

    public interface OnMeasurementListener {
        void onMeasurementClick(int position);
    }
}
