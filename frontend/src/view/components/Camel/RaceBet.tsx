import React, { useState } from "react";
import { useGame } from "@hooks";
import { toast } from "react-toastify";
import { Player } from "@src/types";
import WebSocketService from "@src/services/websockets/socket";

export const CamelBetRace = () => {
    const { currentPlayerId, player, roomCode } = useGame();

    const [winMenuVisible, setWinMenuVisible] = useState(false);
    const [loseMenuVisible, setLoseMenuVisible] = useState(false);
    const [White, setWhite] = useState(true);
    const [Blue, setBlue] = useState(true);
    const [Orange, setOrange] = useState(true);
    const [Yellow, setYellow] = useState(true);
    const [Green, setGreen] = useState(true);

    const handleWinToggle = () => {
        setWinMenuVisible((prev) => !prev);
        setLoseMenuVisible(false);
    };

    const handleRaceBet = async (color: string, type: "winner" | "loser") => {
        await WebSocketService.publish({
            destination: `/app/game/${roomCode}/race-bet`,
            message: {
                camel: color.toUpperCase(),
                betType: type.toUpperCase(),
                playerId: player?.id,
            },
        });
    };

    //Sends the RaceBet to winner stack in back end
    const handleWinItemClick = (item: string) => {
        const updatedPlayer = { ...player, color: item } as Player;
        setWinMenuVisible(false); // hide the menu after click
        if (currentPlayerId !== player?.id) {
            //ckeck if player is on turn
            toast.error("It's not your turn!");
            return;
        }
        if (item === "White") {
            setWhite(false);
        } else if (item === "Blue") {
            setBlue(false);
        } else if (item === "Orange") {
            setOrange(false);
        } else if (item === "Yellow") {
            setYellow(false);
        } else if (item === "Green") {
            setGreen(false);
        }

        handleRaceBet(item, "winner");
    };

    const handleLoseToggle = () => {
        setLoseMenuVisible((prev) => !prev);
        setWinMenuVisible(false);
    };

    //Sends the RaceBet to loser stack in back end
    const handleLoseItemClick = (item: string) => {
        const updatedPlayer = { ...player, color: item } as Player;
        setLoseMenuVisible(false); // hide the menu after click
        if (currentPlayerId !== player?.id) {
            toast.error("It's not your turn!");
            return;
        }
        if (item === "White") {
            setWhite(false);
        } else if (item === "Blue") {
            setBlue(false);
        } else if (item === "Orange") {
            setOrange(false);
        } else if (item === "Yellow") {
            setYellow(false);
        } else if (item === "Green") {
            setGreen(false);
        }

        handleRaceBet(item, "loser");
    };

    return (
        <div className="w-full h-full flex flex-col items-center justify-evenly px-4 gap-2 relative">
            <img
                src="/gameimg/raceWinner.png"
                alt="Race Winner"
                width={180}
                onClick={handleWinToggle}
                className="border-4 border-blue-mat rounded-lg object-contain max-h-full cursor-pointer hover:scale-105 transition-transform duration-200 ease-in-out"
            />

            {winMenuVisible && (
                <div className="absolute left-10 top-20 translate-y-10 bg-yellow-secondary border-yellow-dark border-2 rounded-lg shadow-lg w-48 py-2">
                    <div
                        className={
                            White
                                ? "px-4 py-2 bg-gray-100 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-gray-100 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleWinItemClick("White")}
                    >
                        White
                    </div>
                    <div
                        className={
                            Blue
                                ? "px-4 py-2 bg-blue-800 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-blue-800 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleWinItemClick("Blue")}
                    >
                        Blue
                    </div>
                    <div
                        className={
                            Orange
                                ? "px-4 py-2 bg-orange-600 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-orange-600 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleWinItemClick("Orange")}
                    >
                        Orange
                    </div>
                    <div
                        className={
                            Yellow
                                ? "px-4 py-2 bg-yellow-300 rounded-lg hover:scale-105 text-black border-yellow-dark border-2  cursor-pointer"
                                : "px-4 py-2 bg-yellow-300 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleWinItemClick("Yellow")}
                    >
                        Yellow
                    </div>
                    <div
                        className={
                            Green
                                ? "px-4 py-2 bg-green-500 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-green-500 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleWinItemClick("Green")}
                    >
                        Green
                    </div>
                </div>
            )}

            <img
                src="/gameimg/raceLoser.png"
                alt="TExt?"
                width={180}
                className="border border-4 border-red-500 rounded-lg object-contain max-h-full cursor-pointer hover:scale-105 transition-transform duration-200 ease-in-out"
                onClick={handleLoseToggle}
            />

            {loseMenuVisible && (
                <div className="absolute left-10 top-64 translate-x-0 bg-yellow-secondary border-yellow-dark border-2 rounded-lg shadow-lg w-48 py-2">
                    <div
                        className={
                            White
                                ? "px-4 py-2 bg-gray-100 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-gray-100 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleLoseItemClick("White")}
                    >
                        White
                    </div>
                    <div
                        className={
                            Blue
                                ? "px-4 py-2 bg-blue-800 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-blue-800 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleLoseItemClick("Blue")}
                    >
                        Blue
                    </div>
                    <div
                        className={
                            Orange
                                ? "px-4 py-2 bg-orange-600 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-orange-600 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleLoseItemClick("Orange")}
                    >
                        Orange
                    </div>
                    <div
                        className={
                            Yellow
                                ? "px-4 py-2 bg-yellow-300 rounded-lg hover:scale-105 text-black border-yellow-dark border-2  cursor-pointer"
                                : "px-4 py-2 bg-yellow-300 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleLoseItemClick("Yellow")}
                    >
                        Yellow
                    </div>
                    <div
                        className={
                            Green
                                ? "px-4 py-2 bg-green-500 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 cursor-pointer"
                                : "px-4 py-2 bg-green-500 rounded-lg hover:scale-105 text-black border-yellow-dark border-2 opacity-50 cursor-not-allowed"
                        }
                        onClick={() => handleLoseItemClick("Green")}
                    >
                        Green
                    </div>
                </div>
            )}
        </div>
    );
};
