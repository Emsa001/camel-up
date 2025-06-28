import { IMessage } from "@stomp/stompjs";
import service from "@services/websockets/socket";
import { Camel } from "@src/types";

interface IDiceEvent {
    playerId: string;
    color: string;
    value: number;
    camels: Camel[];
}

export const subscribeToDice = async (
    roomCode: string,
    rollDice: (color: string, value: number, camels: Camel[]) => void,
) => {
    return await service.subscribe(`/topic/game/${roomCode}/dice`, (message: IMessage) => {
        const data: IDiceEvent = JSON.parse(message.body);

        console.log("Dice event received:", data);
        rollDice(data.color.toLowerCase(), data.value, data.camels);
    });
};
