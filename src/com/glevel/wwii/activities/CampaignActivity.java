package com.glevel.wwii.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.glevel.wwii.R;
import com.glevel.wwii.database.DatabaseHelper;
import com.glevel.wwii.game.model.Campaign;
import com.glevel.wwii.game.model.Operation;
import com.glevel.wwii.utils.WWActivity;

public class CampaignActivity extends WWActivity implements OnClickListener {

    private DatabaseHelper mDbHelper;
    private Campaign mCampaign;
    private Operation mCurrentOperation;
    private Dialog mGameMenuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new DatabaseHelper(getApplicationContext());

        // get campaign from intent extras
        Bundle extras = getIntent().getExtras();
        long campaignId = extras.getLong("campaign_id", 0);
        mCampaign = mDbHelper.getCampaignDao().getById(campaignId);

        // get current operation
        mCurrentOperation = mCampaign.getCurrentOperation();
        if (mCurrentOperation == null) {
            // campaign is over
            showCampaignFinalReport();
            return;
        }

        setContentView(R.layout.activity_campaign);
        setupUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGameMenuDialog != null) {
            mGameMenuDialog.dismiss();
        }
        // save campaign
        mDbHelper.getCampaignDao().save(mCampaign);
    }

    @Override
    public void onBackPressed() {
        openGameMenu();
    }

    @Override
    public void onClick(View v) {

    }

    private void setupUI() {
        // campaign title
        TextView campaignTitle = (TextView) findViewById(R.id.title);
        campaignTitle.setText(getString(mCampaign.getName()) + " 1 / " + mCampaign.getOperations().size());
        campaignTitle.setCompoundDrawablesWithIntrinsicBounds(mCampaign.getArmy().getFlagImage(), 0, 0, 0);

        // current objectives points
        TextView objectivePointsTv = (TextView) findViewById(R.id.objectivePoints);
        objectivePointsTv.setText(getString(R.string.objective_points, mCurrentOperation.getCurrentPoints(),
                mCurrentOperation.getObjectivePoints()));
    }

    private void openGameMenu() {
        mGameMenuDialog = new Dialog(this, R.style.FullScreenDialog);
        mGameMenuDialog.setContentView(R.layout.dialog_campaign_game_menu);
        mGameMenuDialog.setCancelable(true);
        Animation menuButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        // resume game button
        Button resumeGameBtn = (Button) mGameMenuDialog.findViewById(R.id.resumeGameButton);
        resumeGameBtn.setAnimation(menuButtonAnimation);
        resumeGameBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameMenuDialog.dismiss();
            }
        });
        // exit button
        Button exitBtn = (Button) mGameMenuDialog.findViewById(R.id.exitButton);
        exitBtn.setAnimation(menuButtonAnimation);
        exitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CampaignActivity.this, HomeActivity.class));
                finish();
            }
        });
        mGameMenuDialog.show();
        menuButtonAnimation.start();
    }

    private void showCampaignFinalReport() {
        // TODO Auto-generated method stub
    }

}
