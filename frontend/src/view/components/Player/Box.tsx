import React from "react";

import { useGame } from "@hooks";
import { Player } from "@src/types";

interface PlayerBox1Props {
    player: Player;
    className?: string;
}

export const PlayerBox1 = ({ player, className }: PlayerBox1Props) => {
    const { hostId } = useGame();

    return (
        <div className={`${className} p-2 rounded-lg`}>
            <div className="font-bold text-white text-sm">
                {player.id === hostId && "🔨 "}
                {player.name}
            </div>
        </div>
    );
};
