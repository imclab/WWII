package com.glevel.wwii.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.utils.ApplicationUtils;
import com.glevel.wwii.utils.WWActivity;

public class Home extends WWActivity implements OnClickListener {

	private static enum ScreenState {
		home, solo, settings
	}

	private SharedPreferences mSharedPrefs;

	private Animation mMainButtonAnimationRightIn,
			mMainButtonAnimationRightOut, mMainButtonAnimationLeftIn,
			mMainButtonAnimationLeftOut;
	private Animation mFadeOutAnimation, mFadeInAnimation;

	private Button mSoloButton, mMultiplayerButton, mSettingsButton,
			mCampaignButton, mBattleModeButton, mAboutButton;
	private ViewGroup mSettingsLayout;
	private View mBackButton;
	private RadioGroup mRadioMusicvolume, mRadioDifficulty;
	private VideoView mBackgroundVideoView;
	private Dialog mAboutDialog = null;

	private ScreenState mScreenState = ScreenState.home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home);
		setupUI();

		ApplicationUtils.showRateDialogIfNeeded(this);
	}

	private void setupUI() {
		mSharedPrefs = getSharedPreferences(GameUtils.GAME_PREFS_FILENAME,
				MODE_PRIVATE);

		mSettingsLayout = (ViewGroup) findViewById(R.id.settingsLayout);

		mMainButtonAnimationRightIn = AnimationUtils.loadAnimation(this,
				R.anim.main_btn_right_in);
		mMainButtonAnimationLeftIn = AnimationUtils.loadAnimation(this,
				R.anim.main_btn_left_in);
		mMainButtonAnimationRightOut = AnimationUtils.loadAnimation(this,
				R.anim.main_btn_right_out);
		mMainButtonAnimationLeftOut = AnimationUtils.loadAnimation(this,
				R.anim.main_btn_left_out);
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
		int gameDifficulty = mSharedPrefs.getInt(
				GameUtils.GAME_PREFS_KEY_DIFFICULTY,
				GameUtils.DifficultyLevel.medium.ordinal());
		((RadioButton) mRadioDifficulty.getChildAt(gameDifficulty))
				.setChecked(true);
		mRadioDifficulty
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// update game difficulty in preferences
						Editor editor = mSharedPrefs.edit();
						switch (checkedId) {
						case R.id.easyRadioBtn:
							editor.putInt(GameUtils.GAME_PREFS_KEY_DIFFICULTY,
									GameUtils.DifficultyLevel.easy.ordinal());
							break;
						case R.id.mediumRadioBtn:
							editor.putInt(GameUtils.GAME_PREFS_KEY_DIFFICULTY,
									GameUtils.DifficultyLevel.medium.ordinal());
							break;
						case R.id.hardRadioBtn:
							editor.putInt(GameUtils.GAME_PREFS_KEY_DIFFICULTY,
									GameUtils.DifficultyLevel.hard.ordinal());
							break;
						}
						editor.commit();
					}
				});

		mRadioMusicvolume = (RadioGroup) findViewById(R.id.musicVolume);
		// update radio buttons states according to the music preference
		int musicVolume = mSharedPrefs.getInt(
				GameUtils.GAME_PREFS_KEY_MUSIC_VOLUME, 0);
		((RadioButton) mRadioMusicvolume.getChildAt(musicVolume))
				.setChecked(true);
		mRadioMusicvolume
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// update game difficulty in preferences
						Editor editor = mSharedPrefs.edit();
						switch (checkedId) {
						case R.id.musicOff:
							editor.putInt(
									GameUtils.GAME_PREFS_KEY_MUSIC_VOLUME,
									GameUtils.MusicState.off.ordinal());
							break;
						case R.id.musicOn:
							editor.putInt(
									GameUtils.GAME_PREFS_KEY_MUSIC_VOLUME,
									GameUtils.MusicState.on.ordinal());
							break;
						}
						editor.commit();
					}
				});

		// setup background video
		mBackgroundVideoView = (VideoView) findViewById(R.id.backgroundVideo);
		Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/"
				+ R.raw.video_bg_home);
		mBackgroundVideoView.setVideoURI(videoUri);
		mBackgroundVideoView.start();

		mAboutButton = (Button) findViewById(R.id.aboutButton);
		mAboutButton.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		showMainHomeButtons();
	}

	@Override
	protected void onPause() {
		mBackgroundVideoView.stopPlayback();
		if (mAboutDialog != null) {
			mAboutDialog.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v.isShown()) {
			switch (v.getId()) {
			case R.id.soloButton:
				showSoloButtons();
				break;
			case R.id.multiplayerButton:
				Toast.makeText(Home.this, "Coming soon...", Toast.LENGTH_LONG)
						.show();
				break;
			case R.id.settingsButton:
				showSettings();
				break;
			case R.id.backButton:
				onBackPressed();
				break;
			case R.id.aboutButton:
				openAboutDialog();
				break;
			default:
				break;
			}
		}
	}

	private void openAboutDialog() {
		mAboutDialog = new Dialog(this, R.style.FullScreenDialog);
		mAboutDialog.setCancelable(true);
		mAboutDialog.setContentView(R.layout.dialog_about);
		// activate the dialog links
		TextView creditsTV = (TextView) mAboutDialog
				.findViewById(R.id.aboutCredits);
		creditsTV.setMovementMethod(LinkMovementMethod.getInstance());
		TextView blogTV = (TextView) mAboutDialog.findViewById(R.id.aboutBlog);
		blogTV.setMovementMethod(LinkMovementMethod.getInstance());
		mAboutDialog.show();
	}

	private void showSoloButtons() {
		mScreenState = ScreenState.solo;
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
		mScreenState = ScreenState.home;
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
		mScreenState = ScreenState.settings;
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
		case home:
			super.onBackPressed();
			break;
		case solo:
			showMainHomeButtons();
			hideSoloButtons();
			break;
		case settings:
			showMainHomeButtons();
			hideSettings();
			break;
		default:
			break;
		}

	}

}
