package com.nidaappdev.brainer.RecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.nidaappdev.brainer.R;
import com.nidaappdev.brainer.customObjects.Achievement;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder> {

    private final Context context;
    private ArrayList<Achievement> achievements;

    public AchievementsAdapter(Context context, ArrayList<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
    }

    public void updateAchievementsList(ArrayList<Achievement> achievements) {
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public AchievementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.achievement_card, parent, false);
        return new AchievementsViewHolder(view);
    }

    public class AchievementsViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView parentCard;
        ConstraintLayout lockView;
        RelativeLayout collapsedContainer;
        CircleImageView achievementIcon;
        TextView achievementNameTV, achievementDescriptionTV, lockExplanationTV;

        public AchievementsViewHolder(@NonNull View itemView) {
            super(itemView);

            initViews(itemView);
        }

        private void initViews(View itemView) {
            parentCard = itemView.findViewById(R.id.achievement_card_view);
            collapsedContainer = itemView.findViewById(R.id.collapsed_content);
            lockView = itemView.findViewById(R.id.lock_view);
            achievementIcon = itemView.findViewById(R.id.achievement_icon);
            achievementNameTV = itemView.findViewById(R.id.achievement_name);
            achievementDescriptionTV = itemView.findViewById(R.id.achievement_descriptionTV);
            lockExplanationTV = itemView.findViewById(R.id.lock_explanation);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementsViewHolder holder, int position) {
        if(achievements.get(position) == null) {
            return;
        }
        Achievement currentAchievement = achievements.get(position);
        String name = currentAchievement.getName(),
        description = currentAchievement.getDescription(),
        requirements = currentAchievement.getRequirements() + " to unlock this achievement";
        int iconRes = currentAchievement.getIconResId();
        boolean achieved = currentAchievement.isAchieved();

        holder.achievementNameTV.setText(name);
        holder.achievementDescriptionTV.setText(description);
        holder.achievementIcon.setImageResource(iconRes);
        holder.lockExplanationTV.setText(requirements);
        holder.lockView.setVisibility(achieved ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }
}
