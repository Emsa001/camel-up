import { useEffect } from "react";
import service from "@services/websockets/socket";
import { subscribeToTiles } from "@src/services/websockets/subscriptions/desertTiles";
import { useGame } from "./useGame";
import { DesertTile } from "@src/types";

export const useTiles = () => {
    const { roomCode, desertTiles, setDesertTiles } = useGame();

    const addDesertTile = (tile: DesertTile) => {
        console.log("Adding desert tile:", tile);
        service.publish({
            destination: `/app/game/${roomCode}/tiles`,
            message: tile,
        });
    };

    useEffect(() => {
        if (roomCode) {
            subscribeToTiles(roomCode, setDesertTiles);
        }

        return () => {
            service.unsubscribe(`/topic/game/${roomCode}/tiles`);
        };
    }, [roomCode]);

    return {
        desertTiles,
        addDesertTile,
        setDesertTiles,
    };
};
