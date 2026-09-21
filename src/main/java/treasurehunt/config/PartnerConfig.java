package treasurehunt.config;

import java.util.Map;

public class PartnerConfig {

    private String name;
    private int grade;

    private Map<String, LevelUpCondition> levelUpConditions;
    private Map<String, LevelConfig> levels;

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public Map<String, LevelUpCondition> getLevelUpConditions() {
        return levelUpConditions;
    }

    public Map<String, LevelConfig> getLevels() {
        return levels;
    }

    public static class LevelUpCondition {

        private int captureCount;

        public int getCaptureCount() {
            return captureCount;
        }
    }

    public static class LevelConfig {

        private AbilityConfig ability;

        public AbilityConfig getAbility() {
            return ability;
        }
    }

    public static class AbilityConfig {

        private String type;
        private int value;

        public String getType() {
            return type;
        }

        public int getValue() {
            return value;
        }
    }
}