import React from "react";
import { Button, Loading, Progress } from "react-daisyui";

import gameConfig from "@src/config/game.json";

import { useGame } from "@hooks";
import { LogoBox } from "@components/Logo/Box";
import { PlayerBox1 } from "@components/Player/Box";
import { CharacterSelection } from "./CharacterSelection";
import { Player } from "@src/types";

const Header = ({ canStart }: { canStart: boolean }) => {
    return (
        <div className="flex flex-col items-center justify-center">
            <LogoBox className="opacity-80 mb-16" />

            {!canStart ? (
                <div className="flex flex-col items-center justify-center gap-6 h-[100px]">
                    <span className="text-white font-bold">Waiting for players</span>
                    <Loading variant="dots" className="text-main" size="lg" />
                </div>
            ) : (
                <div className="flex flex-col items-center justify-center gap-6 h-[100px]">
                    <span className="text-white font-bold">All players are ready</span>
                </div>
            )}
        </div>
    );
};

const ActionButtons = ({
    player,
    canStart,
    onStart,
    onLeave,
}: {
    player: Player;
    canStart: boolean;
    onStart: () => void;
    onLeave: () => void;
}) => {
    const { hostId, players } = useGame();

    // Fehlende Characters finden
    const missingCharacters = players.filter((p) => !p.character).map((p) => p.name);

    const isHost = player.id === hostId;

    return (
        <div className="mt-auto">
            {canStart && !isHost && (
                <p className="text-center">Wait for the host to start the game...</p>
            )}

            {/* Neue Fehlermeldung */}
            {!canStart && isHost && (
                <div className="text-center text-red-400 mb-2">
                    {players.length < gameConfig.minPlayers
                        ? `Need ${gameConfig.minPlayers - players.length} more players`
                        : `Missing characters: ${missingCharacters.join(", ")}`}
                </div>
            )}

            <div className="flex items-center justify-center gap-2 mt-2">
                {isHost && (
                    <Button
                        color="success"
                        className="text-white w-[150px] bg-opacity-50 border-none disabled:bg-opacity-20 disabled:bg-green-500 disabled:text-gray-400"
                        onClick={onStart}
                        disabled={!canStart}
                    >
                        Start Game
                    </Button>
                )}

                <Button
                    className="text-white w-[150px] bg-opacity-50 border-none"
                    color="error"
                    onClick={onLeave}
                >
                    Leave
                </Button>
            </div>
        </div>
    );
};

const PlayersList = ({ players, player }: { players: Player[]; player: Player }) => {
    return (
        <div className="lg:w-1/2 flex flex-col items-center justify-center gap-2">
            <Progress
                className=""
                color="warning"
                max={gameConfig.maxPlayers}
                value={players.length}
            />
            <p className="text-center [word-spacing:10px] mt-3">
                <span className="text-green-400">{players.length}</span> of{" "}
                <span className="text-yellow-400">{gameConfig.maxPlayers}</span> Players
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-x-8 gap-y-4 mt-4">
                {players.map((p) => (
                    <PlayerBox1
                        key={p.id}
                        player={p}
                        className={`${
                            p.id === player.id ? "bg-yellow-mat" : "bg-blue-mat"
                        } w-[150px]`}
                    />
                ))}
            </div>
        </div>
    );
};

export const WaitingRoom = () => {
    const { hostId, player, players, handleStartGame, handleLeaveGame } = useGame();
    if (!player) return null;

    // Neue Logik für Host-Überprüfung
    const isHost = hostId === player.id;

    // Überprüfung mit zusätzlicher Host-Bedingung
    const canStart = players.length >= gameConfig.minPlayers && players.every((p) => p.character);

    return (
        <div className="w-screen h-screen py-12 flex flex-col gap-4 justify-center items-center">
            <Header canStart={canStart} />
            {players.length >= gameConfig.minPlayers && <CharacterSelection players={players} />}
            <PlayersList players={players} player={player} />
            <ActionButtons
                player={player}
                canStart={canStart}
                onStart={handleStartGame}
                onLeave={handleLeaveGame}
            />
        </div>
    );
};
