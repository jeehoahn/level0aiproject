package treasurehunt.service;

import treasurehunt.config.ConfigLoader;
import org.springframework.stereotype.Service;
import treasurehunt.config.GameConfig;
import treasurehunt.domain.Board;
import treasurehunt.domain.GameState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class GameService {

    private final ConfigLoader configLoader;
    private final Board board;

    private GameState gameState;

    private final Random random = new Random();

    public GameService() throws IOException {

        this.configLoader = new ConfigLoader();

        this.board = new Board(
                "data/country.json"
        );
    }

    public void startGame() {

        GameConfig gameConfig = configLoader.getGameConfig();

        gameState = new GameState(
                gameConfig.getBoardSize(),
                gameConfig.getStartingGold()
        );

        initializeCells(gameConfig);
    }

    private void initializeCells(GameConfig gameConfig) {

        int boardSize = gameConfig.getBoardSize();
        int cellCount = boardSize * boardSize;

        int bombCount =
                gameConfig.getBomb().getStartingCount();

        int animalCount =
                gameConfig.getTargetAnimals().getStartingCount();

        int moneyProbability =
                gameConfig.getMoneyCell().getProbability();


        // =====================================================
        // 1. 폭탄 배치
        // =====================================================

        List<Integer> availableCells = new ArrayList<>();

        for (int i = 1; i <= cellCount; i++) {
            availableCells.add(i);
        }

        List<Integer> bombCells;

        while (true) {

            // 1~25를 랜덤으로 섞음
            Collections.shuffle(
                    availableCells,
                    random
            );

            // 앞에서 bombCount개 선택
            bombCells = new ArrayList<>(
                    availableCells.subList(
                            0,
                            bombCount
                    )
            );

            // 폭탄이 배치된 국가들을 저장
            Set<String> bombCountries = new HashSet<>();

            for (int cellId : bombCells) {

                String countryCode =
                        board.getCell(cellId)
                                .getCountry()
                                .getCode();

                bombCountries.add(countryCode);
            }

            // 최소 2개의 국가에 폭탄이 있으면 확정
            if (bombCountries.size() >= 2) {
                break;
            }

            // 1개 국가에만 몰렸다면 while 처음으로 돌아가서
            // 폭탄을 다시 랜덤 배치
        }

        // 확정된 폭탄을 GameState에 저장
        for (int cellId : bombCells) {
            gameState.addBombCell(cellId);
        }


        // =====================================================
        // 2. 폭탄을 제외한 칸에서 동물 배치
        // =====================================================

        List<Integer> remainingCells = new ArrayList<>();

        for (int cellId : availableCells) {

            if (!gameState.isBombCell(cellId)) {
                remainingCells.add(cellId);
            }
        }

        Collections.shuffle(
                remainingCells,
                random
        );

        for (int i = 0; i < animalCount; i++) {

            int cellId = remainingCells.get(i);

            gameState.addTargetAnimalCell(cellId);
        }


        // =====================================================
        // 3. 폭탄과 동물을 제외한 칸에 돈 확률 적용
        // =====================================================

        List<Integer> moneyCandidates = new ArrayList<>();

        for (int cellId : remainingCells) {

            if (!gameState.isTargetAnimalCell(cellId)) {
                moneyCandidates.add(cellId);
            }
        }

        int goldCount = 0;

        for (int cellId : moneyCandidates) {

            int probability =
                    random.nextInt(100) + 1;

            if (probability <= moneyProbability) {
                goldCount++;
            }
        }


        // =====================================================
        // 4. 성공한 개수만큼 GOLD 배치
        // =====================================================

        Collections.shuffle(
                moneyCandidates,
                random
        );

        for (int i = 0; i < goldCount; i++) {

            int cellId = moneyCandidates.get(i);

            gameState.addGoldCell(cellId);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public Board getBoard() {
        return board;
    }
}