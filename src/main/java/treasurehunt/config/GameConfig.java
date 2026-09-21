package treasurehunt.config;

public class GameConfig {

    private int boardSize;
    private int maxExploration;

    private int startingGold;

    private BombConfig bomb;
    private TargetAnimalsConfig targetAnimals;

    private int animalReward;

    private MoneyCellConfig moneyCell;

    public int getBoardSize() {
        return boardSize;
    }

    public int getMaxExploration() {
        return maxExploration;
    }

    public int getStartingGold() {
        return startingGold;
    }

    public BombConfig getBomb() {
        return bomb;
    }

    public TargetAnimalsConfig getTargetAnimals() {
        return targetAnimals;
    }

    public int getAnimalReward() {
        return animalReward;
    }

    public MoneyCellConfig getMoneyCell() {
        return moneyCell;
    }

    public static class BombConfig {

        private int startingCount;
        private int additionalCountInterval;
        private int maxCount;

        public int getStartingCount() {
            return startingCount;
        }

        public int getAdditionalCountInterval() {
            return additionalCountInterval;
        }

        public int getMaxCount() {
            return maxCount;
        }
    }

    public static class TargetAnimalsConfig {

        private int startingCount;
        private int additionalCountInterval;
        private int maxCount;

        public int getStartingCount() {
            return startingCount;
        }

        public int getAdditionalCountInterval() {
            return additionalCountInterval;
        }

        public int getMaxCount() {
            return maxCount;
        }
    }

    public static class MoneyCellConfig {

        private int probability;
        private int minReward;
        private int maxReward;

        public int getProbability() {
            return probability;
        }

        public int getMinReward() {
            return minReward;
        }

        public int getMaxReward() {
            return maxReward;
        }
    }
}