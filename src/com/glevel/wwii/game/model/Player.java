package com.glevel.wwii.game.model;

import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.model.units.Unit;

public class Player {

	private String name;
	private int requisition;
	private ArmiesData army;
	private List<Unit> units = new ArrayList<Unit>();
	private boolean isAI;
	private VictoryCondition victoryCondition;
	private int armyIndex;

	public boolean checkIfPlayerWon(Battle battle) {
		return victoryCondition.checkVictory(armyIndex, battle);
	}

}
