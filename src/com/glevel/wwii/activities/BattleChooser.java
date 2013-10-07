package com.glevel.wwii.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.glevel.wwii.R;
import com.glevel.wwii.adapters.MapPagerAdapter;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.utils.WWActivity;
import com.glevel.wwii.views.CustomRadioButton;

public class BattleChooser extends WWActivity {

	private RadioGroup mRadioGroupArmy;
	private ViewPager mMapsCarousel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_battle_chooser);
		setupUI();

	}

	private void setupUI() {
		// init armies chooser
		mRadioGroupArmy = (RadioGroup) findViewById(R.id.radioGroupArmy);
		for (ArmiesData army : ArmiesData.values()) {
			addArmyRadioButton(army);
		}
		// checks first radio button
		((CompoundButton) mRadioGroupArmy.getChildAt(0)).setChecked(true);

		// init maps carousel
		mMapsCarousel = (ViewPager) findViewById(R.id.lstMaps);
		PagerAdapter pagerAdapter = new MapPagerAdapter(onMapSelectedListener);
		mMapsCarousel.setAdapter(pagerAdapter);
	}

	private void addArmyRadioButton(ArmiesData army) {
		CustomRadioButton radioBtn = (CustomRadioButton) getLayoutInflater().inflate(R.layout.radio_army, null);
		radioBtn.setId(army.ordinal());
		radioBtn.setText(army.getName());
		radioBtn.setCompoundDrawablesWithIntrinsicBounds(army.getFlagImage(), 0, 0, 0);
		mRadioGroupArmy.addView(radioBtn);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private OnClickListener onMapSelectedListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(BattleChooser.this, ArmyBuilder.class);
			intent.putExtra(ArmyBuilder.EXTRA_ARMY, mRadioGroupArmy.getCheckedRadioButtonId());
			intent.putExtra(ArmyBuilder.EXTRA_MAP, (Integer) v.getTag(R.string.id));
			startActivity(intent);
		}
	};

}
