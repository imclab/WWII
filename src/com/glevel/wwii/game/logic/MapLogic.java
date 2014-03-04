package com.glevel.wwii.game.logic;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTile;

import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.map.Tile.TerrainType;
import com.glevel.wwii.game.models.units.categories.Unit;
import com.glevel.wwii.game.models.units.categories.Unit.Action;
import com.glevel.wwii.game.models.units.categories.Vehicle;

public class MapLogic {

    private static final int RAYCASTER_STEP = 2 * GameUtils.PIXEL_BY_METER; // 2
                                                                            // meters

    public static float getDistanceBetween(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

    public static float getDistanceBetween(GameElement g1, GameElement g2) {
        return getDistanceBetween(g1.getSprite().getX(), g1.getSprite().getY(), g2.getSprite().getX(), g2.getSprite()
                .getY());
    }

    public static float getDistanceBetween(GameElement g1, float x, float y) {
        return getDistanceBetween(g1.getSprite().getX(), g1.getSprite().getY(), x, y);
    }

    public static boolean canSee(Map map, GameElement g1, GameElement g2) {

        // hiding units are more difficult to see
        if (!g2.isVisible() && getDistanceBetween(g1, g2) > 20 * GameUtils.PIXEL_BY_METER && g2 instanceof Unit) {
            Unit unit = (Unit) g2;
            if (unit.getCurrentAction() == Action.HIDING && unit.getTilePosition().getTerrain() != null
                    && Math.random() < 0.9f) {
                return false;
            }
        }

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
                x += RAYCASTER_STEP * Math.cos(angle);
                y += RAYCASTER_STEP * Math.sin(angle);
            } else {
                x += -RAYCASTER_STEP * Math.cos(angle);
                y += RAYCASTER_STEP * Math.sin(angle + Math.PI);
            }

            dx = destinationX - x;
            dy = destinationY - y;

            TMXTile tmxTile = map.getTmxLayer().getTMXTileAt(x, y);
            if (tmxTile != null) {
                Tile t = map.getTiles()[tmxTile.getTileRow()][tmxTile.getTileColumn()];

                if (t.getContent() != null && t.getContent() instanceof Vehicle
                        && Math.sqrt(dx * dx + dy * dy) > GameUtils.PIXEL_BY_METER * 1) {
                    // target is hidden behind a vehicle
                    return false;
                }

                // lists the different different obstacles
                if (lstTerrain.size() == 0 || t.getTerrain() != lstTerrain.get(lstTerrain.size() - 1)) {
                    lstTerrain.add(t.getTerrain());
                    if (lstTerrain.size() > 3) {
                        return false;
                    } else if ((getDistanceBetween(g1, x, y) > GameUtils.PIXEL_BY_METER * 3 || Math.sqrt(dx * dx + dy
                            * dy) > GameUtils.PIXEL_BY_METER * 3)
                            && lstTerrain.size() > 1) {
                        // target is behind an obstacle
                        return false;
                    }
                }
            }

            if (Math.sqrt(dx * dx + dy * dy) <= RAYCASTER_STEP) {
                break;
            }

            n++;
        }

        return true;
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

    public static float getAngle(Sprite sprite, float xDestination, float yDestination) {
        float dx = xDestination - sprite.getX();
        float dy = yDestination - sprite.getY();
        float finalAngle = (float) (Math.atan(dy / dx) * 180 / Math.PI);
        if (dx > 0) {
            finalAngle += 90;
        } else {
            finalAngle -= 90;
        }
        return finalAngle - sprite.getRotation();
    }

}
