package com.example.performancemeasurement.GoalRecyclerViewAdapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.developer.mtextfield.ExtendedEditText;
import com.developer.mtextfield.TextFieldBoxes;
import com.example.performancemeasurement.GoalAndDatabaseObjects.DialogHandler;
import com.example.performancemeasurement.GoalAndDatabaseObjects.Goal;
import com.example.performancemeasurement.GoalAndDatabaseObjects.GoalDBHelper;
import com.example.performancemeasurement.R;
import com.example.performancemeasurement.activities.MainActivity;
import com.example.performancemeasurement.customViews.CustomProgressBar.CustomProgressBar;
import com.example.performancemeasurement.customViews.NestedRecyclerView.NestedRecyclerView;
import com.example.performancemeasurement.publicClassesAndInterfaces.PublicMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import it.emperor.animatedcheckbox.AnimatedCheckBox;

//TODO: fix bug {When opening a goal with edit button, the other opened goal won't close up}.
//TODO: add finish button

public class ActiveGoalsAdapter extends RecyclerView.Adapter<ActiveGoalsAdapter.ActiveGoalsViewHolder> implements View.OnTouchListener {

    private Context context;
    private Cursor cursor;
    private ArrayList<Goal> activeGoals;
    private MainActivity activity;
    private NestedRecyclerView recyclerView;
    private GoalDBHelper db;
    private int expandedItem = -1, editedItem = -1;
    private final static boolean[] openedFromParent = new boolean[]{false, true}, continueExpanding = new boolean[]{true}, selectable = new boolean[]{false};
    private ArrayList<Goal> removedSubGoals = new ArrayList<>(), selectedGoals = new ArrayList<>();
    private String newName, newDescription;
    private DialogHandler stopEditDialogHandler;
    private FloatingActionButton fab;

