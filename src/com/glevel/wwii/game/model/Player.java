package com.glevel.wwii.game.model;

import java.util.List;

import com.glevel.wwii.game.model.armies.Unit;

public class Player {

	public static enum Nationality {
		german, american
	}

	private String name;
	private Nationality nationality;
	private int requisition;
	private List<Unit> army;
	private boolean isAI;
	private VictoryCondition victoryCondition;
	private int armyIndex;
	
	public boolean checkIfPlayerWon(Battle battle) {
		return victoryCondition.checkVictory(armyIndex, battle);
	}
	
	
}
