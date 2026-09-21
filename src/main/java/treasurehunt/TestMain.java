package treasurehunt;

import treasurehunt.domain.Cell;
import treasurehunt.domain.Game;

public class TestMain {

    public static void main(String[] args) {

        Game game = new Game(
                "C:\\25 Treasure Hunt\\data"
        );

        System.out.println(
                "Gold: " + game.getGold()
        );

        System.out.println(
                "Board Size: "
                        + game.getGameConfig()
                        .get("boardSize")
                        .asInt()
        );

        System.out.println(
                "Max Exploration: "
                        + game.getGameConfig()
                        .get("maxExploration")
                        .asInt()
        );

        System.out.println(
                "Tiger: "
                        + game.getAnimals()
                        .get("TIGER")
                        .get("name")
                        .asText()
        );

        System.out.println(
                "Gun Price: "
                        + game.getItems()
                        .get("GUN")
                        .get("price")
                        .asInt()
        );

        System.out.println(
                "Magpie Ability: "
                        + game.getPartners()
                        .get("MAGPIE")
                        .get("levels")
                        .get("1")
                        .get("ability")
                        .get("type")
                        .asText()
        );
    }
}