package treasurehunt.domain;

public class Partner {

    private final String id;
    private final String name;
    private final int grade;

    private int level;
    private int captureCount;

    public Partner(String id, String name, int grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.level = 1;
        this.captureCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }

    public int getLevel() {
        return level;
    }

    public int getCaptureCount() {
        return captureCount;
    }

    public void addCaptureCount(int count) {
        captureCount += count;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}