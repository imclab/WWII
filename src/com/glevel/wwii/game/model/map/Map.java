package com.glevel.wwii.game.model.map;

public class Map {

    private Tile[][] tiles;
    private final boolean isAllyLeftSide = true;

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

    public boolean isAllyLeftSide() {
        return isAllyLeftSide;
    }

}
