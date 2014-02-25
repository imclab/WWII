package com.glevel.wwii.activities;

import java.util.List;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.glevel.wwii.MyActivity;
import com.glevel.wwii.R;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventAction;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventCategory;
import com.glevel.wwii.billing.InAppBillingHelper;
import com.glevel.wwii.billing.OnBillingServiceConnectedListener;
import com.glevel.wwii.database.DatabaseHelper;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.GameUtils.DifficultyLevel;
import com.glevel.wwii.game.GameUtils.MusicState;
import com.glevel.wwii.game.SaveGameHelper;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.utils.ApplicationUtils;
import com.glevel.wwii.utils.MusicManager;
import com.glevel.wwii.views.CustomAlertDialog;

public class HomeActivity extends MyActivity implements OnClickListener, OnBillingServiceConnectedListener {

    private static enum ScreenState {
        HOME, SOLO, SETTINGS
    }

    private SharedPreferences mSharedPrefs;
    private ScreenState mScreenState = ScreenState.HOME;
    private DatabaseHelper mDbHelper;

    private Animation mMainButtonAnimationRightIn, mMainButtonAnimationRightOut, mMainButtonAnimationLeftIn,
            mMainButtonAnimationLeftOut;
    private Animation mFadeOutAnimation, mFadeInAnimation;
    private Button mPlayButton, mSettingsButton, mTutorialButton, mAboutButton, mRateAppButton, mSupportButton,
            mShareButton;
    private ViewGroup mSettingsLayout;
    private View mBackButton, mAppNameView;
    private RadioGroup mRadioMusicvolume, mRadioDifficulty;
    private VideoView mBackgroundVideoView;
    private Dialog mAboutDialog = null;
    private InAppBillingHelper mInAppBillingHelper;

