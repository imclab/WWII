package com.glevel.wwii.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.glevel.wwii.WWApplication.FONTS;

public class CustomButton extends Button {

	public CustomButton(Context context) {
		super(context);
		setTypeface(FONTS.main);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FONTS.main);
	}
}
