import React from "react";
import { LobbyStateType } from "@hooks";
import { Game } from "@src/types";

interface GameCardProps {
    game: Game;
    setCheckGame: (game: Game | null) => void;
    setState: (state: LobbyStateType) => void;
}

export function GameCard({ game, setCheckGame, setState }: GameCardProps) {
    const getStateColor = (state: string) => {
        switch (state) {
            case "WAITING":
                return "text-green-500";
            case "IN_PROGRESS":
                return "text-yellow-500";
            case "FINISHED":
                return "text-blue-500";
            default:
                return "text-gray-500";
        }
    };
    const handleClick = () => {
        setCheckGame(game);
        setState("CHECK");
    };

    return (
        <div
            className="w-full bg-purple-light rounded-lg p-4 mb-4 shadow-md hover:bg-purple-primary cursor-pointer transition duration-200 border border-white border-opacity-25 border-1"
            onClick={handleClick}
        >
            <div className="flex justify-between items-center">
                <div>
                    <p className="text-white text-md font-medium">{game.name}</p>
                    <p className="text-gray-400 mt-2 text-sm">Players: {game.players.length}/8</p>
                </div>
                <span className={`text-sm font-semibold ${getStateColor(game.state)}`}>
                    {game.state}
                </span>
            </div>
        </div>
    );
}
