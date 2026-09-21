package treasurehunt.domain;

public class Cell {

    private final int id;

    private Country country;

    private CellType type;

    private boolean explored;

    public Cell(int id) {
        this.id = id;
        this.type = CellType.NORMAL;
        this.explored = false;
    }

    public int getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public boolean isExplored() {
        return explored;
    }

    public void explore() {
        this.explored = true;
    }

    public void resetExplore() {
        this.explored = false;
    }
}