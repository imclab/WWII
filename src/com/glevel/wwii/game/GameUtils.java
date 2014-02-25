package com.glevel.wwii.game;

import java.util.ArrayList;
import java.util.List;

import org.andengine.extension.tmx.TMXTile;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.BattlesData;
import com.glevel.wwii.game.data.UnitsData;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.Player;
import com.glevel.wwii.game.models.VictoryCondition;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.map.Tile.TerrainType;
import com.glevel.wwii.game.models.units.Unit;
import com.glevel.wwii.game.models.units.Unit.Experience;

public class GameUtils {

    public static final String GAME_PREFS_FILENAME = "com.glevel.wwii";
    public static final String GAME_PREFS_KEY_DIFFICULTY = "game_difficulty";
    public static final String GAME_PREFS_KEY_MUSIC_VOLUME = "game_music_volume";
    public static final String TUTORIAL_DONE = "tutorial_done";

    public static final int PIXEL_BY_METER = 15;

    public static final int GAME_LOOP_FREQUENCY = 10;// per second

    public static enum DifficultyLevel {
        easy, medium, hard
    }

    public static enum MusicState {
        off, on
    }

    public static final int MAX_UNIT_PER_ARMY = 8;
    public static final float SELL_PRICE_FACTOR = 1.0f;

    public static final int DEPLOYMENT_ZONE_SIZE = 8;

    public static float getDistanceBetween(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2)));
    }

    public static float getDistanceBetween(GameElement g1, GameElement g2) {
        return getDistanceBetween(g1.getSprite().getX(), g1.getSprite().getY(), g2.getSprite().getX(), g2.getSprite()
                .getY());
    }

    public static float getDistanceBetween(GameElement g1, float x, float y) {
        return getDistanceBetween(g1.getSprite().getX(), g1.getSprite().getY(), x, y);
    }

    private static final int STEP = 2 * PIXEL_BY_METER; // 2 meters

    public static boolean canSee(Map map, GameElement g1, GameElement g2) {
        return canSee(map, g1, g2.getSprite().getX(), g2.getSprite().getY());
    }

    public static boolean canSee(Map map, GameElement g1, float destinationX, float destinationY) {
        float dx = destinationX - g1.getSprite().getX();
        float dy = destinationY - g1.getSprite().getY();
        double angle = Math.atan(dy / dx);
        boolean hasArrived = false;

        float x = g1.getSprite().getX(), y = g1.getSprite().getY();

        List<TerrainType> lstTerrain = new ArrayList<TerrainType>();

        int n = 0;

        while (!hasArrived) {
            if (n > 30) {
                return false;
            }
            // go one step further
            if (dx > 0) {
                x += STEP * Math.cos(angle);
                y += STEP * Math.sin(angle);
            } else {
                x += -STEP * Math.cos(angle);
                y += STEP * Math.sin(angle + Math.PI);
            }

            dx = destinationX - x;
            dy = destinationY - y;

            // check if can see
            TMXTile tmxTile = map.getTmxLayer().getTMXTileAt(x, y);
            if (tmxTile != null) {
                Tile t = map.getTiles()[tmxTile.getTileRow()][tmxTile.getTileColumn()];
                if (t.getContent() != null && Math.sqrt(dx * dx + dy * dy) > PIXEL_BY_METER * 3) {
                    // target is behind someone else
                    return false;
                } else if (t.getTerrain() != null
                        && GameUtils.getDistanceBetween(g1, x, y) > PIXEL_BY_METER * 3
                        && (g1.getTilePosition().getTerrain() != t.getTerrain() && lstTerrain.size() > 1 || Math
                                .sqrt(dx * dx + dy * dy) > PIXEL_BY_METER * 3)) {
                    // target is behind an obstacle
                    return false;
                } else if (t.getTerrain() != null
                        && (lstTerrain.size() == 0 || t.getTerrain() != lstTerrain.get(lstTerrain.size() - 1))) {
                    lstTerrain.add(t.getTerrain());
                }
            }

            if (Math.sqrt(dx * dx + dy * dy) <= STEP) {
                return true;
            }

            n++;
        }

        return true;
    }

    public static Battle createTestData() {
        Battle battle = new Battle(BattlesData.OOSTERBEEK);

        // me
        Player p = new Player("Me", ArmiesData.USA, 0, false, new VictoryCondition(100));
        ArrayList<Unit> lstUnits = new ArrayList<Unit>();
        Unit e = UnitsData.buildATCannon(ArmiesData.USA, Experience.elite).copy();
        lstUnits.add(e);
        Unit e2 = UnitsData.buildRifleMan(ArmiesData.USA, Experience.veteran).copy();
        lstUnits.add(e2);
        p.setUnits(lstUnits);
        battle.getPlayers().add(p);

        // enemy
        p = new Player("Enemy", ArmiesData.GERMANY, 1, true, new VictoryCondition(100));
        lstUnits = new ArrayList<Unit>();
        e = UnitsData.buildScout(ArmiesData.GERMANY, Experience.recruit).copy();
        lstUnits.add(e);
        e2 = UnitsData.buildRifleMan(ArmiesData.GERMANY, Experience.veteran).copy();
        lstUnits.add(e2);
        e2 = UnitsData.buildRifleMan(ArmiesData.GERMANY, Experience.elite).copy();
        lstUnits.add(e2);
        p.setUnits(lstUnits);
        battle.getPlayers().add(p);
        return battle;
    }

    public static float[] getCoordinatesAfterTranslation(float xPosition, float yPosition, float distance,
            double angle, boolean isXPositive) {
        if (isXPositive) {
            return new float[] { (float) (xPosition + distance * Math.cos(angle)),
                    (float) (yPosition + distance * Math.sin(angle)) };
        } else {
            return new float[] { (float) (xPosition - distance * Math.cos(angle)),
                    (float) (yPosition + distance * Math.sin(angle + Math.PI)) };
        }
    }

}