    /**
     * Callbacks
     */
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer m) {
            // remove background video sound - enable video looping
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                // disable sound
                m.setVolume(0f, 0f);
                // repeat video
                m.setLooping(true);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setupUI();

        mInAppBillingHelper = new InAppBillingHelper(this, this);

        ApplicationUtils.showRateDialogIfNeeded(this);
        ApplicationUtils.showAdvertisementIfNeeded(this);

        showMainHomeButtons();

        if (savedInstanceState != null) {
            // restart video where it had been stopped
            mBackgroundVideoView.seekTo(savedInstanceState.getInt("video_stop_position"));
        }

        mDbHelper = new DatabaseHelper(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store the stop position to restart the video at the correct position
        outState.putInt("video_stop_position", mBackgroundVideoView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackgroundVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackgroundVideoView.pause();
        if (mAboutDialog != null) {
            mAboutDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mInAppBillingHelper.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.isShown()) {
            switch (v.getId()) {
            case R.id.playButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                if (mSharedPrefs.getInt(GameUtils.TUTORIAL_DONE, 0) == 0) {
                    showTutorialDialog();
                } else {
                    List<Battle> lstBattles = SaveGameHelper.getUnfinishedBattles(mDbHelper);
                    if (lstBattles.size() > 0) {
                        showResumeGameDialog(lstBattles.get(0));
                    } else {
                        goToBattleChooserActivity();
                    }
                    GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                            EventAction.button_press, "play_solo");
                }
                break;
            case R.id.settingsButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                showSettings();
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_settings");
                break;
            case R.id.backButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                onBackPressed();
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "back_button_soft");
                break;
            case R.id.aboutButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                openAboutDialog();
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_about_dialog");
                break;
            case R.id.rateButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                ApplicationUtils.rateTheApp(this);
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "rate_app_button");
                break;
            case R.id.donateButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                mInAppBillingHelper.purchaseItem("com.glevel.wwii.donate");
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "donate_button");
                break;
            case R.id.shareButton:
                MusicManager.playSound(getApplicationContext(), R.raw.main_button);
                ApplicationUtils.startSharing(this, getString(R.string.share_subject, getString(R.string.app_name)),
                        getString(R.string.share_message, getPackageName()), 0);
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "share_app_button");
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (mScreenState) {
        case HOME:
            super.onBackPressed();
            break;
        case SETTINGS:
            showMainHomeButtons();
            hideSettings();
            GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action, EventAction.button_press,
                    "back_pressed");
            break;
        default:
            break;
        }
    }

    private void setupUI() {
        mSettingsLayout = (ViewGroup) findViewById(R.id.settingsLayout);

        mMainButtonAnimationRightIn = AnimationUtils.loadAnimation(this, R.anim.main_btn_right_in);
        mMainButtonAnimationLeftIn = AnimationUtils.loadAnimation(this, R.anim.main_btn_left_in);
        mMainButtonAnimationRightOut = AnimationUtils.loadAnimation(this, R.anim.main_btn_right_out);
        mMainButtonAnimationLeftOut = AnimationUtils.loadAnimation(this, R.anim.main_btn_left_out);

        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        mPlayButton = (Button) findViewById(R.id.playButton);
        mPlayButton.setOnClickListener(this);

        mSettingsButton = (Button) findViewById(R.id.settingsButton);
        mSettingsButton.setOnClickListener(this);

        mTutorialButton = (Button) findViewById(R.id.tutorialButton);
        mTutorialButton.setOnClickListener(this);

        mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(this);

        mSupportButton = (Button) findViewById(R.id.donateButton);
        mSupportButton.setOnClickListener(this);

        mShareButton = (Button) findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(this);

        mAppNameView = (View) findViewById(R.id.appName);

        mRadioDifficulty = (RadioGroup) findViewById(R.id.radioDifficulty);
        // update radio buttons states according to the game difficulty
        int gameDifficulty = mSharedPrefs.getInt(GameUtils.GAME_PREFS_KEY_DIFFICULTY,
                GameUtils.DifficultyLevel.medium.ordinal());
        ((RadioButton) mRadioDifficulty.getChildAt(gameDifficulty)).setChecked(true);
        mRadioDifficulty.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // update game difficulty in preferences
                DifficultyLevel newDifficultyLevel = null;
                Editor editor = mSharedPrefs.edit();
                switch (checkedId) {
                case R.id.easyRadioBtn:
                    newDifficultyLevel = DifficultyLevel.easy;
                    break;
                case R.id.mediumRadioBtn:
                    newDifficultyLevel = DifficultyLevel.medium;
                    break;
                case R.id.hardRadioBtn:
                    newDifficultyLevel = DifficultyLevel.hard;
                    break;
                }
                editor.putInt(GameUtils.GAME_PREFS_KEY_DIFFICULTY, newDifficultyLevel.ordinal());
                editor.commit();
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "difficulty_" + newDifficultyLevel.name());
            }
        });

        mRadioMusicvolume = (RadioGroup) findViewById(R.id.musicVolume);
        // update radio buttons states according to the music preference
        int musicVolume = mSharedPrefs.getInt(GameUtils.GAME_PREFS_KEY_MUSIC_VOLUME, 0);
        ((RadioButton) mRadioMusicvolume.getChildAt(musicVolume)).setChecked(true);
        mRadioMusicvolume.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // update game difficulty in preferences
                MusicState newMusicState = null;
                Editor editor = mSharedPrefs.edit();
                switch (checkedId) {
                case R.id.musicOff:
                    newMusicState = MusicState.off;
                    break;
                case R.id.musicOn:
                    newMusicState = MusicState.on;
                    break;
                }
                editor.putInt(GameUtils.GAME_PREFS_KEY_MUSIC_VOLUME, newMusicState.ordinal());
                editor.commit();
                GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "music_" + newMusicState.name());
            }
        });

        // setup background video
        mBackgroundVideoView = (VideoView) findViewById(R.id.backgroundVideo);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_bg_home);
        mBackgroundVideoView.setVideoURI(videoUri);
        mBackgroundVideoView.setOnPreparedListener(mPreparedListener);

        mAboutButton = (Button) findViewById(R.id.aboutButton);
        mAboutButton.setOnClickListener(this);

        mRateAppButton = (Button) findViewById(R.id.rateButton);
        mRateAppButton.setOnClickListener(this);
    }

    private void goToBattleChooserActivity() {
        startActivity(new Intent(this, BattleChooserActivity.class));
        finish();
        GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.ui_action, EventAction.button_press,
                "go_battle");
    }

    private void openAboutDialog() {
        mAboutDialog = new Dialog(this, R.style.Dialog);
        mAboutDialog.setCancelable(true);
        mAboutDialog.setContentView(R.layout.dialog_about);
        // activate the dialog links
        TextView creditsTV = (TextView) mAboutDialog.findViewById(R.id.aboutCredits);
        creditsTV.setMovementMethod(LinkMovementMethod.getInstance());
        TextView blogTV = (TextView) mAboutDialog.findViewById(R.id.aboutBlog);
        blogTV.setMovementMethod(LinkMovementMethod.getInstance());
        TextView contactTV = (TextView) mAboutDialog.findViewById(R.id.aboutContact);
        contactTV.setMovementMethod(LinkMovementMethod.getInstance());
        TextView sourcesTV = (TextView) mAboutDialog.findViewById(R.id.aboutSources);
        sourcesTV.setMovementMethod(LinkMovementMethod.getInstance());
        mAboutDialog.show();
    }

    private void showButton(View view, boolean fromRight) {
        if (fromRight) {
            view.startAnimation(mMainButtonAnimationRightIn);
        } else {
            view.startAnimation(mMainButtonAnimationLeftIn);
        }
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
    }

    private void hideButton(View view, boolean toRight) {
        if (toRight) {
            view.startAnimation(mMainButtonAnimationRightOut);
        } else {
            view.startAnimation(mMainButtonAnimationLeftOut);
        }
        view.setVisibility(View.GONE);
        view.setEnabled(false);
    }

    private void showMainHomeButtons() {
        mScreenState = ScreenState.HOME;
        mBackButton.startAnimation(mFadeOutAnimation);
        mBackButton.setVisibility(View.GONE);
        mAppNameView.startAnimation(mFadeInAnimation);
        mAppNameView.setVisibility(View.VISIBLE);
        mShareButton.startAnimation(mFadeInAnimation);
        mShareButton.setVisibility(View.VISIBLE);
        mSupportButton.startAnimation(mFadeInAnimation);
        mSupportButton.setVisibility(View.VISIBLE);
        showButton(mPlayButton, true);
        showButton(mTutorialButton, false);
        showButton(mSettingsButton, true);
    }

    private void hideMainHomeButtons() {
        mAppNameView.startAnimation(mFadeOutAnimation);
        mAppNameView.setVisibility(View.GONE);
        mShareButton.startAnimation(mFadeOutAnimation);
        mShareButton.setVisibility(View.GONE);
        mSupportButton.startAnimation(mFadeOutAnimation);
        mSupportButton.setVisibility(View.GONE);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.startAnimation(mFadeInAnimation);
        hideButton(mPlayButton, true);
        hideButton(mTutorialButton, false);
        hideButton(mSettingsButton, true);
    }

    private void showSettings() {
        mScreenState = ScreenState.SETTINGS;
        mSettingsLayout.setVisibility(View.VISIBLE);
        mSettingsLayout.startAnimation(mFadeInAnimation);
        hideMainHomeButtons();
    }

    private void hideSettings() {
        mSettingsLayout.setVisibility(View.GONE);
        mSettingsLayout.startAnimation(mFadeOutAnimation);
    }

    private void showResumeGameDialog(final Battle savedGame) {
        // ask user if he wants to resume a saved game
        Dialog dialog = new CustomAlertDialog(this, R.style.Dialog, getString(R.string.resume_saved_battle,
                getString(savedGame.getName())), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == R.id.okButton) {
                    // load game
                    Intent i = new Intent(HomeActivity.this, GameActivity.class);
                    Bundle extras = new Bundle();
                    extras.putLong("game_id", savedGame.getId());
                    i.putExtras(extras);
                    dialog.dismiss();
                    startActivity(i);
                    finish();
                } else {
                    // create new battle
                    dialog.dismiss();
                    goToBattleChooserActivity();
                }
            }
        });
        dialog.show();
    }

    private void showTutorialDialog() {
        // ask user if he wants to do the tutorial as he is a noob
        Dialog dialog = new CustomAlertDialog(this, R.style.Dialog, getString(R.string.ask_tutorial),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == R.id.okButton) {
                            // go to tutorial
                            // Intent intent = new Intent(HomeActivity.this,
                            // TutorialActivity.class);
                            // dialog.dismiss();
                            // startActivity(intent);
                            // finish();
                        } else {
                            // create new battle
                            dialog.dismiss();
                            goToBattleChooserActivity();
                        }
                    }
                });
        dialog.show();
        mSharedPrefs.edit().putInt(GameUtils.TUTORIAL_DONE, 1).commit();
    }

    @Override
    public void onBillingServiceConnected() {
    }

}
