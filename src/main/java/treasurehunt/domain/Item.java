package treasurehunt.domain;

public class Item {

    private final String id;
    private final String name;
    private final String type;
    private final int price;
    private final Integer maxGrade;
    private final boolean consumable;

    public Item(
            String id,
            String name,
            String type,
            int price,
            Integer maxGrade,
            boolean consumable
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.maxGrade = maxGrade;
        this.consumable = consumable;
    }

    public String getId() {
        return id;
    }

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