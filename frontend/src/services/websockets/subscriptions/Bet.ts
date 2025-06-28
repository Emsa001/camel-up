import service from "@services/websockets/socket";
import { IMessage } from "@stomp/stompjs";

export const subscribeToBet = async (roomCode: string | null) => {
    return await service.subscribe(`/topic/game/${roomCode}/handleBetting`, (message: IMessage) => {
        const data = JSON.parse(message.body);

        console.log("Dice event received:", data);
    });
};
