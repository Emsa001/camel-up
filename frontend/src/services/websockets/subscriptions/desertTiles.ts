import service from "@services/websockets/socket";
import { DesertTile } from "@src/types";
import { IMessage } from "@stomp/stompjs";

export const subscribeToTiles = async (
    roomCode: string,
    setDesertTiles: (tiles: DesertTile[]) => void
) => {
    return await service.subscribe(`/topic/game/${roomCode}/tiles`, (message: IMessage) => {
        const rawData = JSON.parse(message.body);

        setDesertTiles(rawData.tiles);
    });
};