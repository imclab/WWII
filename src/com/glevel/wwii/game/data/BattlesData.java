package com.glevel.wwii.game.data;

import com.glevel.wwii.R;

public enum BattlesData {
	ARNHEM_BRIDGE(R.string.about, R.drawable.map_0, 0), ARNHEM_BRIDGE2(R.string.about, R.drawable.ic_launcher, 0), ARNHEM_BRIDGE3(
	        R.string.about, R.drawable.ic_launcher, 0);

	private final int name;
	private final int image;
	private final int requisition;

	BattlesData(int name, int image, int requisition) {
		this.name = name;
		this.image = image;
		this.requisition = requisition;
	}

	public int getName() {
		return name;
	}

	public int getImage() {
		return image;
	}

	public int getRequisition() {
		return requisition;
	}
}
