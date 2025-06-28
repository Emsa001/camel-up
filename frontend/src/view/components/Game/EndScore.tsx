import { useGame } from "@hooks";
import React from "react";
import {RaceWinner} from "@components/Game/RaceWinner";
import {RaceLoser} from "@components/Game/RaceLoser";
import {CharacterIcon} from "@components/Leaderboard/CharacterIcon";
import {Button} from "react-daisyui";
import {HammerIcon} from "@components/Player/PlayerProfile";
import {PlayerPopup} from "@components/Leaderboard/PlayerPopup"; // Make sure this path is correct based on your project structure

export const EndScore = () => {
    const { raceBettingCards, players, camels, handleRestart, player, hostId } = useGame();

    const sortedPlayers = players.sort((a, b) => b.money - a.money);
    const isHost = player?.id === hostId;

    return (

        <div className="grid grid-cols-[auto_1fr_auto] items-start h-full w-full text-gray-800 gap-6 p-8"
             style={{
                 backgroundImage: "url('/background/team.png')",
                 backgroundSize: "cover",
                 backgroundPosition: "center",
             }}
        >
            {/* Left Section for RaceWinner - will be flush left within the parent container */}
            <div className="flex flex-col gap-6 mt-24 ">
                <RaceWinner />
            </div>

            {/* Middle Section: Game Over and Results - will be perfectly centered in the remaining space */}
            <div className="flex flex-col items-center justify-center gap-6 mx-auto">

                <div className="w-full max-w-md bg-yellow-dark rounded-lg shadow-md p-4">
                    <h2 className="text-2xl font-semibold text-gray-800 mb-4">Result</h2>
                    <ul className="space-y-3">
                        {sortedPlayers.map((gamePlayer, index) => (
                            <li
                                key={gamePlayer.id}
                                className={`flex items-center justify-between p-2 rounded-lg ${
                                    index === 0 ? "bg-amber-200" : "bg-amber-200"
                                }`}
                            >
                                <div className="relative" >
                                    {index===0 && (
                                        <span className="absolute -top-[10px] -left-[5px] -rotate-12 text-xl z-[20]">👑</span>
                                    )}
                                    <CharacterIcon character={gamePlayer.character} />
                                </div>
                                <div className="flex items-center">
                                    <div className="text-center font-medium text-black ">{gamePlayer.name}
                                    </div>
                                </div>
                                <span className="text-black font-semibold">💰{gamePlayer.money}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>


            <div className="flex flex-col gap-6 mt-24 ">
                <RaceLoser />
            </div>
        </div>
    );
};