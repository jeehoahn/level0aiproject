package treasurehunt.domain;

public class Animal {

    private final String code;
    private final String name;
    private final int grade;

    public Animal(String code, String name, int grade) {
        this.code = code;
        this.name = name;
        this.grade = grade;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getGrade() {
        return grade;
    }
}