package com.glevel.wwii.game;

import java.util.ArrayList;
import java.util.List;

import org.andengine.extension.tmx.TMXLayer;

import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.map.Map;
import com.glevel.wwii.game.model.map.Tile;
import com.glevel.wwii.game.model.map.Tile.TerrainType;

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

    private static final int STEP = 30;

    public static boolean canSee(Map map, GameElement g1, GameElement g2, TMXLayer tmxLayer) {
        float dx = g2.getSprite().getX() - g1.getSprite().getX();
        float dy = g2.getSprite().getY() - g1.getSprite().getY();
        double angle = Math.atan(dy / dx);
        float dd = STEP;
        boolean hasArrived = false;

        float x = g1.getSprite().getX(), y = g1.getSprite().getY();

        List<TerrainType> lstTerrain = new ArrayList<TerrainType>();

        while (!hasArrived) {
            // go one step further
            if (dx > 0) {
                x += dd * Math.cos(angle);
                y += dd * Math.sin(angle);
            } else {
                x += -dd * Math.cos(angle);
                y += dd * Math.sin(angle + Math.PI);
            }

            dx = g2.getSprite().getX() - x;
            dy = g2.getSprite().getY() - y;

            // check if can see
            Tile t = map.getTiles()[tmxLayer.getTMXTileAt(x, y).getTileRow()][tmxLayer.getTMXTileAt(x, y)
                    .getTileColumn()];
            if (t.getContent() != null && Math.sqrt(dx * dx + dy * dy) > 150) {
                return false;
            } else if (t.getTerrain() != null && lstTerrain.size() > 0
                    && t.getTerrain() != lstTerrain.get(lstTerrain.size() - 1) && Math.sqrt(dx * dx + dy * dy) > 200) {
                return false;
            } else if (t.getTerrain() != null
                    && (lstTerrain.size() == 0 || t.getTerrain() != lstTerrain.get(lstTerrain.size() - 1))) {
                lstTerrain.add(t.getTerrain());
            }

            if (Math.sqrt(dx * dx + dy * dy) <= dd) {
                hasArrived = true;
            }
        }

        return true;
    }
}
