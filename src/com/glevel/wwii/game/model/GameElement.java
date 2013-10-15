package com.glevel.wwii.game.model;

import android.graphics.Canvas;

import com.glevel.wwii.game.model.map.Tile;

public abstract class GameElement {

	protected final int name;
	private int xAbsolutePosition;
	private int yAbsolutePosition;
	private int orientation;

	private Tile tilePosition;

	public GameElement(int name) {
		this.name = name;
	}

	public int getName() {
		return name;
	}

	public int getxAbsolutePosition() {
		return xAbsolutePosition;
	}

	public void setxAbsolutePosition(int xAbsolutePosition) {
		this.xAbsolutePosition = xAbsolutePosition;
	}

	public int getyAbsolutePosition() {
		return yAbsolutePosition;
	}

	public void setyAbsolutePosition(int yAbsolutePosition) {
		this.yAbsolutePosition = yAbsolutePosition;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public Tile getTilePosition() {
		return tilePosition;
	}

	public void setTilePosition(Tile tilePosition) {
		this.tilePosition = tilePosition;
	}

	public abstract void draw(Canvas canvas);

}
