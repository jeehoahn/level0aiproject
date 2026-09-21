package treasurehunt.config;

import java.util.List;

public class CountryConfig {

    private String name;
    private List<Integer> cells;
    private List<String> animals;

    public String getName() {
        return name;
    }

    public List<Integer> getCells() {
        return cells;
    }

    public List<String> getAnimals() {
        return animals;
    }
}