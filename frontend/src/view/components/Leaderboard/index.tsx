import React, { useState } from "react";
import { useGame } from "@src/hooks";
import { PlayerCard } from "./PlayerCard";

export const LeaderBoard = () => {
    const { hostId, players, player, currentPlayerId, legBettingCards } = useGame();
    const [openPopupPlayerId, setOpenPopupPlayerId] = useState<string | null>(null);

    if (!player) return null;

    return (
        <div className="text-black flex flex-col gap-3 mt-3">
            {players
                .slice()
                .sort((a, b) => a.age - b.age)
                .map((p, index) => {
                    const playerCards = legBettingCards.filter((card) => card.playerId === p.id);

                    return (
                        <PlayerCard
                            key={p.id}
                            player={p}
                            isHost={p.id === hostId}
                            isCurrentPlayer={p.id === currentPlayerId}
                            isYou={p.id === player.id}
                            isPopupOpen={openPopupPlayerId === String(p.id)}
                            onTogglePopup={() =>
                                setOpenPopupPlayerId((prev) =>
                                    prev === String(p.id) ? null : String(p.id)
                                )
                            }
                            playerCards={playerCards}
                        />
                    );
                })}
        </div>
    );
};
