package treasurehunt.domain;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private final int boardSize;

    private final List<Integer> exploredCells;
    private final List<Integer> bombCells;
    private final List<Integer> targetAnimalCells;
    private final List<Integer> goldCells;


    private final Player player;

    private int explorationCount;
    private boolean gameOver;
    private int levelGold;

    public GameState(int boardSize, int startingGold) {

        this.boardSize = boardSize;
        this.levelGold = 0;

        this.exploredCells = new ArrayList<>();
        this.bombCells = new ArrayList<>();
        this.targetAnimalCells = new ArrayList<>();
        this.goldCells = new ArrayList<>();

        this.player = new Player(startingGold);

        this.explorationCount = 0;
        this.gameOver = false;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public List<Integer> getExploredCells() {
        return exploredCells;
    }

    public List<Integer> getBombCells() {
        return bombCells;
    }

    public List<Integer> getTargetAnimalCells() {
        return targetAnimalCells;
    }

    public List<Integer> getGoldCells() {
        return goldCells;
    }

    public Player getPlayer() {
        return player;
    }

    public int getExplorationCount() {
        return explorationCount;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void addExploredCell(int cell) {
        if (!exploredCells.contains(cell)) {
            exploredCells.add(cell);
            explorationCount++;
        }
    }

    public boolean isExplored(int cell) {
        return exploredCells.contains(cell);
    }

    public boolean isBombCell(int cell) {
        return bombCells.contains(cell);
    }

    public boolean isTargetAnimalCell(int cell) {
        return targetAnimalCells.contains(cell);
    }

    public boolean isGoldCell(int cell) {
        return goldCells.contains(cell);
    }

    public void addBombCell(int cell) {
        if (!bombCells.contains(cell)) {
            bombCells.add(cell);
        }
    }

    public void addTargetAnimalCell(int cell) {
        if (!targetAnimalCells.contains(cell)) {
            targetAnimalCells.add(cell);
        }
    }

    public void addGoldCell(int cell) {
        if (!goldCells.contains(cell)) {
            goldCells.add(cell);
        }
    }
    public boolean isNearAnimal(int cell) {

        int boardSize = this.boardSize;

        int row = (cell - 1) / boardSize;
        int col = (cell - 1) % boardSize;

        for (Integer animalCell : targetAnimalCells) {

            int animalRow =
                    (animalCell - 1) / boardSize;

            int animalCol =
                    (animalCell - 1) % boardSize;

            int rowDistance =
                    Math.abs(row - animalRow);

            int colDistance =
                    Math.abs(col - animalCol);

            // 상하좌우 + 대각선으로 1칸 이내
            if (rowDistance <= 1 &&
                    colDistance <= 1 &&
                    !(rowDistance == 0 && colDistance == 0)) {

                return true;
            }
        }

        return false;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public boolean removeTargetAnimalCell(int cell) {
    return targetAnimalCells.remove(Integer.valueOf(cell));
    }
    
    public int getRemainingAnimalCount() {
    return targetAnimalCells.size();
    }

    public int getLevelGold() {
        return levelGold;
    }

    public void addLevelGold(int amount) {
        levelGold += amount;
    }

    public int getRemainingBombCount() {
        int count = 0;

        for (Integer bombCell : bombCells) {
            if (!exploredCells.contains(bombCell)) {
                count++;
            }
        }

        return count;
    }
}