package com.example.performancemeasurement.GoalAndDatabaseObjects;

import android.app.Activity;
import android.content.Context;

import com.example.performancemeasurement.Lottie.ClickListenerCustom;
import com.example.performancemeasurement.R;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialogCustom;

import org.jetbrains.annotations.NotNull;

public class DialogHandler {

    public Runnable ans_ok = null;

    public boolean continueExpanding(Activity activity, Context context, String title, String content, String okBtn, Runnable okProcedure){

        final boolean[] shouldContinue = new boolean[1];

        ans_ok = okProcedure;

        LottieAlertDialogCustom stopEditDialog = new LottieAlertDialogCustom.Builder(activity, DialogTypes.TYPE_WARNING)
                .setTitle(title)
                .setDescription(content)
                .setPositiveText(okBtn)
                .setPositiveButtonColor(context.getResources().getColor(R.color.save_edits))
                .setPositiveTextColor(context.getResources().getColor(R.color.white))
                .setPositiveListener(new ClickListenerCustom() {
                    @Override
                    public void onClick(@NotNull LottieAlertDialogCustom lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                        shouldContinue[0] = false;
                        ans_ok.run();
                    }
                })
                .build();
        stopEditDialog.show();
        return shouldContinue[0];
    }
//context.getResources().getColor(R.color.save_edits)

}
