package com.glevel.wwii.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.glevel.wwii.MyActivity;
import com.glevel.wwii.R;
import com.glevel.wwii.activities.adapters.MapPagerAdapter;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.utils.ApplicationUtils;

public class BattleChooserActivity extends MyActivity {

    private RadioGroup mRadioGroupArmy;
    private ViewPager mMapsCarousel;
    private ImageView mAllyPovBackground;

    private Runnable mStormEffect;

    private boolean isMapClicked;// avoid view pager's multiple
    // selection

    /**
     * Callbacks
     */
    private OnClickListener onMapSelectedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isMapClicked) {
                isMapClicked = true;
                Intent intent = new Intent(BattleChooserActivity.this, ArmyBuilderActivity.class);
                intent.putExtra(ArmyBuilderActivity.EXTRA_ARMY,
                        (mRadioGroupArmy.getCheckedRadioButtonId() == R.id.usa_army ? ArmiesData.USA.ordinal()
                                : ArmiesData.GERMANY.ordinal()));
                intent.putExtra(ArmyBuilderActivity.EXTRA_MAP, (Integer) v.getTag(R.string.id));
                startActivity(intent);
            }
        }
    };
    private ImageView mGermanPovBackground;
    private Animation fadeOutAnimation, fadeInAnimation;
    private Animation paraAnim;
    private Animation groundAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_battle_chooser);
        setupUI();
    }

    private void setupUI() {
        // init armies chooser
        mRadioGroupArmy = (RadioGroup) findViewById(R.id.radioGroupArmy);

        // init maps carousel
        mMapsCarousel = (ViewPager) findViewById(R.id.lstMaps);
        PagerAdapter pagerAdapter = new MapPagerAdapter(onMapSelectedListener);
        mMapsCarousel.setAdapter(pagerAdapter);

        // prepare background animations
        fadeInAnimation = AnimationUtils.loadAnimation(BattleChooserActivity.this, R.anim.fade_in);
        fadeInAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mGermanPovBackground.startAnimation(groundAnim);
            }
        });
        fadeOutAnimation = AnimationUtils.loadAnimation(BattleChooserActivity.this, R.anim.fade_out);
        fadeOutAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAllyPovBackground.setVisibility(View.VISIBLE);
                mGermanPovBackground.setVisibility(View.GONE);
                mGermanPovBackground.setAnimation(null);
            }
        });
        paraAnim = AnimationUtils.loadAnimation(this, R.anim.paratrooper_effect);
        groundAnim = AnimationUtils.loadAnimation(this, R.anim.ground_troops_effect);

        mAllyPovBackground = (ImageView) findViewById(R.id.allyPovBackground);
        mGermanPovBackground = (ImageView) findViewById(R.id.germanPovBackground);

        // stretch the background image a bit to fill all the screen while
        // rotating
        Point screenSize = ApplicationUtils.getScreenDimensions(this);
        android.widget.FrameLayout.LayoutParams layoutParams = new android.widget.FrameLayout.LayoutParams(
                screenSize.x + 100, screenSize.y + 100);
        layoutParams.setMargins(-50, -50, 0, 0);
        mAllyPovBackground.setLayoutParams(layoutParams);
        mGermanPovBackground.setLayoutParams(layoutParams);

        mAllyPovBackground.startAnimation(paraAnim);
        mRadioGroupArmy.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.german_army) {
                    mGermanPovBackground.startAnimation(fadeInAnimation);
                    mGermanPovBackground.setVisibility(View.VISIBLE);
                    mAllyPovBackground.removeCallbacks(mStormEffect);
                    mAllyPovBackground.setAnimation(null);
                    mAllyPovBackground.setVisibility(View.GONE);
                    mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mGermanPovBackground, 100, 0);
                } else {
                    mGermanPovBackground.startAnimation(fadeOutAnimation);
                    mAllyPovBackground.setAnimation(paraAnim);
                    mGermanPovBackground.removeCallbacks(mStormEffect);
                    mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mAllyPovBackground, 150, 50);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        isMapClicked = false;

        // init storm effect
        mStormEffect = ApplicationUtils.addStormBackgroundAtmosphere(mAllyPovBackground, 150, 50);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAllyPovBackground.removeCallbacks(mStormEffect);
        mGermanPovBackground.removeCallbacks(mStormEffect);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}
