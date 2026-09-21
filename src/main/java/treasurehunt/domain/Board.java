package treasurehunt.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Board {

    private static final int BOARD_SIZE = 5;
    private static final int CELL_COUNT = BOARD_SIZE * BOARD_SIZE;

    private final List<Cell> cells = new ArrayList<>();

    private final Random random = new Random();

    public Board(String countryJsonPath) {
        loadCountryData(countryJsonPath);
    }

    private void loadCountryData(String countryJsonPath) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(
                    Path.of(countryJsonPath).toFile()
            );

            // 1 ~ 25번 Cell 생성
            for (int i = 1; i <= CELL_COUNT; i++) {
                cells.add(new Cell(i));
            }

            // country.json 읽기
            var fields = root.fields();

            while (fields.hasNext()) {

                var entry = fields.next();

                String countryCode = entry.getKey();
                JsonNode countryData = entry.getValue();

                String countryName =
                        countryData.get("name").asText();

                // 국가가 가진 Cell 번호
                List<Integer> countryCells = new ArrayList<>();

                JsonNode cellArray =
                        countryData.get("cells");

                for (JsonNode cellNode : cellArray) {
                    countryCells.add(cellNode.asInt());
                }

                // 국가가 가진 동물 이름
                List<String> countryAnimals = new ArrayList<>();

                JsonNode animalArray =
                        countryData.get("animals");

                for (JsonNode animalNode : animalArray) {
                    countryAnimals.add(animalNode.asText());
                }

                Country country = new Country(
                        countryCode,
                        countryName,
                        countryCells,
                        countryAnimals
                );

                // 해당 국가의 Cell에 국가 연결
                for (int cellId : countryCells) {

                    if (cellId < 1 || cellId > CELL_COUNT) {
                        throw new IllegalArgumentException(
                                "잘못된 Cell 번호: " + cellId
                        );
                    }

                    Cell cell = cells.get(cellId - 1);

                    cell.setCountry(country);
                }
            }

            // 모든 Cell에 국가가 배정되었는지 확인
            for (Cell cell : cells) {

                if (cell.getCountry() == null) {
                    throw new IllegalStateException(
                            "국가가 배정되지 않은 Cell이 있습니다: "
                                    + cell.getId()
                    );
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "country.json을 읽을 수 없습니다: "
                            + countryJsonPath,
                    e
            );
        }
    }

    public int getSize() {
        return BOARD_SIZE;
    }

    public int getCellCount() {
        return cells.size();
    }

    public Cell getCell(int cellId) {

        if (cellId < 1 || cellId > CELL_COUNT) {
            throw new IllegalArgumentException(
                    "잘못된 Cell 번호: " + cellId
            );
        }

        return cells.get(cellId - 1);
    }

    public List<Cell> getCells() {
        return Collections.unmodifiableList(cells);
    }


    // =========================================================
    // 게임 시작 시 Cell 배치
    // =========================================================

    public void initializeCells(
            int bombCount,
            int animalCount,
            int moneyProbability
    ) {

        // -----------------------------------------------------
        // 0. 기존 상태 초기화
        // -----------------------------------------------------

        for (Cell cell : cells) {
            cell.setType(CellType.NORMAL);
            cell.resetExplore();
        }


        // -----------------------------------------------------
        // 1. 폭탄 배치
        // -----------------------------------------------------

        List<Cell> bombCandidates =
                new ArrayList<>(cells);

        List<Cell> bombCells;

        while (true) {

            Collections.shuffle(
                    bombCandidates,
                    random
            );

            bombCells = new ArrayList<>(
                    bombCandidates.subList(
                            0,
                            bombCount
                    )
            );

            // 폭탄이 들어간 국가를 저장
            Set<String> bombCountries = new HashSet<>();

            for (Cell cell : bombCells) {
                bombCountries.add(
                        cell.getCountry().getCode()
                );
            }

            // 최소 2개의 국가에 폭탄이 있어야 함
            if (bombCountries.size() >= 2) {
                break;
            }
        }

        // 뽑힌 칸을 BOMB으로 설정
        for (Cell cell : bombCells) {
            cell.setType(CellType.BOMB);
        }


        // -----------------------------------------------------
        // 2. 폭탄을 제외한 칸에서 동물 배치
        // -----------------------------------------------------

        List<Cell> remainingCells =
                new ArrayList<>();

        for (Cell cell : cells) {

            if (cell.getType() != CellType.BOMB) {
                remainingCells.add(cell);
            }
        }

        Collections.shuffle(
                remainingCells,
                random
        );

        List<Cell> animalCells =
                new ArrayList<>(
                        remainingCells.subList(
                                0,
                                animalCount
                        )
                );

        for (Cell cell : animalCells) {
            cell.setType(CellType.ANIMAL);
        }


        // -----------------------------------------------------
        // 3. 남은 칸에 돈 확률 적용
        // -----------------------------------------------------

        List<Cell> moneyCandidates =
                new ArrayList<>();

        for (Cell cell : remainingCells) {

            if (cell.getType() != CellType.ANIMAL) {
                moneyCandidates.add(cell);
            }
        }

        // 30% 등의 확률을 각 칸마다 검사
        int moneyCellCount = 0;

        for (Cell cell : moneyCandidates) {

            int probability =
                    random.nextInt(100) + 1;

            if (probability <= moneyProbability) {
                moneyCellCount++;
            }
        }


        // -----------------------------------------------------
        // 4. 성공한 개수만큼 GOLD 배치
        // -----------------------------------------------------

        Collections.shuffle(
                moneyCandidates,
                random
        );

        for (int i = 0; i < moneyCellCount; i++) {

            moneyCandidates
                    .get(i)
                    .setType(CellType.GOLD);
        }


        // -----------------------------------------------------
        // 5. 나머지는 NORMAL
        // -----------------------------------------------------

        // 이미 NORMAL로 초기화했기 때문에
        // 따로 처리할 필요 없음
    }
}