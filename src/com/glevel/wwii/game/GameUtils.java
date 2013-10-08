package com.glevel.wwii.game;

public class GameUtils {

	public static final String GAME_PREFS_FILENAME = "game_settings";
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

}
