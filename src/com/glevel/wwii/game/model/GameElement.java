package com.glevel.wwii.game.model;

import org.andengine.entity.sprite.Sprite;

import com.glevel.wwii.game.model.map.Tile;

public abstract class GameElement {

    protected final int name;
    private final String spriteName;
    private Tile tilePosition;
    protected Sprite sprite;

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public GameElement(int name, String spriteName) {
        this.name = name;
        this.spriteName = spriteName;
    }

    public int getName() {
        return name;
    }

    public Tile getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(Tile tilePosition) {
        this.tilePosition = tilePosition;
    }

    public String getSpriteName() {
        return spriteName;
    }

}
