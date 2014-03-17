package com.glevel.wwii.activities;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.glevel.wwii.R;
import com.glevel.wwii.game.GameGUI;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.GameUtils.DifficultyLevel;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.BattlesData;
import com.glevel.wwii.game.data.UnitsData;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.Battle.Phase;
import com.glevel.wwii.game.models.Player;
import com.glevel.wwii.game.models.units.categories.Unit.Experience;

public class TutorialActivity extends GameActivity {

	private static final int[] TUTORIAL_INSTRUCTIONS = { R.string.tutorial0,
			R.string.tutorial1, R.string.tutorial2, R.string.tutorial3,
			R.string.tutorial4, R.string.tutorial5, R.string.tutorial6,
			R.string.tutorial7, R.string.tutorial8, R.string.tutorial9,
			R.string.tutorial10, R.string.tutorial11 };

	private int tutorialStep = 0;

	private Button nextButton;
	private TextView tutorialInstructions;

	@Override
	protected void initGameActivity() {
		mMustSaveGame = false;

		battle = new Battle(BattlesData.OOSTERBEEK);
		battle.setPhase(Phase.combat);

		// init human player
		Player mPlayer = new Player("Me", ArmiesData.USA, 0, false,
				battle.getPlayerVictoryCondition(ArmiesData.USA));
		mPlayer.getUnits().add(
				UnitsData.buildRifleMan(mPlayer.getArmy(), Experience.ELITE));
		mPlayer.getUnits().add(
				UnitsData.buildRifleMan(mPlayer.getArmy(), Experience.ELITE));
		mPlayer.getUnits().add(
				UnitsData.buildScout(mPlayer.getArmy(), Experience.ELITE));
		mPlayer.getUnits().add(
				UnitsData.buildHMG(mPlayer.getArmy(), Experience.VETERAN));
		mPlayer.getUnits().add(
				UnitsData.buildATCannon(mPlayer.getArmy(), Experience.VETERAN));
		mPlayer.getUnits()
				.add(UnitsData.buildShermanM4A1(mPlayer.getArmy(),
						Experience.ELITE));
		battle.getPlayers().add(mPlayer);

		// create AI's player
		Player enemyPlayer = new Player("Enemy", ArmiesData.GERMANY, battle
				.getPlayers().size(), false,
				battle.getPlayerVictoryCondition(ArmiesData.GERMANY));
		enemyPlayer.getUnits().add(
				UnitsData.buildRifleMan(enemyPlayer.getArmy(),
						Experience.RECRUIT));
		enemyPlayer.getUnits().add(
				UnitsData.buildRifleMan(enemyPlayer.getArmy(),
						Experience.RECRUIT));
		battle.getPlayers().add(enemyPlayer);

		battle.setDifficultyLevel(DifficultyLevel.values()[mSharedPrefs.getInt(
				GameUtils.GAME_PREFS_KEY_DIFFICULTY,
				DifficultyLevel.medium.ordinal())]);
		battle.setOnNewSprite(this);
		battle.setOnNewSoundToPlay(this);

		// init GUI
		mGameGUI = new GameGUI(this);
		mGameGUI.showLoadingScreen();
		mGameGUI.initGUI();

		setupTutorialUI();
	}

	@Override
	protected int getLayoutID() {
		return R.layout.activity_tutorial;
	}

	private void setupTutorialUI() {
		nextButton = (Button) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToNextStep();
			}
		});
		tutorialInstructions = (TextView) findViewById(R.id.tutorialInstructions);
		tutorialInstructions.setText(TUTORIAL_INSTRUCTIONS[tutorialStep]);
	}

	private void goToNextStep() {
		tutorialStep++;
		tutorialInstructions.setText(TUTORIAL_INSTRUCTIONS[tutorialStep]);
		if (tutorialStep >= TUTORIAL_INSTRUCTIONS.length - 1) {
			nextButton.setVisibility(View.GONE);
		}
	}

	@Override
	public void goToReport(boolean victory) {
		startActivity(new Intent(this, HomeActivity.class));
		finish();
	}

}
