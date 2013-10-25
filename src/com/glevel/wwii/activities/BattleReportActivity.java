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
import com.glevel.wwii.analytics.GoogleAnalyticsHandler;
import com.glevel.wwii.analytics.GoogleAnalyticsHandler.EventAction;
import com.glevel.wwii.analytics.GoogleAnalyticsHandler.EventCategory;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.utils.WWActivity;

public class BattleReportActivity extends WWActivity {

    private boolean mIsVictory = false;
    private Battle battle;
    private Button mLeaveReportBtn;

    private OnClickListener onLeaveReportClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action, EventAction.button_press,
                    "leave_battle_report");
            leaveReport();
        }
    };
    private ListView mMyArmyList;
    private ListView mEnemyArmyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bundle extras = getIntent().getExtras();
        // mIsVictory = extras.getBoolean("victory");

        // TODO test
        battle = GameUtils.createTestData();

        setContentView(R.layout.activity_battle_report);
        setupUI();

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
        GameReportUnitsArrayAdapter mMyArmyAdapter = new GameReportUnitsArrayAdapter(this, R.layout.army_list_item, battle
                .getMe().getUnits(), true);
        mMyArmyList.setAdapter(mMyArmyAdapter);

        // init enemy's army list
        mEnemyArmyList = (ListView) findViewById(R.id.enemyArmyList);
        GameReportUnitsArrayAdapter mEnemyArmyAdapter = new GameReportUnitsArrayAdapter(this, R.layout.army_list_item,
                battle.getEnemies(battle.getMe().getArmy()), true);
        mEnemyArmyList.setAdapter(mEnemyArmyAdapter);
    }

    @Override
    public void onBackPressed() {
        GoogleAnalyticsHandler.sendEvent(getApplicationContext(), EventCategory.ui_action, EventAction.button_press,
                "battle_report_back_pressed");
        leaveReport();
    }

    private void leaveReport() {
        // the user is playing a campaign
        // TODO
        // if (false) {
        // // go to campaign screen
        // } else {
        // go to home screen
        startActivity(new Intent(BattleReportActivity.this, HomeActivity.class));
        // }
        finish();
    }
}