    public ActiveGoalsAdapter(Context context, ArrayList<Goal> activeGoals, Cursor cursor, NestedRecyclerView recyclerView, MainActivity activity, FloatingActionButton fab) {
        this.context = context;
        this.activeGoals = activeGoals;
        this.cursor = cursor;
        this.recyclerView = recyclerView;
        this.activity = activity;
        this.fab = fab;

        db = new GoalDBHelper(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }

    /**
     * get from outside the adapter class whether the items are in selectable mode or not.
     */
    public boolean getSelectable(){
        return selectable[0];
    }

    /**
     * set from outside the adapter class whether the items are in selectable mode or not.
     */
    public void setSelectable(boolean new_selectable){
        selectable[0] = new_selectable;
        selectedGoals.clear();
        notifyDataSetChanged();
    }

    public void updateAddedActiveGoal() {
        activeGoals = db.getActiveGoalsArrayList();
        notifyItemInserted(0);
        scrollToPositionInRecyclerView(0, Objects.requireNonNull(recyclerView.getLayoutManager()));
    }

    public class ActiveGoalsViewHolder extends RecyclerView.ViewHolder {

        LinearLayout shrunkContainer, subGoalsTitleContainer, selectableContainer;
        RelativeLayout expandedContainer, subGoalsRecyclerViewContainer, btnFinish, btnDelete, btnCancel, btnSave;
        ConstraintLayout editPanel;
        CustomProgressBar shrunkProgressBar, expandedProgressBar, selectableProgressBar;
        ImageButton btnExpandShrink, btnEdit, btnBackToParent;
        AnimatedCheckBox selectButton;
        TextView title, description;
        RecyclerView subGoalsRecyclerView;
        ExtendedEditText nameET, descriptionET;
        TextFieldBoxes nameETContainer, descriptionETContainer;
        ItemTouchHelper itemTouchHelper;
        boolean shrunk = true;


        public ActiveGoalsViewHolder(@NonNull View itemView) {
            super(itemView);

            shrunkContainer = itemView.findViewById(R.id.shrunk_active_goal_container);
            expandedContainer = itemView.findViewById(R.id.expanded_active_goal_container);
            selectableContainer = itemView.findViewById(R.id.selectable_active_goal_container);
            editPanel = itemView.findViewById(R.id.edit_panel);
            btnExpandShrink = itemView.findViewById(R.id.active_goal_expand_shrink_btn);
            btnEdit = itemView.findViewById(R.id.active_goal_edit_btn);
            btnBackToParent = itemView.findViewById(R.id.active_goal_back_to_parent_btn);
            shrunkProgressBar = itemView.findViewById(R.id.shrunk_active_goal_progress_bar);
            shrunkProgressBar.enableDefaultGradient(true);
            title = itemView.findViewById(R.id.expanded_active_goal_title);
            expandedProgressBar = itemView.findViewById(R.id.expanded_active_goal_progress_bar);
            expandedProgressBar.enableDefaultGradient(true);
            description = itemView.findViewById(R.id.expanded_active_goal_description);
            selectableProgressBar = itemView.findViewById(R.id.selectable_active_goal_progress_bar);
            selectableProgressBar.enableDefaultGradient(true);
            selectButton = itemView.findViewById(R.id.active_goal_select_button);
            subGoalsTitleContainer = itemView.findViewById(R.id.expanded_active_goal_sub_goals_title_container);
            subGoalsRecyclerViewContainer = itemView.findViewById(R.id.expanded_active_goal_sub_goals_container);
            subGoalsRecyclerView = itemView.findViewById(R.id.expanded_active_goal_sub_goals_recyclerview);
            nameET = itemView.findViewById(R.id.expanded_active_goal_edit_name_edit_text);
            descriptionET = itemView.findViewById(R.id.expanded_active_goal_edit_description_edit_text);
            nameETContainer = itemView.findViewById(R.id.expanded_active_goal_edit_name_field_box);
            descriptionETContainer = itemView.findViewById(R.id.expanded_active_goal_edit_description_field_box);
            btnFinish = itemView.findViewById(R.id.finish_goal_button);
            btnDelete = itemView.findViewById(R.id.edit_delete_button);
            btnCancel = itemView.findViewById(R.id.edit_cancel_button);
            btnSave = itemView.findViewById(R.id.edit_save_button);

            itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);

            /**
             * controls what happens when an active-goal item is clicked.
             */
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectable[0]) {
                        selectButton.callOnClick();
                    } else {
                        toggleExpand();
                    }
                }
            });

            /**
             * controls what happens when an active-goal item is long-clicked.
             */
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!selectable[0]) {
                        selectable[0] = true;
                        expandedItem = -1;
                        selectButton.callOnClick();
                        notifyDataSetChanged();
                    }
                    return false;
                }
            });

            /**
             * controls what happens when an active-goal items expand/shrink button is clicked.
             */
            btnExpandShrink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand();
                }
            });

            /**
             * controls what happens when an active-goal items back-to-parent button is clicked.
             */
            btnBackToParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBackToParent(activeGoals.get(getAdapterPosition()));
                }
            });

            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishGoal(activeGoals.get(getAdapterPosition()));
                }
            });

            /**
             * controls what happens when an active-goal items edit button is clicked.
             */
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEdit();
                }
            });

            /**
             * controls what happens when an active-goal items edit->cancel button is clicked.
             */
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Goal currentGoal = activeGoals.get(getAdapterPosition());
                    endEdit(currentGoal, currentGoal.getName(), currentGoal.getDescription(), removedSubGoals, "cancel");
                }
            });

            /**
             * controls what happens when an active-goal items edit->save button is clicked.
             */
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Goal currentGoal = activeGoals.get(getAdapterPosition());
                    endEdit(currentGoal, nameET.getText().toString(), descriptionET.getText().toString(), removedSubGoals, "save");
                }
            });

            /**
             * controls what happens when an active-goal items edit->delete button is clicked.
             */
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Goal currentGoal = activeGoals.get(getAdapterPosition());
                    endEdit(currentGoal, currentGoal.getName(), currentGoal.getDescription(), removedSubGoals, "delete");
                }
            });

            /**
             * controls what happens when a goal is selected / unselected.
             */
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedGoals.contains(activeGoals.get(getAdapterPosition()))) {
                        select(false);
                    } else {
                        select(true);
                    }
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
            if (editedItem == -1 || openStopEditWarningDialog(activeGoals.get(editedItem), getAdapterPosition())) {
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
            expandedContainer.setVisibility(View.VISIBLE);
            shrunkProgressBar.setVisibility(View.INVISIBLE);
            selectableContainer.setVisibility(View.INVISIBLE);
            shrunkContainer.setClickable(false);
            shrunk = false;
        }

        /**
         * handles the shrinking functionality.
         */
        public void shrink() {
            toggleExpandShrinkIcon(false);
            expandedContainer.setVisibility(View.GONE);
            shrunkProgressBar.setVisibility(View.VISIBLE);
            selectableContainer.setVisibility(View.INVISIBLE);
            selectableContainer.setFocusable(false);
            shrunk = true;
        }

        /**
         * switches the cards to selectable mode
         */
        public void setToSelectable() {
            expandedContainer.setVisibility(View.GONE);
            shrunkContainer.setVisibility(View.INVISIBLE);
            shrunkProgressBar.setVisibility(View.INVISIBLE);
            selectableContainer.setVisibility(View.VISIBLE);
            selectableContainer.setFocusable(true);
            shrunkContainer.setFocusable(false);
            shrunk = true;
        }

        /**
         * switches the cards to not-selectable mode
         */
        public void setToNotSelectable() {
            expandedContainer.setVisibility(View.GONE);
            shrunkContainer.setVisibility(View.VISIBLE);
            shrunkProgressBar.setVisibility(View.VISIBLE);
            selectableContainer.setVisibility(View.INVISIBLE);
            selectableContainer.setFocusable(false);
            shrunkContainer.setFocusable(true);
            shrunk = true;
        }

        /**
         * switches the card to selected / unselected
         */
        public void select(boolean select) {
            if (select) {
                selectedGoals.add(activeGoals.get(getAdapterPosition()));
                selectButton.setChecked(true, true);
            } else {
                selectedGoals.remove(activeGoals.get(getAdapterPosition()));
                selectButton.setChecked(false, true);
            }
        }

        /**
         * moves the user to the subGoal selected (the function gets the subGoal selected as a parameter.
         *
         * @param goal is the selected subGoal.
         */
        private void goToSubGoal(Goal goal) {
            int subGoalPositionInActiveGoals = PublicMethods.positionOfGoalInGoalsArrayList(goal.getName(), activeGoals);
            expandedItem = subGoalPositionInActiveGoals;
            if (subGoalPositionInActiveGoals != -1 && (editedItem == -1 || openStopEditWarningDialog(activeGoals.get(editedItem), getAdapterPosition()))) {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(getAdapterPosition())).itemView.performClick();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPositionInRecyclerView(subGoalPositionInActiveGoals, Objects.requireNonNull(recyclerView.getLayoutManager()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(subGoalPositionInActiveGoals)).itemView.performClick();
                            }
                        }, Math.abs(subGoalPositionInActiveGoals - getAdapterPosition()));
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
            int parentPositionInActiveGoals = PublicMethods.positionOfGoalInGoalsArrayList(goal.getParentGoal(), activeGoals);
            if (parentPositionInActiveGoals != -1) {
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(getAdapterPosition())).itemView.performClick();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToPositionInRecyclerView(parentPositionInActiveGoals, Objects.requireNonNull(recyclerView.getLayoutManager()));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(parentPositionInActiveGoals)).itemView.performClick();

                            }
                        }, Math.abs(parentPositionInActiveGoals - getAdapterPosition()));
                    }
                }, 850);
            }
        }

        public void finishGoal(Goal goal) {
            //TODO: here add goal-finishing functionality
        }

        /**
         * handles edit-panel opening functionality.
         *
         * @param animated sets if the edit panel animates to open or not (if it was already opened
         *                 and just reloads then it won't animate.
         *                 if it gets opened at the moment by the user, it will animate.).
         */
        private void openEditPanel(boolean animated) {
            if (animated) {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView.getRootView(), new AutoTransition());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    TransitionManager.endTransitions((ViewGroup) itemView.getRootView());
                }
            }
            btnEdit.setVisibility(View.INVISIBLE);
            btnFinish.setVisibility(View.GONE);
            editPanel.setVisibility(View.VISIBLE);
            title.setVisibility(View.INVISIBLE);
            description.setVisibility(View.INVISIBLE);
            nameETContainer.setVisibility(View.VISIBLE);
            descriptionETContainer.setVisibility(View.VISIBLE);
            itemTouchHelper.attachToRecyclerView(subGoalsRecyclerView);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (context.getResources().getDisplayMetrics().density * 30.0f));
            params.addRule(RelativeLayout.BELOW, R.id.expanded_active_goal_edit_name_field_box);
            expandedProgressBar.setLayoutParams(params);

            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.expanded_active_goal_edit_description_field_box);
            subGoalsTitleContainer.setLayoutParams(params);

            nameET.setText(newName);
            descriptionET.setText(newDescription);
            if (subGoalsRecyclerView.getAdapter() != null) {
                ((GoalsAdapter) Objects.requireNonNull(subGoalsRecyclerView.getAdapter())).setGoals(PublicMethods.arrayListWithout(db.getSubGoalsArrayListOf(activeGoals.get(getAdapterPosition())), removedSubGoals));
                Objects.requireNonNull(subGoalsRecyclerView.getAdapter()).notifyDataSetChanged();
            }
        }

        /**
         * handles edit-panel closing functionality.
         *
         * @param animated sets if the edit panel animates to close or not (if it was already closed
         *                 and just reloads then it won't animate.
         *                 if it gets closed at the moment by the user, it will animate.).
         */
        private void closeEditPanel(boolean animated) {
            if (animated) {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView.getRootView(), new AutoTransition());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    TransitionManager.endTransitions((ViewGroup) itemView.getRootView());
                }
            }

            btnEdit.setVisibility(View.VISIBLE);
            editPanel.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            nameETContainer.setVisibility(View.INVISIBLE);
            descriptionETContainer.setVisibility(View.INVISIBLE);
            itemTouchHelper.attachToRecyclerView(null);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (context.getResources().getDisplayMetrics().density * 30.0f));
            params.addRule(RelativeLayout.BELOW, R.id.expanded_active_goal_title);
            expandedProgressBar.setLayoutParams(params);

            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.expanded_active_goal_description);
            subGoalsTitleContainer.setLayoutParams(params);
        }

        /**
         * enables editing the selected goal and opens the edit-panel
         */
        private void startEdit() {
            if (editedItem == -1 || openStopEditWarningDialog(activeGoals.get(editedItem), getAdapterPosition())) {
                newName = title.getText().toString();
                removedSubGoals.clear();
                newDescription = description.getText().toString();
                if (expandedItem == getAdapterPosition() || expandedItem == -1) {
                    expand();
                } else {
                    toggleExpand();
                }
                editedItem = getAdapterPosition();
                expandedItem = editedItem;
                openEditPanel(true);
            }
        }

        /**
         * disables/ends editing the selected goal and saves whatever data changed in the database of goals.
         *
         * @param editedGoal      is the selected goal (the one that was edited).
         * @param newName         is the new name to be saved as the goals name (if it's different from the old one).
         * @param newDescription  is the new description to be saved as the goals description (if it's different from the old one).
         * @param removedSubGoals is the new subGoals list to be saved as the goals subGoals (if it's different from the old one).
         * @param endTitle        is the kind of ending of the edit - "save", "delete" or "cancel" - that param decides if the edits will be saved, the goal will be deleted from the database, or the edits will just be ignored, respectively
         */
        private void endEdit(Goal editedGoal, String newName, String newDescription, ArrayList<Goal> removedSubGoals, String endTitle) {

            boolean doneEditing = true;

            switch (endTitle) {
                case "save":
                    GoalDBHelper dbHelper = new GoalDBHelper(context);
                    if (!dbHelper.doesActiveGoalNameAlreadyExist(newName, editedGoal.getName())) {
                        Log.d(TAG, "endEdit: saved: (" + editedGoal.getName() + ", " + newName + ")");
                        db.editGoal(editedGoal, newName, newDescription, removedSubGoals);
                        activeGoals = db.getActiveGoalsArrayList();
                        notifyItemChanged(editedItem);
                        break;
                    }
                    Log.d(TAG, "endEdit: not: (" + editedGoal.getName() + ", " + newName + ")");
                    openIdenticalGoalNameWarningDialog(newName);
                    doneEditing = false;
                    break;
                case "delete":
                    db.removeGoal(editedGoal);
                    activeGoals = db.getActiveGoalsArrayList();
                    notifyItemRemoved(editedItem);
                    editedItem = -1;
                    break;
                case "cancel":
                    if (subGoalsRecyclerView.getAdapter() != null) {
                        ((GoalsAdapter) Objects.requireNonNull(subGoalsRecyclerView.getAdapter())).setGoals(db.getSubGoalsArrayListOf(activeGoals.get(editedItem)));
                        Objects.requireNonNull(subGoalsRecyclerView.getAdapter()).notifyDataSetChanged();
                    }
                default:
                    break;
            }
            if (doneEditing) {
                closeEditPanel(true);
                editedItem = -1;
            }


        }

        /**
         * enables scrolling to the right/left in order to remove an item.
         */
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Goal removedSubGoal = ((GoalsAdapter) (Objects.requireNonNull(subGoalsRecyclerView.getAdapter()))).getGoals().get(viewHolder.getAdapterPosition());
                removedSubGoals.add(removedSubGoal);
                ((GoalsAdapter) (Objects.requireNonNull(subGoalsRecyclerView.getAdapter()))).getGoals().remove(viewHolder.getAdapterPosition());
                subGoalsRecyclerView.getAdapter().notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

    }

    @NonNull
    @Override
    public ActiveGoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.active_goal_card, parent, false);
        return new ActiveGoalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveGoalsViewHolder holder, int position) {
        if (activeGoals.get(position) == null) {
            return;
        }

        if (editedItem != position) {
            if (holder.editPanel.getVisibility() == View.VISIBLE) {
                holder.closeEditPanel(false);
            }
            holder.editPanel.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.openEditPanel(false);
        }

        int resource;
        if (expandedItem != position) {
            if (holder.expandedContainer.getVisibility() == View.VISIBLE) {
                holder.shrink();
            }
            if (selectable[0]) {
                holder.setToSelectable();
                if (selectedGoals.contains(activeGoals.get(holder.getAdapterPosition()))) {
                    holder.selectButton.setChecked(true);
                } else {
                    holder.selectButton.setChecked(false);
                }
            } else {
                holder.setToNotSelectable();
            }
            resource = R.drawable.expand;
        } else {
            holder.shrunkProgressBar.setVisibility(View.INVISIBLE);
            holder.expandedContainer.setVisibility(View.VISIBLE);
            resource = R.drawable.shrink;
        }
        holder.btnExpandShrink.setImageResource(resource);

        ArrayList<Goal> subGoalsArrayList;
        Goal currentGoal = activeGoals.get(position);
        if (editedItem != position) {
            subGoalsArrayList = db.getSubGoalsArrayListOf(currentGoal);
        } else {
            subGoalsArrayList = PublicMethods.arrayListWithout(db.getSubGoalsArrayListOf(currentGoal), removedSubGoals);
        }


        String name = currentGoal.getName(),
                description = currentGoal.getDescription(),
                parent = currentGoal.getParentGoal();
        int timeCounted = currentGoal.getTimeCounted(),
                timeEstimated = currentGoal.getTimeEstimated();

        holder.shrunkProgressBar.setText(name);
        holder.shrunkProgressBar.setProgress((timeCounted * 100 / timeEstimated));
        holder.shrunkProgressBar.setRadius(300.0f);
        holder.expandedProgressBar.setText("");
        holder.expandedProgressBar.setProgress((timeCounted * 100 / timeEstimated));
        holder.expandedProgressBar.setRadius(300.0f);
        holder.selectableProgressBar.setText(name);
        holder.selectableProgressBar.setProgress((timeCounted * 100 / timeEstimated));
        holder.selectableProgressBar.setRadius(300.0f);
        holder.title.setText(name);
        holder.description.setText(description);
        holder.description.setOnTouchListener(this);
        holder.description.setMovementMethod(ScrollingMovementMethod.getInstance());

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
     * handles what happens when moving the focus from edited goal to another one.
     *
     * @param editedGoal      is the edited goal.
     * @param adapterPosition is the another one.
     * @return if the user wants to continue.
     */
    public boolean openStopEditWarningDialog(Goal editedGoal, int adapterPosition) {
        stopEditDialogHandler = new DialogHandler();
        Runnable okProcedure;
        okProcedure = new Runnable() {
            @Override
            public void run() {
                scrollToPositionInRecyclerView(editedItem, Objects.requireNonNull(recyclerView.getLayoutManager()));
            }
        };
        return stopEditDialogHandler.continueExpanding(activity, context, "Goal In Edit", "Changes were made to " + editedGoal.getName() + ". You cannot proceed while it's edited", "OK", okProcedure);
    }

    /**
     * handles what happens when trying to save edits while name was changed and identical to
     * another goals name.
     *
     * @returnif the user wants to continue.
     */
    public boolean openIdenticalGoalNameWarningDialog(String name) {
        stopEditDialogHandler = new DialogHandler();
        Runnable okProcedure;
        okProcedure = new Runnable() {
            @Override
            public void run() {
                /* Here handle whatever happens when user clicks the 'OK' button.*/
            }
        };
        return stopEditDialogHandler.continueExpanding(activity, context, "Goals Name Already Exist", "The name " + name + " is already used on another goal. Please think of another name for your goal.", "OK", okProcedure);
    }

    /**
     * @param subGoalsRecyclerView
     * @param subGoals
     * @param holder
     */
    public void initSubGoalsAdapter(RecyclerView subGoalsRecyclerView, ArrayList<Goal> subGoals, ActiveGoalsViewHolder holder) {
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
        return activeGoals.size();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
