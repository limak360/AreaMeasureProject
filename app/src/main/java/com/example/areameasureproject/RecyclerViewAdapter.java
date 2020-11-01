package com.example.areameasureproject;

import android.annotation.SuppressLint;
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

    private final List<Measurement> mMeasurements;
    private final OnMeasurementListener mOnMeasurementListener;
    private final OnLongMeasurementListener onLongMeasurementListener;

    public RecyclerViewAdapter(List<Measurement> mMeasurements, OnMeasurementListener mOnMeasurementListener, OnLongMeasurementListener onLongMeasurementListener) {
        this.mMeasurements = mMeasurements;
        this.mOnMeasurementListener = mOnMeasurementListener;
        this.onLongMeasurementListener = onLongMeasurementListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view, mOnMeasurementListener, onLongMeasurementListener);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(mMeasurements.get(position).getName());
        holder.date.setText(mMeasurements.get(position).getDate());
        holder.area.setText(String.format("%.3f", mMeasurements.get(position).getArea()) + " m²");
    }

    @Override
    public int getItemCount() {
        return mMeasurements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView date;
        TextView area;
        TextView name;
        ConstraintLayout parentLayout;
        OnMeasurementListener onMeasurementListener;
        OnLongMeasurementListener onLongMeasurementListener;

        public ViewHolder(@NonNull View itemView, OnMeasurementListener onMeasurementListener, OnLongMeasurementListener onLongMeasurementListener) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            area = itemView.findViewById(R.id.area);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.recycler_view);
            this.onMeasurementListener = onMeasurementListener;
            this.onLongMeasurementListener = onLongMeasurementListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onMeasurementListener.onMeasurementClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onLongMeasurementListener.onLongMeasurementListener(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            return true;
        }
    }

    public interface OnMeasurementListener {
        void onMeasurementClick(int position);
    }

    public interface OnLongMeasurementListener {
        void onLongMeasurementListener(int position);
    }
}
