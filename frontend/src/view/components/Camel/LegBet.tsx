import { useGame } from "@hooks";
import React, { useState } from "react";
import { Stack } from "react-daisyui";
import { toast } from "react-toastify";
import webSocketService from "@services/websockets/socket";

export const LegBet = () => {
    const { currentPlayerId, camels, legBettingCards, player, roomCode } = useGame();

    const handleClick = async (color: string) => {
        if (currentPlayerId !== player?.id) return toast.error("It's not your turn to bet!");

        await webSocketService.publish({
            destination: `/app/game/${roomCode}/leg-bet`,
            message: {
                camel: color,
                playerId: player?.id,
            },
        });
    };

    return (
        <div className="flex items-center justify-center gap-6 w-full bg-yellow-secondary py-4">
            {camels.map((camel) => {
                const initialCardsForCamel = legBettingCards.filter(
                    (card) => card.camel === camel.color
                ).length;

                const takenCardsForCamel = legBettingCards.filter(
                    (card) => card.camel === camel.color && card.playerId !== null
                ).length;

                const remainingCards = initialCardsForCamel - takenCardsForCamel;
                const isStackFinished = remainingCards === 0;

                return (
                    <Stack
                        key={camel.color}
                        className={`stack h-36 w-24 cursor-pointer transition-all ${
                            isStackFinished ? 'bg-gray-400 opacity-60 rounded-lg pointer-events-none' : ''
                        }`}
                        onClick={() => !isStackFinished && handleClick(camel.color)}
                    >
                        {isStackFinished ? (
                            <div className="bg-gray-400 text-white grid place-content-center rounded-lg h-full w-full">
                                Taken
                            </div>
                        ) : (
                            // Rendere die Karten von unten nach oben, beginnend mit der am weitesten unten liegenden (höchsten Index)
                            Array.from({ length: remainingCards }).map((_, i) => (
                                <img
                                    key={`${camel.color}-${i}`}
                                    src={`/camel/cards/${camel.color.toLowerCase()}/${takenCardsForCamel + i + 1 + "v2"}.png`}
                                    alt={`${camel.color} card`}
                                    className="rounded-lg h-full w-full object-cover"
                                />
                            ))
                        )}
                    </Stack>
                );
            })}
        </div>
    );
};