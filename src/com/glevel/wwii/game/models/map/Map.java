package com.glevel.wwii.game.models.map;

import org.andengine.extension.tmx.TMXLayer;

public class Map {

    private Tile[][] tiles;
    private TMXLayer tmxLayer;

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

    public TMXLayer getTmxLayer() {
        return tmxLayer;
    }

    public void setTmxLayer(TMXLayer tmxLayer) {
        this.tmxLayer = tmxLayer;
    }

}
