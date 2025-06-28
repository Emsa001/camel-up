import React, { useState } from "react";
import { Button, Input, Range, Toggle } from "react-daisyui";
import { FaCopy } from "react-icons/fa6";
import { IconBaseProps } from "react-icons";
import { RawGame, RawPlayer } from "@src/types";

interface CreateGameProps {
    goBack: () => void;
    handleCreateGame: (player: RawPlayer, game: RawGame) => Promise<void>;
}

export function CreateGame({ goBack, handleCreateGame }: CreateGameProps) {
    const [gameName, setGameName] = useState("");
    const [isPrivate, setIsPrivate] = useState(false);
    const [playerName, setPlayerName] = useState("");
    const [playerAge, setPlayerAge] = useState<number>(10);

    const handleClick = async () => {
        try {
            const player: RawPlayer = {
                name: playerName,
                age: playerAge,
            }

            const game: RawGame = {
                name: gameName,
                isPrivate: isPrivate,
            };

            await handleCreateGame(player, game);
        } catch (error) {
            console.error("Error creating game:", error);
        }
    };

    return (
        <div className="flex flex-col h-full text-white">
            <div>
                <h2 className="text-2xl font-bold mb-6">Create a New Game</h2>

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Room Name</label>
                    <Input
                        type="text"
                        placeholder="Enter game name"
                        value={gameName}
                        onChange={(e) => setGameName(e.target.value)}
                        className="w-full bg-purple-light"
                    />
                </div>

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Private Game</label>
                    <div className="flex items-center gap-4">
                        <Toggle checked={isPrivate} onChange={() => setIsPrivate((prev) => !prev)} />
                    </div>
                </div>

                {/* {isPrivate && (
                    <div className="mb-4">
                        <label className="block mb-2 text-sm font-medium">Room Code</label>
                        <div className="flex items-center justify-between bg-purple-light px-4 py-2 rounded-lg">
                            <span className="font-mono text-lg tracking-widest">{roomCode}</span>
                            <button
                                onClick={handleCopy}
                                className="flex items-center text-sm text-blue-400 hover:text-blue-300 transition"
                            >
                                <Icon className="text-xl mr-1" />
                            </button>
                        </div>
                    </div>
                )} */}

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Your Name</label>
                    <Input
                        type="text"
                        placeholder="Enter your name"
                        value={playerName}
                        onChange={(e) => setPlayerName(e.target.value)}
                        className="w-full bg-purple-light"
                    />
                </div>

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Your Age</label>
                    <div className="flex items-center gap-4">
                        <Range
                            value={playerAge}
                            size="xs"
                            color="primary"
                            onChange={(e) => setPlayerAge(Number(e.target.value))}
                        />
                        <span className="text-sm">{playerAge}</span>
                    </div>
                </div>
            </div>

            <div className="mt-auto flex gap-2 pt-6">
                <Button className="text-white flex-1 border-none bg-blue-mat bg-opacity-80 hover:bg-blue-mat hover:bg-opacity-100" onClick={handleClick}>
                    Create Game
                </Button>
                <Button color="error" className="text-white" onClick={goBack}>
                    Back
                </Button>
            </div>
        </div>
    );
}
