package com.glevel.wwii.utils;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class WWActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// allow user to change the music volume with his phone's buttons
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

}
