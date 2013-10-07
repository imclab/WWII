package com.glevel.wwii.activities;

import android.content.Intent;
import android.os.Bundle;

import com.glevel.wwii.R;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.BattlesData;
import com.glevel.wwii.utils.WWActivity;

public class ArmyBuilder extends WWActivity {

	public static final String EXTRA_ARMY = "armyId", EXTRA_MAP = "mapId";

	private ArmiesData mPlayerArmy;
	private BattlesData mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get intent extras
		Intent intent = getIntent();
		mPlayerArmy = ArmiesData.values()[intent.getIntExtra(EXTRA_ARMY, 0)];
		mMap = BattlesData.values()[intent.getIntExtra(EXTRA_MAP, 0)];

		setContentView(R.layout.activity_army_builder);
		setupUI();

	}

	private void setupUI() {

	}

}
