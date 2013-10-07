package com.glevel.wwii.game.model.map;

import java.util.List;

import com.glevel.wwii.game.model.GameElement;

public class Map {

	private List<GameElement> terrainTiles;
	private Tile[][] tiles;

	public List<GameElement> getTerrain() {
		return terrainTiles;
	}

	public void setTerrain(List<GameElement> terrain) {
		this.terrainTiles = terrain;
	}

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tiles) {
		this.tiles = tiles;
	}
	
	public int getWidth() {
		return tiles[0].length;
	}
	
	public int getHeight() {
		return tiles.length;
	}

}
