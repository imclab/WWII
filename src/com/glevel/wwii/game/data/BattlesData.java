package com.glevel.wwii.game.data;

import com.glevel.wwii.R;

public enum BattlesData {
	OOSTERBEEK(R.string.battle_oosterbeek, R.drawable.oosterbeck, 90, "oosterbeck.tmx"), NIMEGUE(R.string.battle_nimegue,
            R.drawable.oosterbeck, 90, "map2.tmx"), ARNHEM_STREETS(R.string.battle_arnhem_streets, R.drawable.oosterbeck, 90,
            "map2.tmx");

    private final int id;
    private final int name;
    private final int image;
    private final int requisition;
    private final String tileMapName;

    BattlesData(int name, int image, int requisition, String tileMapName) {
        this.id = this.ordinal();
        this.name = name;
        this.image = image;
        this.requisition = requisition;
        this.tileMapName = tileMapName;
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getRequisition() {
        return requisition;
    }

    public int getId() {
        return id;
    }

    public String getTileMapName() {
        return tileMapName;
    }

}
