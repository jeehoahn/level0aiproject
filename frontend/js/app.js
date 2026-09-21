const connectButton = document.getElementById("connectButton");
const board = document.getElementById("board");
const output = document.getElementById("output");

const level = document.getElementById("level");
const partner = document.getElementById("partner");
const remainingBombs =
    document.getElementById("remainingBombs");
const remainingAnimals =
    document.getElementById("remainingAnimals");
const remainingExploration =
    document.getElementById("remainingExploration");
const gold =
    document.getElementById("gold");
const nextLevelGold =
    document.getElementById("nextLevelGold");

let socket = null;
let waitingForBombConfirm = false;


function showDialogue(message) {
    output.innerHTML = "";
    
    const text = document.createElement("p");
    text.textContent = message;

    output.appendChild(text);
}


function createBoard(boardSize) {

    board.innerHTML = "";

    console.log("보드 크기:", boardSize);

    const cellCount = boardSize * boardSize;

    for (let i = 1; i <= cellCount; i++) {

        const cell = document.createElement("button");

        cell.textContent = i;
        cell.dataset.cell = i;

        cell.addEventListener("click", () => {
            selectCell(i);
        });

        board.appendChild(cell);
    }
}


function markCellAsExplored(cellNumber) {

    const cell = document.querySelector(
        `#board button[data-cell="${cellNumber}"]`
    );

    if (cell !== null) {
        cell.disabled = true;
        cell.textContent = "X";
    }
}


function selectCell(cellNumber) {

    if (socket === null) {
        return;
    }

    // 폭탄 확인을 기다리는 동안에는 칸을 선택할 수 없음
    if (waitingForBombConfirm) {
        return;
    }

    const cell = document.querySelector(
        `#board button[data-cell="${cellNumber}"]`
    );

    if (cell === null || cell.disabled) {
        return;
    }

    cell.disabled = true;
    cell.textContent = "X";

    const request = {
        type: "SELECT_CELL",
        cell: cellNumber
    };

    socket.send(JSON.stringify(request));
}


connectButton.addEventListener("click", () => {

    board.innerHTML = "";
    showDialogue("게임에 연결하는 중...");

    socket = new WebSocket(
        "ws://localhost:8080/game-stream"
    );


    socket.onopen = () => {

        showDialogue("WebSocket 연결 성공");

    };


    socket.onmessage = (event) => {

        console.log("서버 응답:", event.data);

        const response = JSON.parse(event.data);


        // =========================
        // 게임 시작
        // =========================
        if (response.type === "GAME_STARTED") {

            waitingForBombConfirm = false;

            level.textContent = 1;
            partner.textContent = "없음";

            remainingBombs.textContent =
                response.remainingBombs ?? 0;

            remainingAnimals.textContent =
                response.remainingAnimals ?? 0;

            remainingExploration.textContent =
                response.remainingExploration ?? 0;

            gold.textContent =
                response.gold ?? 0;

            nextLevelGold.textContent =
                response.levelGold ?? 0;

            createBoard(response.boardSize);

            showDialogue("게임이 시작되었습니다.");

            return;
        }


        // =========================
        // 폭탄 발견
        // =========================
        if (response.type === "BOMB_FOUND") {

            waitingForBombConfirm = true;

            // 아직 X가 아닌 모든 칸을 일시적으로 비활성화
            const cells = document.querySelectorAll(
                "#board button"
            );

            cells.forEach(cell => {
                cell.disabled = true;
            });

            output.innerHTML = "";

            const message =
                document.createElement("p");

            message.textContent =
                "💣 폭탄을 발견했습니다!";

            const confirmButton =
                document.createElement("button");

            confirmButton.textContent = "확인";

            confirmButton.addEventListener(
                "click",
                () => {

                    confirmButton.disabled = true;

                    socket.send(
                        JSON.stringify({
                            type: "RESET_GAME"
                        })
                    );
                }
            );

            output.appendChild(message);
            output.appendChild(confirmButton);

            return;
        }


        // =========================
        // 동물 발견
        // =========================
        if (response.type === "ANIMAL_FOUND") {

            remainingAnimals.textContent =
                response.remainingAnimals ?? 0;

            remainingBombs.textContent =
                response.remainingBombs ?? 0;

            remainingExploration.textContent =
                response.remainingExploration ?? 0;

            showDialogue(
                "🐾 동물을 발견했습니다!"
            );

            return;
        }


        // =========================
        // 모든 동물 발견
        // =========================
        if (response.type === "ALL_ANIMALS_FOUND") {

            showDialogue(
                response.message ??
                "모든 동물을 잡았습니다!"
            );

            return;
        }
        if (response.type === "EXPLORATION_FAILED") {

            output.innerHTML = "";

            showDialogue(response.message);

            const cells =
                document.querySelectorAll("#board button");

            cells.forEach(cell => {
                cell.disabled = true;
            });

            const confirmButton =
                document.createElement("button");

            confirmButton.textContent = "확인";

            confirmButton.addEventListener("click", () => {

                confirmButton.disabled = true;

                socket.send(
                    JSON.stringify({
                        type: "RESET_GAME"
                    })
                );
            });

            output.appendChild(confirmButton);

            return;
        }


        // =========================
        // 골드 발견
        // =========================
        if (response.type === "GOLD_FOUND") {

            remainingBombs.textContent =
                response.remainingBombs ?? 0;

            remainingAnimals.textContent =
                response.remainingAnimals ?? 0;

            remainingExploration.textContent =
                response.remainingExploration ?? 0;

            gold.textContent =
                response.gold ?? 0;

            nextLevelGold.textContent =
                response.levelGold ?? 0;

            showDialogue(
                `💰 골드를 발견했습니다! +${response.reward ?? 0} 골드`
            );

            return;
        }


        // =========================
        // 일반 칸
        // =========================
        if (response.type === "NORMAL_CELL") {

            remainingBombs.textContent =
                response.remainingBombs ?? 0;

            remainingAnimals.textContent =
                response.remainingAnimals ?? 0;

            remainingExploration.textContent =
                response.remainingExploration ?? 0;

            showDialogue(
                "아무것도 발견하지 못했습니다."
            );

            return;
        }
    };


    socket.onerror = (error) => {

        showDialogue("WebSocket 오류");

        console.error(error);
    };


    socket.onclose = () => {

        console.log("WebSocket 연결 종료");
    };
    if (response.type === "EXPLORATION_FAILED") {

    waitingForBombConfirm = true;

    // 모든 칸 클릭 잠금
    const cells = document.querySelectorAll(
        "#board button"
    );

    cells.forEach(cell => {
        cell.disabled = true;
    });

    output.innerHTML = "";

    const message =
        document.createElement("p");

    message.textContent =
        response.message;

    const confirmButton =
        document.createElement("button");

    confirmButton.textContent = "확인";

    confirmButton.addEventListener(
        "click",
        () => {

            confirmButton.disabled = true;

            socket.send(
                JSON.stringify({
                    type: "RESET_GAME"
                })
            );
        }
    );

    output.appendChild(message);
    output.appendChild(confirmButton);

    return;
}
});