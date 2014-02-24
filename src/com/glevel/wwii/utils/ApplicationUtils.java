package com.glevel.wwii.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.glevel.wwii.R;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventAction;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventCategory;

public class ApplicationUtils {

    public static final String PREFS_NB_LAUNCHES = "nb_launches";
    public static final String PREFS_RATE_DIALOG_IN = "rate_dialog_in";
    public static final int NB_LAUNCHES_WITH_SPLASHSCREEN = 8;
    public static final int NB_LAUNCHES_RATE_DIALOG_APPEARS = 5;

    public static void showRateDialogIfNeeded(final Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        if (prefs.getInt(PREFS_RATE_DIALOG_IN, NB_LAUNCHES_RATE_DIALOG_APPEARS) == 0) {
            final Editor editor = prefs.edit();

            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = (View) inflater.inflate(R.layout.custom_rate_dialog, null);

            // Create and show custom alert dialog
            final Dialog dialog = new AlertDialog.Builder(activity, R.style.Dialog).setView(view).create();

            ((TextView) view.findViewById(R.id.message)).setText(activity.getString(R.string.rate_message,
                    activity.getString(R.string.app_name)));
            ((Button) view.findViewById(R.id.cancelButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putInt(PREFS_RATE_DIALOG_IN, -1);
                    editor.commit();
                    dialog.dismiss();
                    GoogleAnalyticsHelper.sendEvent(activity, EventCategory.ui_action, EventAction.button_press,
                            "rate_app_no");
                }
            });
            ((Button) view.findViewById(R.id.neutralButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putInt(PREFS_RATE_DIALOG_IN, 5);
                    dialog.dismiss();
                    GoogleAnalyticsHelper.sendEvent(activity, EventCategory.ui_action, EventAction.button_press,
                            "rate_app_later");
                }
            });
            ((Button) view.findViewById(R.id.okButton)).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putInt(PREFS_RATE_DIALOG_IN, -1);
                    editor.commit();
                    rateTheApp(activity);
                    dialog.dismiss();
                    GoogleAnalyticsHelper.sendEvent(activity, EventCategory.ui_action, EventAction.button_press,
                            "rate_app_yes");
                }
            });

            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            // Remove padding from parent
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.setPadding(0, 0, 0, 0);
        }
    }

    public static void rateTheApp(Activity activity) {
        Intent goToMarket = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + activity.getPackageName()));
        activity.startActivity(goToMarket);
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
        GoogleAnalyticsHelper.sendEvent(context, EventCategory.ui_action, EventAction.button_press, "contact_support");
    }

    public static void showToast(Context context, int textResourceId, int duration) {
        // setup custom toast view
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(textResourceId);

        Toast toast = new Toast(context);
        // toast.setGravity(Gravity.CE, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    public static void openDialogFragment(FragmentActivity activity, DialogFragment dialog, Bundle bundle) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        Fragment prev = activity.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        dialog.setArguments(bundle);
        dialog.show(ft, "dialog");
    }

    public static Runnable addStormBackgroundAtmosphere(final ImageView backgroundView, final int fromAlpha,
            final int stormAlpha) {
        // Storm atmosphere
        backgroundView.setColorFilter(Color.argb(fromAlpha, 0, 0, 0));
        Runnable stormEffectRunnable = new Runnable() {

            private boolean isThunder = false;

            public void run() {
                if (Math.random() < 0.03) {
                    // thunderstruck !
                    isThunder = true;
                    backgroundView.setColorFilter(Color.argb(stormAlpha, 0, 0, 0));
                } else if (isThunder) {
                    isThunder = false;
                    backgroundView.setColorFilter(Color.argb(fromAlpha, 0, 0, 0));
                }
                backgroundView.postDelayed(this, 100);
            }
        };
        backgroundView.postDelayed(stormEffectRunnable, 100);
        return stormEffectRunnable;
    }

    public static Point getScreenDimensions(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void startSharing(Activity activity, String subject, String text, int image) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        if (image > 0) {
            sharingIntent.setType("image/jpeg");
            Uri imageUri = Uri.parse("android.resource://" + activity.getPackageName() + "/drawable/" + image);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        } else {
            sharingIntent.setType("text/plain");
        }
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

}
