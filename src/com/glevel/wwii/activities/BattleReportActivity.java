package com.glevel.wwii.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.glevel.wwii.R;
import com.glevel.wwii.activities.adapters.GameReportUnitsArrayAdapter;
import com.glevel.wwii.database.DatabaseHelper;
import com.glevel.wwii.game.GameConverterHelper;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.ObjectivePoint;
import com.glevel.wwii.game.models.units.Tank;
import com.glevel.wwii.game.models.units.categories.Unit;
import com.glevel.wwii.game.models.units.categories.Unit.InjuryState;
import com.glevel.wwii.utils.MusicManager;
import com.glevel.wwii.utils.MusicManager.Music;
import com.google.android.gms.games.GamesClient;
import com.google.example.games.basegameutils.BaseGameActivity;

public class BattleReportActivity extends BaseGameActivity {

	private DatabaseHelper mDbHelper;
	private boolean mIsVictory = false;
	private Battle battle;

	private Button mLeaveReportBtn;
	private ListView mMyArmyList;
	private ListView mEnemyArmyList;

	/**
	 * Callbacks
	 */
	private OnClickListener onLeaveReportClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			MusicManager.playSound(getApplicationContext(), R.raw.button_sound);
			leaveReport();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMusic = Music.MUSIC_CAMPAIGN;

		mDbHelper = new DatabaseHelper(getApplicationContext());

		// get battle info
		Bundle extras = getIntent().getExtras();
		battle = mDbHelper.getBattleDao().getById(extras.getLong("game_id"));
		if (battle == null) {
			leaveReport();
			return;
		}

		mIsVictory = extras.getBoolean("victory");

		// erase saved game from database
		GameConverterHelper.deleteSavedBattles(mDbHelper, battle.getCampaignId());

		setContentView(R.layout.activity_battle_report);
		setupUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	@Override
	public void onBackPressed() {
		leaveReport();
	}

	private void setupUI() {
		// setup background victory label
		TextView victoryLabel = (TextView) findViewById(R.id.victoryLabel);
		if (mIsVictory) {
			victoryLabel.setText(R.string.victory);
			victoryLabel.setTextColor(getResources().getColor(R.color.green));
		} else {
			victoryLabel.setText(R.string.defeat);
			victoryLabel.setTextColor(getResources().getColor(R.color.red));
		}

		// init army flag
		TextView viewTitle = (TextView) findViewById(R.id.title);
		viewTitle.setCompoundDrawablesWithIntrinsicBounds(battle.getMe().getArmy().getFlagImage(), 0, 0, 0);

		// init leave report button
		mLeaveReportBtn = (Button) findViewById(R.id.leaveReport);
		mLeaveReportBtn.setOnClickListener(onLeaveReportClicked);

		// init my army list
		mMyArmyList = (ListView) findViewById(R.id.myArmyList);
		GameReportUnitsArrayAdapter mMyArmyAdapter = new GameReportUnitsArrayAdapter(this, R.layout.army_list_item, battle.getMe().getUnits(), true);
		mMyArmyList.setAdapter(mMyArmyAdapter);

		// init enemy's army list
		mEnemyArmyList = (ListView) findViewById(R.id.enemyArmyList);
		GameReportUnitsArrayAdapter mEnemyArmyAdapter = new GameReportUnitsArrayAdapter(this, R.layout.army_list_item, battle.getEnemies(battle.getMe()
				.getArmy()), true);
		mEnemyArmyList.setAdapter(mEnemyArmyAdapter);
	}

	private void leaveReport() {
		// go to home screen
		startActivity(new Intent(BattleReportActivity.this, HomeActivity.class));
		finish();
	}

	@Override
	public void onSignInFailed() {
	}

	@Override
	public void onSignInSucceeded() {
		GamesClient gameClient = getGamesClient();
		// unlock achievements
		if (mIsVictory) {
			gameClient.incrementAchievement(getString(R.string.achievement_skillful_general), 1);

			int nbTanksDestroyed = 0;
			// iterate through enemy units
			for (Unit enemyUnit : battle.getEnemyPlayer(battle.getMe()).getUnits()) {
				if (enemyUnit instanceof Tank) {
					nbTanksDestroyed++;
				}
			}

			if (nbTanksDestroyed > 0) {
				gameClient.incrementAchievement(getString(R.string.achievement_tanks_terror), nbTanksDestroyed);
			}

			// iterate through my units
			boolean carefulTactician = true;
			for (Unit unit : battle.getMe().getUnits()) {
				if (unit.getHealth() == InjuryState.DEAD) {
					carefulTactician = false;
				}
				if (unit.getFrags() >= 10) {
					gameClient.unlockAchievement(getString(R.string.achievement_careful_tactician));
				}
			}

			if (carefulTactician) {
				gameClient.unlockAchievement(getString(R.string.achievement_careful_tactician));
			}

			// iterate through the objective points
			boolean brilliantTactician = true;
			for (ObjectivePoint objective : battle.getLstObjectives()) {
				if (objective.getOwner() != battle.getMe().getArmy()) {
					brilliantTactician = false;
					return;
				}
			}

			if (brilliantTactician) {
				gameClient.incrementAchievement(getString(R.string.achievement_brilliant_tactician), 1);
			}

		}
	}
}
