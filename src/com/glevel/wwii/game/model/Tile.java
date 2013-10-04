package com.glevel.wwii.game.model;

public class Tile {
	
	public int getxPosition() {
		return xPosition;
	}
	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	public int getyPosition() {
		return yPosition;
	}
	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	public GameElement getContent() {
		return content;
	}
	public void setContent(GameElement content) {
		this.content = content;
	}
	public GameElement getTerrain() {
		return terrain;
	}
	public void setTerrain(GameElement terrain) {
		this.terrain = terrain;
	}
	public GroundType getGround() {
		return ground;
	}
	public void setGround(GroundType ground) {
		this.ground = ground;
	}
	public enum GroundType {
		grass, beton, sand, concrete
	}
	
	private int xPosition;
	private int yPosition;
	private GameElement content = null;
	private GameElement terrain = null;
	private GroundType ground;

}
