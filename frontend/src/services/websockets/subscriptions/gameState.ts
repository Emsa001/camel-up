import { IMessage } from "@stomp/stompjs";
import service from "@services/websockets/socket";
import { Game } from "@src/types";

export const subscribeToGameState = async (
    roomCode: string,
    updateGameState: (game: Game) => void
) => {
    return await service.subscribe(`/topic/game/${roomCode}`, (message: IMessage) => {
        const data: Game = JSON.parse(message.body);
        console.log("Debug: Game state updated:", data);
        updateGameState(data);
    });
};
