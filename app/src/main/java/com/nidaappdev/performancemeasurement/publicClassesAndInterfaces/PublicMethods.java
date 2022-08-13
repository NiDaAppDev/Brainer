package com.nidaappdev.performancemeasurement.publicClassesAndInterfaces;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.adroitandroid.chipcloud.Chip;
import com.adroitandroid.chipcloud.ChipCloud;
import com.nidaappdev.performancemeasurement.App;
import com.nidaappdev.performancemeasurement.R;
import com.nidaappdev.performancemeasurement.customObjects.Goal;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.Focus.Focus;
import com.nidaappdev.performancemeasurement.customViews.Tutorial.TutorialConfiguration;
import com.nidaappdev.performancemeasurement.databaseObjects.GoalDBHelper;
import com.nidaappdev.performancemeasurement.Lottie.DialogHandler;
import com.nidaappdev.performancemeasurement.util.PrefUtil;
import com.labters.lottiealertdialoglibrary.DialogTypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class PublicMethods<T> {

    public static Goal finishingGoal;

    public static <T> ArrayList<T> arrayListWithout(ArrayList<T> arrayList, ArrayList<T> exceptions) {
        ArrayList<T> output = new ArrayList<>(arrayList);

        for (T exception : exceptions) {
            output.remove(exception);
        }

        return output;
    }

    public static <T> T getValueOrDefault(T value, T defaultValue) {
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public static String formatDateTime(String timeToFormat) {

        String finalDateTime = "";

        Date date = null;
        if (timeToFormat != null) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeToFormat);
            } catch (ParseException e) {
                date = null;
            }

            if (date != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                finalDateTime = formatter.format(date);
            }
        }

        return finalDateTime;

    }

    public static String formatStopWatchTime(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static int getInverseColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, 255 - red, 255 - green, 255 - blue);
    }

    public static int positionOfGoalInGoalsArrayList(String goalName, ArrayList<Goal> goalsArrayList) {
        for (Goal goal : goalsArrayList) {
            if (goalName.equals(goal.getName())) {
                return goalsArrayList.indexOf(goal);
            }
        }
        return -1;
    }

    /**
     * Sort the ActiveGoalsFragment cards.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void sortActiveGoals(Context context, PrefUtil.ActiveSortMode sortMode, boolean ascending, ArrayList<Goal> arrayListToSort) {
        PrefUtil.setActiveSortMode(sortMode);
        PrefUtil.setActiveAscending(ascending);
        switch (sortMode) {
            case Date:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date d1 = sdf.parse(o1.getStartDate(context)),
                                d2 = sdf.parse(o2.getStartDate(context));
                        return d1.compareTo(d2);
                    } catch (ParseException e) {
                        return 0;
                    }
                });
                break;
            case Name:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                break;
            case Progress:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> o1.getProgress() - o2.getProgress());
            default:
                break;
        }

        if (!ascending) {
            Collections.reverse(arrayListToSort);
        }
    }


    /**
     * Sort the AchievedGoalsFragment cards.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void sortAchievedGoals(Context context, PrefUtil.AchievedSortMode sortMode, boolean ascending, ArrayList<Goal> arrayListToSort, ChipCloud tagFilter, ArrayList<Integer> filters) {

        PrefUtil.setAchievedSortMode(sortMode);
        PrefUtil.setAchievedAscending(ascending);
        PrefUtil.setAchievedGoalsFilters(filters);

        ArrayList<Goal> filteredArrayListToRemoveFromOriginal = new ArrayList<>();

        GoalDBHelper db = new GoalDBHelper(context);
        arrayListToSort.clear();
        arrayListToSort.addAll(db.getAchievedGoalsArrayList());

        for (Goal goal : arrayListToSort) {
            boolean goalHasOneOfTheTags = false;
            for (int filterTag : filters) {
                if (tagFilter != null && goal.getTagsAsArrayList().contains(((Chip) tagFilter.getChildAt(filterTag)).getText().toString())) {
                    goalHasOneOfTheTags = true;
                }
            }
            if (!goalHasOneOfTheTags) {
                filteredArrayListToRemoveFromOriginal.add(goal);
            }
        }

        arrayListToSort.removeAll(filteredArrayListToRemoveFromOriginal);


        switch (sortMode) {
            case Name:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                break;
            case FinishDate:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date d1 = sdf.parse(o1.getFinishDate()),
                                d2 = sdf.parse(o2.getFinishDate());
                        return d1.compareTo(d2);
                    } catch (ParseException e) {
                        return 0;
                    }
                });
                break;
            case Difficulty:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> o1.getDifficulty() - o2.getDifficulty());
                break;
            case Evolving:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> o1.getEvolving() - o2.getEvolving());
                break;
            case Satisfaction:
                Collections.sort(arrayListToSort, (o1, o2) -> {
                    try {
                        int numericO1 = Integer.parseInt(o1.getName()),
                                numericO2 = Integer.parseInt(o2.getName());
                        return numericO1 - numericO2;
                    } catch (Exception e) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                Collections.sort(arrayListToSort, (o1, o2) -> o1.getSatisfaction() - o2.getSatisfaction());
                break;
            default:
                break;
        }

        if (!ascending) {
            Collections.reverse(arrayListToSort);
        }

    }


    /**
     * handles what happens when trying to save edits while name was changed and identical to
     * another goals name.
     */
    public static void openIdenticalGoalNameErrorDialog(Context context, Activity activity, String name) {
        DialogHandler dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable okProcedure;
        okProcedure = new Runnable() {
            @Override
            public void run() {
                /* Here handle whatever happens when user clicks the 'OK' button.*/
            }
        };
        dialogHandler.showDialog(activity,
                context,
                "Goals Name Already Exist",
                "The name \"" + name + "\" is already used on another goal. Please think of another name for your goal.",
                "OK",
                okProcedure,
                DialogTypes.TYPE_ERROR,
                null);
    }

    /**
     * handles what happens when trying to save edits while name was changed and identical to
     * another goals name.
     */
    public static void openGoalNameNotValidErrorDialog(Context context, Activity activity, String name) {
        DialogHandler dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable okProcedure;
        okProcedure = () -> {
            /* Here handle whatever happens when user clicks the 'OK' button.*/
        };
        dialogHandler.showDialog(activity,
                context,
                "Goals Name Is Not Valid",
                "The name \"" + name + "\" is not valid. Please think of another name for your goal.",
                "OK",
                okProcedure,
                DialogTypes.TYPE_ERROR,
                null);
    }

    /**
     * handles what happens when trying to create a new goal without inserting an estimation.
     */
    public static void openGoalTimeEstimatedNotValidErrorDialog(Context context, Activity activity) {
        DialogHandler dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable okProcedure = () -> {
            /* Here handle whatever happens when user clicks the 'OK' button.*/
        };
        dialogHandler.showDialog(activity,
                context,
                "Time Estimated Is Not Valid",
                "You must estimate the goal's duration in order to create it.",
                "OK",
                okProcedure,
                DialogTypes.TYPE_ERROR,
                null);
    }


    public static int getNewGoalsTimeEstimated(EditText newGoalsTimeEstimated) {
        int days, hours, minutes;
        try {
            days = Integer.parseInt(newGoalsTimeEstimated.getText().toString().substring(0, 2));
            hours = Integer.parseInt(newGoalsTimeEstimated.getText().toString().substring(7, 9));
            minutes = Integer.parseInt(newGoalsTimeEstimated.getText().toString().substring(14, 16));
        } catch (Exception e) {
            return 0;
        }
        return ((days * 1440) + (hours * 60) + minutes) * 60;
    }

    /**
     * handles what happens when trying to create a new goal while timer or pomodoro is running.
     */
    public static void openTimerIsRunningErrorDialog(Context context, Activity activity) {
        DialogHandler dialogHandler = DialogHandler.getDialogHandler(context);
        Runnable okProcedure = new Runnable() {
            @Override
            public void run() {
                /* Here handle whatever happens when user clicks the 'OK' button.*/
            }
        };
        dialogHandler.showDialog(activity,
                context,
                "Timer Is Running",
                "You cannot create a new goal while another one is in progress." +
                        "\nPlease finish the current goal's session or stop the timer to create a new goal.",
                "OK",
                okProcedure,
                DialogTypes.TYPE_ERROR,
                null);
    }

    public static void setFinishingGoal(Goal finishingGoal) {
        PublicMethods.finishingGoal = finishingGoal;
    }

    public static Goal getFinishingGoal() {
        return finishingGoal;
    }

    public static TutorialConfiguration tutorialConfig() {
        TutorialConfiguration configuration = new TutorialConfiguration();
        configuration.setFocusType(Focus.ALL);
        configuration.setDotViewEnabled(true);
        configuration.setDismissOnTouch(false);
        configuration.setColorTextViewInfo(App.appContext.getResources().getColor(R.color.brain1));
        return configuration;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static Context getAppContext() {
        return App.appContext;
    }

}
