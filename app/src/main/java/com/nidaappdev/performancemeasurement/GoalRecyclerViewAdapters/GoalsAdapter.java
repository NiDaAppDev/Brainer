package com.nidaappdev.performancemeasurement.GoalRecyclerViewAdapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.nidaappdev.performancemeasurement.customViews.CustomProgressBarButton.CustomProgressBarButton;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;

import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalsViewHolder> {

    private Context context;
    private ArrayList<Goal> goals;
    private OnItemClickListener listener;

    public GoalsAdapter(Context context, ArrayList<Goal> goals) {
        this.context = context;
        this.goals = goals;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class GoalsViewHolder extends RecyclerView.ViewHolder {

        private CustomProgressBarButton customProgressBar;

        public GoalsViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            customProgressBar = (CustomProgressBarButton) itemView;

            customProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public CustomProgressBarButton getProgressBar() {
            return customProgressBar;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater.from(context);
        CustomProgressBarButton customProgressBar = new CustomProgressBarButton(parent.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins((int) ((PublicMethods.getAppContext().getResources().getDisplayMetrics()).density * 15.0F), (int) ((PublicMethods.getAppContext().getResources().getDisplayMetrics()).density * 20.0F), (int) ((PublicMethods.getAppContext().getResources().getDisplayMetrics()).density * 15.0F), (int) ((PublicMethods.getAppContext().getResources().getDisplayMetrics()).density * 20.0F));
        customProgressBar.setLayoutParams(layoutParams);
        customProgressBar.enableDefaultGradient(true);
        customProgressBar.setClickable(true);
        customProgressBar.setFocusable(true);
        return new GoalsViewHolder(customProgressBar, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsViewHolder holder, int position) {
        String name = goals.get(position).getName();
        int progress = (goals.get(position).getTimeCounted() * 100) / goals.get(position).getTimeEstimated();
        holder.getProgressBar().setText(name);
        holder.getProgressBar().setProgress(progress);
        holder.getProgressBar().setRadius(300.0f);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public void updateGoalsList(ArrayList<Goal> goals){
        this.goals = goals;
        notifyDataSetChanged();
    }

    public void setGoals(ArrayList<Goal> goals) {
        this.goals = goals;
    }
}
