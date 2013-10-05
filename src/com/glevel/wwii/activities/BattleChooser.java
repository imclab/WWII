package com.glevel.wwii.activities;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.glevel.wwii.R;
import com.glevel.wwii.utils.WWActivity;

public class BattleChooser extends WWActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_battle_chooser);
		setupUI();

	}

	private void setupUI() {

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

}
