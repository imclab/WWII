package com.glevel.wwii.game.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.BattlesData;
import com.glevel.wwii.game.interfaces.OnNewSpriteToDraw;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.units.Unit;

public class Battle implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5687413891626670339L;
    private final int battleId;
    private final int name;
    private final int image;
    private final String tileMapName;
    private transient final Map map;
    private final int alliesRequisition, axisRequisition;
    private final VictoryCondition alliesVictoryCondition, axisVictoryCondition;
    // deployment
    private final int alliesDeploymentZoneSize, axisDeploymentZoneSize;
    private final boolean isAllyLeftSide;

    private long id = 0L;
    private int campaignId = 0;
    private int importance;
    private transient List<Player> players = new ArrayList<Player>();
    private transient Phase phase = Phase.deployment;
    private transient OnNewSpriteToDraw onNewSprite;

    // for campaign mode
    private boolean isDone = false;

    public static enum Phase {
        deployment, combat
    }

    /**
     * Single Battle Mode Constructor
     * 
     * @param data
     */
    public Battle(BattlesData data) {
        this.battleId = data.getId();
        this.name = data.getName();
        this.image = data.getImage();
        this.map = new Map();
        this.tileMapName = data.getTileMapName();
        this.alliesRequisition = data.getAlliesRequisition();
        this.axisRequisition = data.getAxisRequisition();
        this.alliesVictoryCondition = new VictoryCondition(90);
        this.axisVictoryCondition = new VictoryCondition(90);
        this.alliesDeploymentZoneSize = data.getAlliesDeploymentZoneSize();
        this.axisDeploymentZoneSize = data.getAxisDeploymentZoneSize();
        this.isAllyLeftSide = data.getIsAllyLeftSide();
    }

    /**
     * Campaign Mode Constructor
     * 
     * @param data
     */
    public Battle(BattlesData data, int importance, VictoryCondition alliesVictoryCondition,
            VictoryCondition axisVictoryCondition) {
        this.battleId = data.getId();
        this.name = data.getName();
        this.image = data.getImage();
        this.importance = importance;
        this.map = new Map();
        this.tileMapName = data.getTileMapName();
        this.alliesRequisition = data.getAlliesRequisition();
        this.axisRequisition = data.getAxisRequisition();
        this.alliesVictoryCondition = alliesVictoryCondition;
        this.axisVictoryCondition = axisVictoryCondition;
        this.alliesDeploymentZoneSize = data.getAlliesDeploymentZoneSize();
        this.axisDeploymentZoneSize = data.getAxisDeploymentZoneSize();
        this.isAllyLeftSide = data.getIsAllyLeftSide();
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Map getMap() {
        return map;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Unit> getEnemies(Unit unit) {
        for (Player p : players) {
            if (p.getArmy() != unit.getArmy()) {
                return p.getUnits();
            }
        }
        return null;
    }

    public List<Unit> getEnemies(ArmiesData army) {
        for (Player p : players) {
            if (p.getArmy() != army) {
                return p.getUnits();
            }
        }
        return null;
    }

    public Player getEnemyPlayer(Player player) {
        for (Player p : players) {
            if (p != player) {
                return p;
            }
        }
        return null;
    }

    public Player getMe() {
        return getPlayers().get(0);
    }

    public OnNewSpriteToDraw getOnNewSprite() {
        return onNewSprite;
    }

    public void setOnNewSprite(OnNewSpriteToDraw onNewSprite) {
        this.onNewSprite = onNewSprite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBattleId() {
        return battleId;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public boolean isSingleBattle() {
        return campaignId == 0;
    }

    public String getTileMapName() {
        return tileMapName;
    }

    public VictoryCondition getPlayerVictoryCondition(ArmiesData army) {
        if (army.isAlly()) {
            return alliesVictoryCondition;
        } else {
            return axisVictoryCondition;
        }
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    public int getAlliesDeploymentZoneSize() {
        return alliesDeploymentZoneSize;
    }

    public int getAxisDeploymentZoneSize() {
        return axisDeploymentZoneSize;
    }

    public boolean isAllyLeftSide() {
        return isAllyLeftSide;
    }

    public int[] getDeploymentBoundaries(Player player) {
        if (player.isAlly()) {
            if (isAllyLeftSide) {
                return new int[] { 0, alliesDeploymentZoneSize };
            } else {
                return new int[] { map.getWidth() - alliesDeploymentZoneSize, map.getWidth() };
            }
        } else {
            if (isAllyLeftSide) {
                return new int[] { map.getWidth() - axisDeploymentZoneSize, map.getWidth() };
            } else {
                return new int[] { 0, axisDeploymentZoneSize };
            }
        }
    }

    public int getAlliesRequisition() {
        return alliesRequisition;
    }

    public int getAxisRequisition() {
        return axisRequisition;
    }

    public int getRequisition(Player player) {
        if (player.isAlly()) {
            return alliesRequisition;
        } else {
            return axisRequisition;
        }
    }

}
