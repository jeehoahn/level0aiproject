package treasurehunt.domain;

import java.util.List;

public class Country {

    private final String code;
    private final String name;
    private final List<Integer> cells;
    private final List<String> animals;

    public Country(
            String code,
            String name,
            List<Integer> cells,
            List<String> animals
    ) {
        this.code = code;
        this.name = name;
        this.cells = cells;
        this.animals = animals;
    }

    public String getCode() {
        return code;
    }

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