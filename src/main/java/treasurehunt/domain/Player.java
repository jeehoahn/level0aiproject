package treasurehunt.domain;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int gold;

    private final List<Animal> capturedAnimals;
    private final List<Item> items;

    private Partner partner;

    public Player(int startingGold) {
        this.gold = startingGold;
        this.capturedAnimals = new ArrayList<>();
        this.items = new ArrayList<>();
        this.partner = null;
    }

    public int getGold() {
        return gold;
    }

    public List<Animal> getCapturedAnimals() {
        return capturedAnimals;
    }

    public List<Item> getItems() {
        return items;
    }

    public Partner getPartner() {
        return partner;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean spendGold(int amount) {
        if (gold < amount) {
            return false;
        }

        gold -= amount;
        return true;
    }

    public void addAnimal(Animal animal) {
        capturedAnimals.add(animal);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }
}