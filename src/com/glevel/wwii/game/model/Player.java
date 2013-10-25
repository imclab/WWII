package com.glevel.wwii.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.model.units.Unit;

public class Player implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6829836915818818596L;
    private String name;
    private int requisition;

    private ArmiesData army;
    private List<Unit> units = new ArrayList<Unit>();
    private boolean isAI;
    private VictoryCondition victoryCondition;
    private int armyIndex;

    private int xPositionDeployment;

    public boolean checkIfPlayerWon(Battle battle) {
        return victoryCondition.checkVictory(this, battle);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequisition() {
        return requisition;
    }

    public void setRequisition(int requisition) {
        this.requisition = requisition;
    }

    public ArmiesData getArmy() {
        return army;
    }

    public void setArmy(ArmiesData army) {
        this.army = army;
    }

    public List<Unit> gameElement() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setAI(boolean isAI) {
        this.isAI = isAI;
    }

    public VictoryCondition getVictoryCondition() {
        return victoryCondition;
    }

    public void setVictoryCondition(VictoryCondition victoryCondition) {
        this.victoryCondition = victoryCondition;
    }

    public int getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(int armyIndex) {
        this.armyIndex = armyIndex;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public int getXPositionDeployment() {
        return xPositionDeployment;
    }

    public void setXPositionDeployment(int xPositionDeployment) {
        this.xPositionDeployment = xPositionDeployment;
    }

}