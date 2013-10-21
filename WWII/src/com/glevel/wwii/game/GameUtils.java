package com.glevel.wwii.game;

import com.glevel.wwii.game.model.GameElement;

public class GameUtils {

    public static final String GAME_PREFS_FILENAME = "com.glevel.wwii";
    public static final String GAME_PREFS_KEY_DIFFICULTY = "game_difficulty";
    public static final String GAME_PREFS_KEY_MUSIC_VOLUME = "game_music_volume";

    public static enum DifficultyLevel {
        easy, medium, hard
    }

    public static enum MusicState {
        off, on
    }

    public static final int MAX_UNIT_PER_ARMY = 8;
    public static final float SELL_PRICE_FACTOR = 0.5f;

    public static float getDistanceBetween(GameElement g1, GameElement g2) {
        return (float) Math.sqrt((Math.pow(Math.abs(g1.getSprite().getX() - g2.getSprite().getX()), 2) + Math.pow(
                Math.abs(g1.getSprite().getY() - g2.getSprite().getY()), 2)));
    }
}
