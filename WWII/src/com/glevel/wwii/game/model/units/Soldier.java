package com.glevel.wwii.game.model.units;

import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.NamesData;

public class Soldier extends Unit {

    private String realName;

    public Soldier(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed) {
        super(army, name, image, experience, weapons, moveSpeed);
        this.realName = createRandomRealName();
    }

    public String getRealName() {
        return realName;
    }

    private String createRandomRealName() {
        String[][] names = null;
        switch (army) {
        case GERMANY:
            names = NamesData.GERMAN_NAMES;
            break;
        case USA:
            names = NamesData.AMERICAN_NAMES;
            break;
        }
        // get the column corresponding to the experience
        String[] h = names[experience.ordinal()];
        return h[(int) (Math.random() * (h.length - 1))];
    }

}
