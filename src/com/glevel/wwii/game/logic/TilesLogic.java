package com.glevel.wwii.game.logic;

import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.model.Map;
import com.glevel.wwii.game.model.Tile;

public class TilesLogic {

	public static final int TILE_SIZE = 20;

	public static enum Direction {
		up, down, leftUp, leftDown, rightUp, rightDown
	}

	public static int getDistance(Tile from, Tile to) {
		int dy = Math.abs(to.getyPosition() - from.getyPosition());
		int dx = Math.abs(to.getxPosition() - from.getxPosition());
		return dx + Math.max(0, dy - dx);// TODO
	}

	private static boolean isOdd(int n) {
		return (n / 2 == Math.ceil(n / 2));
	}

	public static Tile getNeighbor(Map map, Tile tile, Direction direction) {
		switch (direction) {
		case up:
			return map.getTiles()[tile.getxPosition()][tile.getyPosition() + 1];
		case down:
			return map.getTiles()[tile.getxPosition()][tile.getyPosition() - 1];
		case leftUp:
			return map.getTiles()[tile.getxPosition() - 1][tile.getyPosition()
					- (isOdd(tile.getxPosition()) ? 1 : 0)];
		case leftDown:
			return map.getTiles()[tile.getxPosition() - 1][tile.getyPosition()
					- (isOdd(tile.getxPosition()) ? 0 : 1)];
		case rightUp:
			return map.getTiles()[tile.getxPosition() + 1][tile.getyPosition()
					- (isOdd(tile.getxPosition()) ? 0 : 1)];
		case rightDown:
			return map.getTiles()[tile.getxPosition() + 1][tile.getyPosition()
					- (isOdd(tile.getxPosition()) ? 1 : 0)];
		default:
			return null;
		}
	}

	public static List<Tile> getNeighbors(Map map, Tile tile) {
		List<Tile> neighbors = new ArrayList<Tile>();
		if (tile.getyPosition() > 0) {
			neighbors.add(getNeighbor(map, tile, Direction.down));
		}
		if (tile.getyPosition() < map.getHeight() - 1) {
			neighbors.add(getNeighbor(map, tile, Direction.up));
		}
		if (tile.getxPosition() > 0) {
			if (tile.getyPosition() > 0) {
				neighbors.add(getNeighbor(map, tile, Direction.leftDown));
			}
			if (tile.getyPosition() < map.getHeight() - 1) {
				neighbors.add(getNeighbor(map, tile, Direction.leftUp));
			}
		}
		if (tile.getxPosition() < map.getWidth() - 1) {
			if (tile.getyPosition() > 0) {
				neighbors.add(getNeighbor(map, tile, Direction.rightDown));
			}
			if (tile.getyPosition() < map.getHeight() - 1) {
				neighbors.add(getNeighbor(map, tile, Direction.rightUp));
			}
		}
		return neighbors;
	}

	public static boolean canSee(Map map, Tile from, Tile to) {
		// TODO
		return false;
	}

	public static Tile absolutePositionToTile(Map map, int xAbsolutePosition,
			int yAbsolutePosition) {
		int x = (int) Math.ceil(xAbsolutePosition / TILE_SIZE);
		int y = (int) Math.ceil(yAbsolutePosition
				+ (isOdd(x) ? 0 : TILE_SIZE / 2) / TILE_SIZE);
		return map.getTiles()[x][y];
	}

}
