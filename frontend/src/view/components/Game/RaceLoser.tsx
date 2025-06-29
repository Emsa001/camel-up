import React from "react";
import { useGame } from "@hooks";
import { CharacterIcon } from "@components/Leaderboard/CharacterIcon";
import { setRacePositions } from "@components/Camel/BoardCamels";

export const RaceLoser = () => {
    const { raceBettingCards, camels, players } = useGame();

    const sortedCamels = setRacePositions(camels).reverse();
    const raceLoserCamel = sortedCamels.length > 0 ? sortedCamels[0] : null;

    if (!raceBettingCards || raceBettingCards.length === 0) return null;

    return (
        <div className="text-gray-800 flex flex-col gap-3 mt-3 p-4 bg-yellow-dark rounded-lg shadow-md">
            <h3 className="text-xl font-semibold text-gray-800 mb-2">Race Loser Bets</h3>

            {sortedCamels.map((camel) => {
                const betsForThisCamel = raceBettingCards
                    .filter(
                        (b) =>
                            b.playerId !== null &&
                            b.betType === "LOSER" &&
                            b.camel === camel.color
                    )
                    .sort((a, b) => a.order - b.order);

                return (
                    <div
                        key={camel.color}
                        className=" bg-amber-200 flex items-center gap-4 mb-0 p-2 rounded-md border border-yellow-dark min-h-24"
                    >
                        <div className="flex items-center gap-3 min-w-[160px]">
                            <img
                                src={`/camel/icons/${camel.color.toLowerCase()}.png`}
                                alt={`${camel.color} Camel`}
                                className="object-contain border-2 border-yellow-dark rounded-2xl max-h-[70px]"
                            />
                            <h4 className="text-lg font-semibold text-gray-800 capitalize">Bets:</h4>
                        </div>

                        {betsForThisCamel.length > 0 ? (
                            <div className="flex flex-wrap gap-1.5">
                                {betsForThisCamel.map((b, index) => {
                                    const player = players.find((p) => p.id === b.playerId);
                                    const playerCharacterName = player?.character || "Unknown";

                                    let pointsDisplay = "-1";
                                    if (camel === raceLoserCamel) {
                                        if (index === 0) pointsDisplay = "+8";
                                        else if (index === 1) pointsDisplay = "+5";
                                        else if (index === 2) pointsDisplay = "+3";
                                        else if (index === 3) pointsDisplay = "+2";
                                        else pointsDisplay = "+1";
                                    }

                                    if (playerCharacterName === "Unknown") return null;

                                    return (
                                        <div
                                            key={index}
                                            className="flex flex-col items-center relative"
                                            style={
                                                betsForThisCamel.length >= 4
                                                    ? {
                                                        marginLeft: index === 0 ? "0px" : "-24px",
                                                        zIndex: betsForThisCamel.length - index,
                                                    }
                                                    : {}
                                            }
                                        >
                                            <div
                                                className={`font-bold text-sm text-center ${
                                                    pointsDisplay.startsWith("+")
                                                        ? "text-green-primary"
                                                        : "text-red-400"
                                                }`}
                                            >
                                                {pointsDisplay}
                                            </div>
                                            <CharacterIcon
                                                character={playerCharacterName}
                                            />
                                        </div>
                                    );
                                })}
                            </div>
                        ) : (
                            <p className="text-gray-800 italic">No bets placed</p>
                        )}
                    </div>
                );
            })}
        </div>
    );
};
