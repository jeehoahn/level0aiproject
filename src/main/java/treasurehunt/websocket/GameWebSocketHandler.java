package treasurehunt.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jakarta.annotation.Nonnull;
import treasurehunt.domain.GameState;
import treasurehunt.service.GameService;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameService gameService;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public GameWebSocketHandler(
            GameService gameService,
            ObjectMapper objectMapper
    ) {
        this.gameService = gameService;
        this.objectMapper = objectMapper;
    }


    // =====================================================
    // 게임 시작
    // =====================================================

    @Override
    public void afterConnectionEstablished(
            @Nonnull WebSocketSession session
    ) throws Exception {

        System.out.println("WebSocket 연결됨");

        gameService.startGame();

        GameState gameState =
                gameService.getGameState();

        sendGameStarted(session, gameState);
    }


    // =====================================================
    // GAME_STARTED 전송
    // =====================================================

    private void sendGameStarted(
            WebSocketSession session,
            GameState gameState
    ) throws Exception {

        Map<String, Object> response =
                new HashMap<>();

        response.put("type", "GAME_STARTED");
        response.put("boardSize",
                gameState.getBoardSize());

        response.put("gold",
                gameState.getPlayer().getGold());

        response.put("remainingBombs",
                gameState.getRemainingBombCount());

        response.put("remainingAnimals",
                gameState.getRemainingAnimalCount());

        response.put("explorationCount",
                gameState.getExplorationCount());

        response.put(
                "remainingExploration",
                gameService.getConfigLoader()
                        .getGameConfig()
                        .getMaxExploration()
                        - gameState.getExplorationCount()
        );

        response.put(
                "levelGold",
                gameState.getLevelGold()
        );

        String json =
                objectMapper.writeValueAsString(response);

        session.sendMessage(
                new TextMessage(json)
        );
    }



    // =====================================================
    // 클라이언트 메시지 처리
    // =====================================================

    @Override
    protected void handleTextMessage(
            @Nonnull WebSocketSession session,
            TextMessage message
    ) throws Exception {

        JsonNode request =
                objectMapper.readTree(
                        message.getPayload()
                );

        String type =
                request.get("type").asText();


// =============================================
// 게임 리셋
// =============================================

        if (type.equals("RESET_GAME")) {

            System.out.println(
                    "사용자가 폭탄 확인 -> 게임 리셋"
            );

            gameService.startGame();

            GameState newGameState =
                    gameService.getGameState();

            sendGameStarted(
                    session,
                    newGameState
            );

            return;
        }


// =============================================
// SELECT_CELL이 아니면 무시
// =============================================

        if (!type.equals("SELECT_CELL")) {
            return;
        }

        int cell =
                request.get("cell").asInt();

        System.out.println(
                "선택된 칸: " + cell
        );

        GameState gameState =
                gameService.getGameState();


        // =====================================================
        // 이미 탐색한 칸
        // =====================================================

        if (gameState.isExplored(cell)) {

            System.out.println(
                    "이미 탐색한 칸: " + cell
            );

            return;
        }


        // =====================================================
        // 탐색 처리
        // =====================================================
        int maxExploration =
                gameService.getConfigLoader()
                        .getGameConfig()
                        .getMaxExploration();

        if (gameState.getExplorationCount() >= maxExploration) {

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "type",
                    "EXPLORATION_FAILED"
            );

            response.put(
                    "message",
                    "탐색 횟수를 모두 사용했지만 동물을 찾지 못했습니다!"
            );

            String json =
                    objectMapper.writeValueAsString(response);

            session.sendMessage(
                    new TextMessage(json)
            );

            return;
        }

        gameState.addExploredCell(cell);

        int remainingExploration =
                gameService.getConfigLoader()
                        .getGameConfig()
                        .getMaxExploration()
                        - gameState.getExplorationCount();


        // =====================================================
        // 폭탄
        // =====================================================

        if (gameState.isBombCell(cell)) {

            System.out.println(
                    "폭탄 발견!"
            );

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "type",
                    "BOMB_FOUND"
            );

            response.put(
                    "cell",
                    cell
            );

            response.put(
                    "message",
                    "💣 폭탄을 발견했습니다!"
            );

            String json =
                    objectMapper.writeValueAsString(
                            response
                    );

            session.sendMessage(
                    new TextMessage(json)
            );
            return;
        }


        // =====================================================
        // 동물
        // =====================================================

        if (gameState.isTargetAnimalCell(cell)) {

            System.out.println(
                    "동물 발견!"
            );

            gameState.removeTargetAnimalCell(cell);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "type",
                    "ANIMAL_FOUND"
            );

            response.put(
                    "cell",
                    cell
            );

            response.put(
                    "remainingAnimals",
                    gameState.getRemainingAnimalCount()
            );

            response.put(
                    "remainingBombs",
                    gameState.getRemainingBombCount()
            );

            response.put(
                    "explorationCount",
                    gameState.getExplorationCount()
            );

            response.put(
                    "remainingExploration",
                    remainingExploration
            );

            response.put(
                    "gold",
                    gameState.getPlayer().getGold()
            );

            response.put(
                    "levelGold",
                    gameState.getLevelGold()
            );

            String json =
                    objectMapper.writeValueAsString(
                            response
                    );

            session.sendMessage(
                    new TextMessage(json)
            );


            // ---------------------------------------------
            // 모든 동물 발견
            // ---------------------------------------------

            if (gameState.getRemainingAnimalCount() == 0) {

                Map<String, Object> clearResponse =
                        new HashMap<>();

                clearResponse.put(
                        "type",
                        "ALL_ANIMALS_FOUND"
                );

                clearResponse.put(
                        "message",
                        "모든 동물을 잡았습니다!"
                );

                String clearJson =
                        objectMapper.writeValueAsString(
                                clearResponse
                        );

                session.sendMessage(
                        new TextMessage(clearJson)
                );
            }

            return;
        }


        // =====================================================
        // GOLD
        // =====================================================

        if (gameState.isGoldCell(cell)) {

            boolean nearAnimal = gameState.isNearAnimal(cell);

            if (nearAnimal) {

                System.out.println(
                        "💰 골드를 발견했습니다! 주변에 동물이 있는 것 같습니다."
                );

            } else {

                System.out.println(
                        "💰 골드를 발견했습니다!"
                );
            }

            int minReward =
                    gameService.getConfigLoader()
                            .getGameConfig()
                            .getMoneyCell()
                            .getMinReward();

            int maxReward =
                    gameService.getConfigLoader()
                            .getGameConfig()
                            .getMoneyCell()
                            .getMaxReward();

            int reward =
                    random.nextInt(
                            maxReward
                                    - minReward
                                    + 1
                    )
                            + minReward;


            // ---------------------------------------------
            // 플레이어 골드 증가
            // ---------------------------------------------

            gameState.getPlayer()
                    .addGold(reward);


            // ---------------------------------------------
            // 현재 레벨에서 얻은 골드 누적
            // ---------------------------------------------

            gameState.addLevelGold(reward);


            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "type",
                    "GOLD_FOUND"
            );

            response.put(
                    "cell",
                    cell
            );

            response.put(
                    "reward",
                    reward
            );

            response.put(
                    "gold",
                    gameState.getPlayer().getGold()
            );

            response.put(
                    "levelGold",
                    gameState.getLevelGold()
            );

            response.put(
                    "remainingBombs",
                    gameState.getRemainingBombCount()
            );

            response.put(
                    "remainingAnimals",
                    gameState.getRemainingAnimalCount()
            );

            response.put(
                    "explorationCount",
                    gameState.getExplorationCount()
            );

            response.put(
                    "remainingExploration",
                    remainingExploration
            );

            String json =
                    objectMapper.writeValueAsString(
                            response
                    );

            session.sendMessage(
                    new TextMessage(json)
            );
            if (remainingExploration <= 0 &&
                    gameState.getRemainingAnimalCount() > 0) {

                Map<String, Object> failResponse =
                        new HashMap<>();

                failResponse.put(
                        "type",
                        "EXPLORATION_FAILED"
                );

                failResponse.put(
                        "message",
                        "탐색 횟수를 모두 사용했지만 동물을 찾지 못했습니다!"
                );

                failResponse.put(
                        "remainingExploration",
                        0
                );

                String failJson =
                        objectMapper.writeValueAsString(
                                failResponse
                        );

                session.sendMessage(
                        new TextMessage(failJson)
                );
            }

            return;

        }


        // =====================================================
        // NORMAL
        // =====================================================
        boolean nearAnimal = gameState.isNearAnimal(cell);

        if (nearAnimal) {

            System.out.println(
                    "아무것도 발견하지 못했습니다. 주변에 동물이 있는 것 같습니다."
            );

        } else {

            System.out.println(
                    "아무것도 발견하지 못했습니다."
            );
        }
        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "type",
                "NORMAL_CELL"
        );

        response.put(
                "cell",
                cell
        );

        response.put(
                "remainingBombs",
                gameState.getRemainingBombCount()
        );

        response.put(
                "remainingAnimals",
                gameState.getRemainingAnimalCount()
        );

        response.put(
                "explorationCount",
                gameState.getExplorationCount()
        );

        response.put(
                "remainingExploration",
                remainingExploration
        );

        response.put(
                "gold",
                gameState.getPlayer().getGold()
        );

        response.put(
                "levelGold",
                gameState.getLevelGold()
        );

        String json =
                objectMapper.writeValueAsString(
                        response
                );

        session.sendMessage(
                new TextMessage(json)
        );
        if (remainingExploration <= 0 &&
                gameState.getRemainingAnimalCount() > 0) {

            Map<String, Object> failResponse =
                    new HashMap<>();

            failResponse.put(
                    "type",
                    "EXPLORATION_FAILED"
            );

            failResponse.put(
                    "message",
                    "탐색 횟수를 모두 사용했지만 동물을 찾지 못했습니다!"
            );

            failResponse.put(
                    "remainingExploration",
                    0
            );

            String failJson =
                    objectMapper.writeValueAsString(
                            failResponse
                    );

            session.sendMessage(
                    new TextMessage(failJson)
            );
        }
    }

}