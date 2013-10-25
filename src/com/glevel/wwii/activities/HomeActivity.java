package com.glevel.wwii.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;
import android.widget.VideoView;

import com.glevel.wwii.R;
import com.glevel.wwii.analytics.GoogleAnalyticsHandler;
import com.glevel.wwii.analytics.GoogleAnalyticsHandler.EventAction;
import com.glevel.wwii.analytics.GoogleAnalyticsHandler.EventCategory;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.GameUtils.DifficultyLevel;
import com.glevel.wwii.game.GameUtils.MusicState;
import com.glevel.wwii.utils.ApplicationUtils;
import com.glevel.wwii.utils.WWActivity;

public class HomeActivity extends WWActivity implements OnClickListener {

    private static enum ScreenState {
        HOME, SOLO, SETTINGS
    }

    private SharedPreferences mSharedPrefs;

    private Animation mMainButtonAnimationRightIn, mMainButtonAnimationRightOut, mMainButtonAnimationLeftIn,
            mMainButtonAnimationLeftOut;
    private Animation mFadeOutAnimation, mFadeInAnimation;

    private Button mSoloButton, mMultiplayerButton, mSettingsButton, mCampaignButton, mBattleModeButton, mAboutButton,
            mRateAppButton;
    private ViewGroup mSettingsLayout;
    private View mBackButton;
    private RadioGroup mRadioMusicvolume, mRadioDifficulty;
    private VideoView mBackgroundVideoView;
    private Dialog mAboutDialog = null;

    private ScreenState mScreenState = ScreenState.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        setupUI();

        ApplicationUtils.showRateDialogIfNeeded(this);
        showMainHomeButtons();

        if (savedInstanceState != null) {
            // restart video where it had been stopped
            mBackgroundVideoView.seekTo(savedInstanceState.getInt("video_stop_position"));
        }
        mBackgroundVideoView.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store the stop position to restart the video at the correct position
        outState.putInt("video_stop_position", mBackgroundVideoView.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    private void setupUI() {
        mSharedPrefs = getSharedPreferences(GameUtils.GAME_PREFS_FILENAME, MODE_PRIVATE);

        mSettingsLayout = (ViewGroup) findViewById(R.id.settingsLayout);

        mMainButtonAnimationRightIn = AnimationUtils.loadAnimation(this, R.anim.main_btn_right_in);
        mMainButtonAnimationLeftIn = AnimationUtils.loadAnimation(this, R.anim.main_btn_left_in);
        mMainButtonAnimationRightOut = AnimationUtils.loadAnimation(this, R.anim.main_btn_right_out);
        mMainButtonAnimationLeftOut = AnimationUtils.loadAnimation(this, R.anim.main_btn_left_out);
        mFadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        mSoloButton = (Button) findViewById(R.id.soloButton);
        mSoloButton.setOnClickListener(this);

        mMultiplayerButton = (Button) findViewById(R.id.multiplayerButton);
        mMultiplayerButton.setOnClickListener(this);

        mSettingsButton = (Button) findViewById(R.id.settingsButton);
        mSettingsButton.setOnClickListener(this);

        mCampaignButton = (Button) findViewById(R.id.campaignButton);
        mCampaignButton.setOnClickListener(this);

        mBattleModeButton = (Button) findViewById(R.id.battleButton);
        mBattleModeButton.setOnClickListener(this);

        mBackButton = (Button) findViewById(R.id.backButton);
        mBackButton.setOnClickListener(this);

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
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
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
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
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

    @Override
    protected void onPause() {
        super.onPause();
        mBackgroundVideoView.pause();
        if (mAboutDialog != null) {
            mAboutDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.isShown()) {
            switch (v.getId()) {
            case R.id.soloButton:
                showSoloButtons();
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_solo");
                break;
            case R.id.multiplayerButton:
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_multi");
                break;
            case R.id.settingsButton:
                showSettings();
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_settings");
                break;
            case R.id.backButton:
                onBackPressed();
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "back_button_soft");
                break;
            case R.id.aboutButton:
                openAboutDialog();
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "show_about_dialog");
                break;
            case R.id.rateButton:
                ApplicationUtils.rateTheApp(this);
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "rate_app_button");
                break;
            case R.id.campaignButton:
                ApplicationUtils.showToast(this, R.string.coming_soon, Toast.LENGTH_SHORT);
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "go_campaign");
                break;
            case R.id.battleButton:
                startActivity(new Intent(this, BattleChooserActivity.class));
                GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                        EventAction.button_press, "go_battle");
                break;
            default:
                break;
            }
        }
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
        mAboutDialog.show();
    }

    private void showSoloButtons() {
        mScreenState = ScreenState.SOLO;
        hideMainHomeButtons();
        mCampaignButton.setVisibility(View.VISIBLE);
        mCampaignButton.startAnimation(mMainButtonAnimationLeftIn);
        mCampaignButton.setEnabled(true);
        mBattleModeButton.setVisibility(View.VISIBLE);
        mBattleModeButton.startAnimation(mMainButtonAnimationRightIn);
        mBattleModeButton.setEnabled(true);
    }

    private void hideSoloButtons() {
        mCampaignButton.startAnimation(mMainButtonAnimationLeftOut);
        mCampaignButton.setVisibility(View.GONE);
        mCampaignButton.setEnabled(false);
        mBattleModeButton.startAnimation(mMainButtonAnimationRightOut);
        mBattleModeButton.setVisibility(View.GONE);
        mBattleModeButton.setEnabled(false);
    }

    private void showMainHomeButtons() {
        mScreenState = ScreenState.HOME;
        mSoloButton.setVisibility(View.VISIBLE);
        mMultiplayerButton.setVisibility(View.VISIBLE);
        mSettingsButton.setVisibility(View.VISIBLE);
        mSoloButton.startAnimation(mMainButtonAnimationRightIn);
        mMultiplayerButton.startAnimation(mMainButtonAnimationLeftIn);
        mSettingsButton.startAnimation(mMainButtonAnimationRightIn);
        mBackButton.startAnimation(mFadeOutAnimation);
        mBackButton.setVisibility(View.GONE);
        mSoloButton.setEnabled(true);
        mMultiplayerButton.setEnabled(true);
        mSettingsButton.setEnabled(true);
    }

    private void hideMainHomeButtons() {
        mSoloButton.startAnimation(mMainButtonAnimationRightOut);
        mMultiplayerButton.startAnimation(mMainButtonAnimationLeftOut);
        mSettingsButton.startAnimation(mMainButtonAnimationRightOut);
        mSoloButton.setVisibility(View.GONE);
        mMultiplayerButton.setVisibility(View.GONE);
        mSettingsButton.setVisibility(View.GONE);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.startAnimation(mFadeInAnimation);
        mSoloButton.setEnabled(false);
        mMultiplayerButton.setEnabled(false);
        mSettingsButton.setEnabled(false);
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

    @Override
    public void onBackPressed() {
        switch (mScreenState) {
        case HOME:
            super.onBackPressed();
            break;
        case SOLO:
            showMainHomeButtons();
            hideSoloButtons();
            GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                    EventAction.button_press, "back_pressed");
            break;
        case SETTINGS:
            showMainHomeButtons();
            hideSettings();
            GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action,
                    EventAction.button_press, "back_pressed");
            break;
        default:
            break;
        }

    }

    // remove background video sound - enable video looping
    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer m) {
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

}
