package treasurehunt.config;

public class ItemConfig {

    private String name;
    private String type;
    private int price;
    private Integer maxGrade;
    private boolean consumable;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public boolean isConsumable() {
        return consumable;
    }
}