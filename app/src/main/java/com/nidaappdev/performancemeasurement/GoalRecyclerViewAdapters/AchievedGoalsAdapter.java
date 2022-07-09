package com.nidaappdev.performancemeasurement.GoalRecyclerViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adroitandroid.chipcloud.ChipCloud;
import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.nidaappdev.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.activities.MainActivity;
import com.nidaappdev.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.nidaappdev.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Objects;

public class AchievedGoalsAdapter extends RecyclerView.Adapter<AchievedGoalsAdapter.AchievedGoalsViewHolder> implements View.OnTouchListener {

    private Context context;
    private Cursor cursor;
    private ArrayList<Goal> achievedGoals;
    private MainActivity activity;
    private NestedRecyclerView recyclerView;
    private GoalDBHelper db;
    private int expandedItem = -1;
    private final boolean[] openedFromParent = new boolean[]{false, true};

    public AchievedGoalsAdapter(Context context, ArrayList<Goal> achievedGoals, Cursor cursor, NestedRecyclerView recyclerView, MainActivity activity) {
        this.context = context;
        this.achievedGoals = achievedGoals;
        this.cursor = cursor;
        this.recyclerView = recyclerView;
        this.activity = activity;

        db = new GoalDBHelper(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    public void updateGoalsList() {
        achievedGoals = db.getAchievedGoalsArrayList();
    }

    public class AchievedGoalsViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView parentCard;
        LinearLayout shrunkContainer, subGoalsTitleContainer;
        RelativeLayout expandedContainer, subGoalsRecyclerViewContainer;
        ImageButton btnExpandShrink, btnBackToParent;
        TextView shrunkTitle, expandedTitle, description, finishDate, goalsTagsLabel;
        ChipCloud goalsTagsGroup;
        ImageView medal_1, medal_2, medal_3, medal_4, medal_5, satisfactionEmoji;
        RecyclerView subGoalsRecyclerView;
        boolean shrunk = true;

        public AchievedGoalsViewHolder(@NonNull View itemView) {
            super(itemView);

            parentCard = itemView.findViewById(R.id.achievedCardView);
            shrunkContainer = itemView.findViewById(R.id.shrunk_achieved_goal_container);
            expandedContainer = itemView.findViewById(R.id.expanded_achieved_goal_container);
            btnExpandShrink = itemView.findViewById(R.id.achieved_goal_expand_shrink_btn);
            btnBackToParent = itemView.findViewById(R.id.achieved_goal_back_to_parent_btn);
            shrunkTitle = itemView.findViewById(R.id.shrunk_achieved_goal_title);
            expandedTitle = itemView.findViewById(R.id.expanded_achieved_goal_title);
            description = itemView.findViewById(R.id.expanded_achieved_goal_description);
            finishDate = itemView.findViewById(R.id.expanded_achieved_goal_finish_date);
            goalsTagsLabel = itemView.findViewById(R.id.expanded_achieved_goal_tags_label);
            goalsTagsGroup = itemView.findViewById(R.id.expanded_achieved_goal_tags_parent);
            medal_1 = itemView.findViewById(R.id.medal_1);
            medal_2 = itemView.findViewById(R.id.medal_2);
            medal_3 = itemView.findViewById(R.id.medal_3);
            medal_4 = itemView.findViewById(R.id.medal_4);
            medal_5 = itemView.findViewById(R.id.medal_5);
            satisfactionEmoji = itemView.findViewById(R.id.achieved_goal_satisfaction_smiley);
            subGoalsTitleContainer = itemView.findViewById(R.id.expanded_achieved_goal_sub_goals_title_container);
            subGoalsRecyclerViewContainer = itemView.findViewById(R.id.expanded_achieved_goal_sub_goals_container);
            subGoalsRecyclerView = itemView.findViewById(R.id.expanded_achieved_goal_sub_goals_recyclerview);

            /**
             * controls what happens when an achieved-goal item is clicked.
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand();
                }
            });

            /**
             * controls what happens when an achieved-goal items expand/shrink button is clicked.
             */
            btnExpandShrink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand();
                }
            });

