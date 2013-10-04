package com.glevel.wwii.game.model;

public class VictoryCondition {

	public static enum VictoryType {
		annihilation, takenadhold, rush
	}

	private VictoryType type;
	private int pourcentageDestroyed;
	private int objectiveXPosition;
	private int objectiveYPosition;
	private int timeHold;

	/**
	 * Constructor annihilation
	 * 
	 * @param pourcentageDestroyed
	 */
	public VictoryCondition(int pourcentageDestroyed) {
		type = VictoryType.annihilation;
		this.pourcentageDestroyed = pourcentageDestroyed;
	}

	/**
	 * Constructor take and hold
	 * 
	 * @param objectiveXPosition
	 * @param objectiveYPosition
	 * @param timeHold
	 */
	public VictoryCondition(int objectiveXPosition, int objectiveYPosition,
			int timeHold) {
		type = VictoryType.takenadhold;
		this.objectiveXPosition = objectiveXPosition;
		this.objectiveYPosition = objectiveYPosition;
		this.timeHold = timeHold;
	}

	/**
	 * Constructor rush
	 * 
	 * @param objectiveXPosition
	 * @param objectiveYPosition
	 * @param timeHold
	 */
	public VictoryCondition(int objectiveXPosition, int objectiveYPosition) {
		type = VictoryType.rush;
		this.objectiveXPosition = objectiveXPosition;
		this.objectiveYPosition = objectiveYPosition;
	}

	/**
	 * Checks if the victory condition has been reached
	 * 
	 * @param armyIndex
	 * 
	 * @param battle
	 * @return boolean
	 */
	public boolean checkVictory(int armyIndex, Battle battle) {
		switch (type) {
		case annihilation:
			// TODO
			break;
		case takenadhold:
			// TODO
			break;
		case rush:
			// TODO
			break;
		}

		return false;
	}

}
