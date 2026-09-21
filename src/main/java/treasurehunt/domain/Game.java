package treasurehunt.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class Game {

    private final Board board;

    private final JsonNode gameConfig;
    private final JsonNode animals;
    private final JsonNode items;
    private final JsonNode partners;

    private int gold;

    public Game(String dataPath) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            Path dataDir = Path.of(dataPath);

            // game.json
            this.gameConfig = mapper.readTree(
                    dataDir.resolve("game.json").toFile()
            );

            // animals.json
            this.animals = mapper.readTree(
                    dataDir.resolve("animals.json").toFile()
            );

            // items.json
            this.items = mapper.readTree(
                    dataDir.resolve("items.json").toFile()
            );

            // partners.json
            this.partners = mapper.readTree(
                    dataDir.resolve("partners.json").toFile()
            );

            // game.json의 startingGold 사용
            this.gold = gameConfig
                    .get("startingGold")
                    .asInt();

            // country.json을 사용해서 Board 생성
            this.board = new Board(
                    dataDir.resolve("country.json").toString()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "게임 데이터를 읽는 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    public Board getBoard() {
        return board;
    }

    public int getGold() {
        return gold;
    }

    public JsonNode getGameConfig() {
        return gameConfig;
    }

    public JsonNode getAnimals() {
        return animals;
    }

    public JsonNode getItems() {
        return items;
    }

    public JsonNode getPartners() {
        return partners;
    }

    public void addGold(int amount) {
        gold += amount;
    }

    public boolean useGold(int amount) {

        if (amount < 0) {
            return false;
        }

        if (gold < amount) {
            return false;
        }

        gold -= amount;
        return true;
    }
}