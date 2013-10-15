package com.glevel.wwii.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.glevel.wwii.R;
import com.glevel.wwii.WWApplication;
import com.glevel.wwii.game.GameUtils;

public class ApplicationUtils {

    public static final String PREFS_NB_LAUNCHES = "nb_launch";
    public static final int NB_LAUNCHES_WITH_SPLASHSCREEN = 5;
    private static final int NB_LAUNCHES_RATE_DIALOG_APPEARS = 5;

    public static void showRateDialogIfNeeded(final FragmentActivity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(GameUtils.GAME_PREFS_FILENAME,
                FragmentActivity.MODE_PRIVATE);
        if (prefs.getInt(PREFS_NB_LAUNCHES, 0) == NB_LAUNCHES_RATE_DIALOG_APPEARS) {

            final Editor editor = prefs.edit();

            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.Dialog)
                    .setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.rate_title)
                    .setMessage(activity.getString(R.string.rate_message, activity.getString(R.string.app_name)))
                    .setPositiveButton(R.string.rate_now, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt(PREFS_NB_LAUNCHES, NB_LAUNCHES_RATE_DIALOG_APPEARS + 1);
                            editor.commit();
                            rateTheApp(activity);
                            dialog.dismiss();
                        }
                    }).setNegativeButton(R.string.rate_dont_want, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt(PREFS_NB_LAUNCHES, NB_LAUNCHES_RATE_DIALOG_APPEARS + 1);
                            editor.commit();
                            dialog.dismiss();
                        }
                    }).setNeutralButton(R.string.rate_later, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editor.putInt(PREFS_NB_LAUNCHES, 1);
                            dialog.dismiss();
                        }
                    }).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            stylifyDefaultAlertDialog(dialog);
        }
    }

    public static void rateTheApp(Context context) {
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.glevel.wwii"));
        context.startActivity(goToMarket);
    }

    public static void contactSupport(Context context) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { context.getString(R.string.mail_address) });
        i.putExtra(Intent.EXTRA_SUBJECT,
                context.getString(R.string.contact_title) + context.getString(R.string.app_name));
        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.contact_support_via)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, context.getString(R.string.no_mail_client), Toast.LENGTH_LONG).show();
        }
    }

    public static void stylifyDefaultAlertDialog(AlertDialog dialog) {
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        // center message and update font
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        messageView.setTypeface(WWApplication.FONTS.main);
        messageView.setBackgroundResource(R.drawable.bg_alert_dialog_btn);

        // update buttons background and font
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTypeface(WWApplication.FONTS.main);
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setTypeface(WWApplication.FONTS.main);
        Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setTypeface(WWApplication.FONTS.main);
    }

}