            /**
             * controls what happens when an achieved-goal items back-to-parent button is clicked.
             */
            btnBackToParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToParent(achievedGoals.get(getAdapterPosition()));
                }
            });

        }

        /**
         * @return if the card is shrunk or not.
         */
        public boolean isShrunk() {
            return shrunk;
        }

        /**
         * handles the toggle expand/shrink functionality.
         */
        private void toggleExpand() {
            TransitionManager.beginDelayedTransition((ViewGroup) itemView.getRootView(), new AutoTransition());

            if (expandedContainer.getVisibility() == View.VISIBLE) {
                expandedItem = -1;
                shrink();
            } else {
                expand();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        TransitionManager.endTransitions((ViewGroup) itemView.getRootView());
                        notifyItemChanged(expandedItem);
                    }
                }
            }, 850);
        }

        /**
         * handles the toggle expand/shrink buttons icon functionality.
         */
        void toggleExpandShrinkIcon(boolean expand) {
            int res;
            if (expand) {
                res = R.drawable.avd_expand;
            } else {
                res = R.drawable.avd_shrink;
            }
            btnExpandShrink.setImageResource(res);

            Drawable icon = btnExpandShrink.getDrawable();

            if (icon instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) icon).start();
            }
        }

        /**
         * handles the expanding functionality.
         */
        public void expand() {
            if (expandedItem != -1) {
                notifyItemChanged(expandedItem);
            }
            expandedItem = getLayoutPosition();

            toggleExpandShrinkIcon(true);
            if (!openedFromParent[1]) {
                openedFromParent[1] = true;
            } else {
                openedFromParent[0] = false;
            }
            shrunkTitle.setVisibility(View.INVISIBLE);
            expandedContainer.setVisibility(View.VISIBLE);
            shrunkContainer.setClickable(false);
            shrunk = false;
        }

        /**
         * handles the shrinking functionality.
         */
        public void shrink() {
            toggleExpandShrinkIcon(false);
            expandedContainer.setVisibility(View.GONE);
            shrunkTitle.setVisibility(View.VISIBLE);
            shrunk = true;
        }

        /**
         * moves the user to the subGoal selected (the function gets the subGoal selected as a parameter.
         *
         * @param goal is the selected subGoal.
         */
        private void goToSubGoal(Goal goal) {
            int subGoalPositionInAchievedGoals = PublicMethods.positionOfGoalInGoalsArrayList(goal.getName(), achievedGoals);
            expandedItem = subGoalPositionInAchievedGoals;
            if (subGoalPositionInAchievedGoals != -1) {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(getAdapterPosition())).itemView.performClick();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPositionInRecyclerView(subGoalPositionInAchievedGoals, Objects.requireNonNull(recyclerView.getLayoutManager()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(subGoalPositionInAchievedGoals)).itemView.performClick();
                            }
                        }, Math.abs(subGoalPositionInAchievedGoals - getAdapterPosition()));
                    }
                }, 850);

            }
        }

        /**
         * moves the user back from the subGoal selected to its parent.
         *
         * @param goal is the parent.
         */
        private void goBackToParent(Goal goal) {
            int parentPositionInAchievedGoals = PublicMethods.positionOfGoalInGoalsArrayList(goal.getParentGoal(), achievedGoals);
            if (parentPositionInAchievedGoals != -1) {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(getAdapterPosition())).itemView.performClick();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPositionInRecyclerView(parentPositionInAchievedGoals, Objects.requireNonNull(recyclerView.getLayoutManager()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(parentPositionInAchievedGoals)).itemView.performClick();

                            }
                        }, Math.abs(parentPositionInAchievedGoals - getAdapterPosition()));
                    }
                }, 850);
            }
        }

    }

    @NonNull
    @Override
    public AchievedGoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.achieved_goal_card, parent, false);
        return new AchievedGoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievedGoalsViewHolder holder, int position) {
        if (achievedGoals.get(position) == null) {
            return;
        }

        int resource;
        if (expandedItem != position) {
            if (holder.expandedContainer.getVisibility() == View.VISIBLE) {
                holder.shrink();
            }
            resource = R.drawable.expand;
        } else {
            holder.expandedContainer.setVisibility(View.VISIBLE);
            resource = R.drawable.shrink;
        }
        holder.btnExpandShrink.setImageResource(resource);

        ArrayList<Goal> subGoalsArrayList;
        Goal currentGoal = achievedGoals.get(position);
        subGoalsArrayList = db.getSubGoalsArrayListOf(currentGoal);

        String name = currentGoal.getName(),
                description = currentGoal.getDescription();

        String[] tags = currentGoal.getTagsAsArray();

        holder.shrunkTitle.setText(name);
        holder.expandedTitle.setText(name);
        holder.description.setText(description);
        holder.description.setOnTouchListener(this);
        holder.description.setMovementMethod(ScrollingMovementMethod.getInstance());
        holder.finishDate.setText(currentGoal.getFinishDate());

        int color = context.getResources().getColor(R.color.difficulty_gold);
        switch (currentGoal.getDifficulty()) {
            case 1:
                color = context.getResources().getColor(R.color.difficulty_bronze);
                break;
            case 2:
                color = context.getResources().getColor(R.color.difficulty_silver);
                break;
            case 3:
                color = context.getResources().getColor(R.color.difficulty_gold);
                break;
            case 4:
                color = context.getResources().getColor(R.color.difficulty_purple);
                break;
            case 5:
                color = context.getResources().getColor(R.color.difficulty_black);
                break;
            default:
                break;
        }
        holder.parentCard.setCardBackgroundColor(color);
        holder.shrunkTitle.setTextColor(PublicMethods.getInverseColor(color));
        holder.expandedTitle.setTextColor(PublicMethods.getInverseColor(color));
        holder.description.setTextColor(PublicMethods.getInverseColor(color));
        holder.finishDate.setTextColor(PublicMethods.getInverseColor(color));
        holder.goalsTagsLabel.setTextColor(PublicMethods.getInverseColor(color));
        holder.goalsTagsGroup.setGravity(ChipCloud.Gravity.CENTER);
        holder.goalsTagsGroup.removeAllViews();
        holder.goalsTagsGroup.addChips(tags);

        holder.medal_1.setVisibility(View.VISIBLE);
        holder.medal_2.setVisibility(View.VISIBLE);
        holder.medal_3.setVisibility(View.VISIBLE);
        holder.medal_4.setVisibility(View.VISIBLE);
        holder.medal_5.setVisibility(View.VISIBLE);

        switch (currentGoal.getEvolving()) {
            case 1:
                holder.medal_1.setVisibility(View.GONE);
            case 2:
                holder.medal_2.setVisibility(View.GONE);
            case 3:
                holder.medal_3.setVisibility(View.GONE);
            case 4:
                holder.medal_4.setVisibility(View.GONE);
            default:
                break;
        }

        switch (currentGoal.getSatisfaction()) {
            case 1:
                holder.satisfactionEmoji.setImageResource(R.drawable.smiley_1);
                break;
            case 2:
                holder.satisfactionEmoji.setImageResource(R.drawable.smiley_2);
                break;
            case 3:
                holder.satisfactionEmoji.setImageResource(R.drawable.smiley_3);
                break;
            case 4:
                holder.satisfactionEmoji.setImageResource(R.drawable.smiley_4);
                break;
            case 5:
                holder.satisfactionEmoji.setImageResource(R.drawable.smiley_5);
                break;
        }

        if (subGoalsArrayList.size() <= 0) {
            holder.subGoalsTitleContainer.setVisibility(View.GONE);
            holder.subGoalsRecyclerViewContainer.setVisibility(View.GONE);
        } else {
            holder.subGoalsTitleContainer.setVisibility(View.VISIBLE);
            holder.subGoalsRecyclerViewContainer.setVisibility(View.VISIBLE);
            initSubGoalsAdapter(holder.subGoalsRecyclerView, subGoalsArrayList, holder);
        }

        if (openedFromParent[0]) {
            holder.btnBackToParent.setVisibility(View.VISIBLE);
        } else {
            holder.btnBackToParent.setVisibility(View.GONE);
        }

    }

    /**
     * Set the expanded item from outside the adapter.
     */
    public void setExpandedItem(int expandedItem){
        this.expandedItem = expandedItem;
    }

    /**
     * @param subGoalsRecyclerView
     * @param subGoals
     * @param holder
     */
    public void initSubGoalsAdapter(RecyclerView subGoalsRecyclerView, ArrayList<Goal> subGoals, AchievedGoalsAdapter.AchievedGoalsViewHolder holder) {
        GoalsAdapter adapter = new GoalsAdapter(context, subGoals);
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false);
        layoutManager.setPostLayoutListener((CarouselLayoutManager.PostLayoutListener) new CarouselZoomPostLayoutListener());
        subGoalsRecyclerView.setLayoutManager(layoutManager);
        subGoalsRecyclerView.setHasFixedSize(true);
        subGoalsRecyclerView.setAdapter(adapter);
        subGoalsRecyclerView.addOnScrollListener(new CenterScrollListener());

        adapter.setOnItemClickListener(new GoalsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                holder.goToSubGoal(subGoals.get(position));
                openedFromParent[0] = true;
                openedFromParent[1] = false;
            }
        });
    }

    public void scrollToPositionInRecyclerView(int position, RecyclerView.LayoutManager layoutManager) {
//        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(context) {
//            protected int getVerticalSnapPreference() {
//                return -1;
//            }
//        };
//        linearSmoothScroller.setTargetPosition(position);
//        layoutManager.startSmoothScroll(linearSmoothScroller);

        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);

    }

    @Override
    public int getItemCount() {
        return achievedGoals.size();
    }

}
