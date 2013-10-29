package com.glevel.wwii.game.data;

import com.glevel.wwii.R;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.VictoryCondition;

public class CampaignsData {

    public enum Campaigns {
        usa(ArmiesData.USA, R.string.market_garden, 2, new OperationsData[] { OperationsData.ARNHEM_BRIDGE });

        private final int id;
        private final ArmiesData army;
        private final int name;
        private final int nbOperationsToSucceed;
        private final OperationsData[] operations;

        Campaigns(ArmiesData army, int name, int nbOperationsToSucceed, OperationsData[] operations) {
            this.id = ordinal();
            this.army = army;
            this.name = name;
            this.nbOperationsToSucceed = nbOperationsToSucceed;
            this.operations = operations;
        }

        public int getId() {
            return id;
        }

        public ArmiesData getArmy() {
            return army;
        }

        public int getName() {
            return name;
        }

        public int getNbOperationsToSucceed() {
            return nbOperationsToSucceed;
        }

        public OperationsData[] getOperations() {
            return operations;
        }
    }

    public enum OperationsData {
        ARNHEM_BRIDGE(0, 0, 10, new Battle[] { new Battle(BattlesData.ARNHEM_BRIDGE, 3, 100, new VictoryCondition(100),
                new VictoryCondition(100)) });

        private final int introText;
        private final int mapImage;
        private final int objectivePoints;
        private final Battle[] battles;

        OperationsData(int introText, int mapImage, int objectivePoints, Battle[] battles) {
            this.introText = introText;
            this.mapImage = mapImage;
            this.objectivePoints = objectivePoints;
            this.battles = battles;
        }

        public int getIntroText() {
            return introText;
        }

        public int getMapImage() {
            return mapImage;
        }

        public int getObjectivePoints() {
            return objectivePoints;
        }

        public Battle[] getBattles() {
            return battles;
        }
    }

}
