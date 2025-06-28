import React from "react";
import { useGame } from "@hooks";
import { toast } from "react-toastify";
import { DesertTile, Player } from "@src/types";

export const DesertTiles = ({
    selectedTile,
    onSelectTile,
}: {
    selectedTile: DesertTile | null;
    onSelectTile: (tile: any) => void;
}) => {
    const { currentPlayerId, player } = useGame();
    const handleTileClick = (tile: DesertTile) => {
        if (currentPlayerId !== player?.id) {
            toast.error("It's not your turn!");
            return;
        }
        if (selectedTile?.type === tile.type) {
            onSelectTile(null);
        } else if (selectedTile?.type === tile.type) {
            onSelectTile(null);
        } else {
            onSelectTile(tile);
        }
    };

    if (!player) {
        return <div className="text-center text-red-500">Player not found</div>;
    }

    return (
        <div className="grid grid-rows-2 gap-2 p-2 text-center text-5xl font-bold text-black">
            <div
                className="grid grid-cols-2 gap-2 cursor-pointer"
                onClick={() =>
                    handleTileClick({
                        type: "OASIS",
                        position: -1,
                        ownerId: player.id,
                        gameId: player.gameId!,
                    })
                }
            >
                <img src="/gameimg/oasis.png" alt="Oasis" />
                <div className="flex items-center justify-center">+1</div>
            </div>
            <div
                className="grid grid-cols-2 gap-2 cursor-pointer"
                onClick={() =>
                    handleTileClick({
                        type: "MIRAGE",
                        position: -1,
                        ownerId: player!.id,
                        gameId: player.gameId!,
                    })
                }
            >
                <div className="flex items-center justify-center">-1</div>
                <img src="/gameimg/mirage.png" alt="Mirage" />
            </div>
        </div>
    );
};
