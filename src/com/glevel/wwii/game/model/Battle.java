package com.glevel.wwii.game.model;

import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.model.map.Map;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.interfaces.OnNewSpriteToDraw;

public class Battle {

    private long id;
    private int battleId;
    private int campaignId;
    private int name;
    private int image;
    private int importance;
    private int requisition;
    private Map map;
    private List<Player> players = new ArrayList<Player>();
    private Phase phase = Phase.deployment;

    public static enum Phase {
        deployment, combat
    }

    private OnNewSpriteToDraw onNewSprite;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getRequisition() {
        return requisition;
    }

    public void setRequisition(int requisition) {
        this.requisition = requisition;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
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

    public void setBattleId(int battleId) {
        this.battleId = battleId;
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
}
