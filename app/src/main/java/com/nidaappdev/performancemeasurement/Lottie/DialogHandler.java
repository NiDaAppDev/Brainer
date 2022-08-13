package com.nidaappdev.performancemeasurement.Lottie;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.nidaappdev.performancemeasurement.R;
import com.labters.lottiealertdialoglibrary.LottieAlertDialogCustom;

import org.jetbrains.annotations.NotNull;

public class DialogHandler {

    private Runnable ans_positive = null;
    private Runnable ans_negative = null;
    private static DialogHandler dialogHandler;
    private LottieAlertDialogCustom dialog;

    public static DialogHandler getDialogHandler(Context context){
        if(dialogHandler == null){
            dialogHandler = new DialogHandler();
        }
        return dialogHandler;
    }

    public boolean showDialog(Activity activity, Context context, String title, String content, String positiveBtn, Runnable positiveProcedure, int dialogType, String customName){

        final boolean[] shouldContinue = new boolean[1];

        ans_positive = positiveProcedure;

        LottieAlertDialogCustom tempDialog = new LottieAlertDialogCustom.Builder(activity, dialogType, customName)
                .setTitle(title)
                .setDescription(content)
                .setPositiveText(positiveBtn)
                .setPositiveButtonColor(context.getResources().getColor(R.color.save_edits))
                .setPositiveTextColor(context.getResources().getColor(R.color.white))
                .setPositiveListener(new ClickListenerCustom() {
                    @Override
                    public void onClick(@NotNull LottieAlertDialogCustom dialog) {
                        dialog.dismiss();
                        shouldContinue[0] = false;
                        ans_positive.run();
                    }
                })
                .build();

        if(dialog == null || !dialog.equals(tempDialog)) {
            dialog = tempDialog;
        }
        dialog.setCancelable(false);
        dialog.show();
        return shouldContinue[0];
    }

    public boolean showDialog(Activity activity, Context context, String title, String content, String positiveBtn, Runnable positiveProcedure, String negativeBtn, Runnable negativeProcedure, int dialogType, String customName){

        final boolean[] shouldContinue = new boolean[1];

        ans_positive = positiveProcedure;
        ans_negative = negativeProcedure;

        LottieAlertDialogCustom tempDialog = new LottieAlertDialogCustom.Builder(activity, dialogType, customName)
                .setTitle(title)
                .setDescription(content)
                .setPositiveText(positiveBtn)
                .setPositiveButtonColor(context.getResources().getColor(R.color.save_edits))
                .setPositiveTextColor(context.getResources().getColor(R.color.white))
                .setPositiveListener(new ClickListenerCustom() {
                    @Override
                    public void onClick(@NotNull LottieAlertDialogCustom dialog) {
                        dialog.dismiss();
                        shouldContinue[0] = false;
                        ans_positive.run();
                    }
                })
                .setNegativeText(negativeBtn)
                .setNegativeButtonColor(context.getResources().getColor(R.color.stop_red))
                .setNegativeTextColor(context.getResources().getColor(R.color.white))
                .setNegativeListener(new ClickListenerCustom() {
                    @Override
                    public void onClick(@NonNull LottieAlertDialogCustom dialog) {
                        dialog.dismiss();
                        shouldContinue[0] = false;
                        ans_negative.run();
                    }
                })
                .build();
        if(dialog == null || !dialog.equals(tempDialog)) {
            dialog = tempDialog;
        }
        dialog.setCancelable(false);
        dialog.show();
        return shouldContinue[0];
    }
//context.getResources().getColor(R.color.save_edits)

}
