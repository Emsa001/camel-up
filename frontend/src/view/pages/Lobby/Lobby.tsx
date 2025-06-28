import React, { useState, useEffect, useMemo } from "react";
import { useLobby } from "@hooks";
import { Button } from "react-daisyui";
import { SelectLobby } from "@components/Lobby/SelectLobby";
import { GameCard } from "@components/Lobby/GameCard";
import { CreateGame } from "@components/Lobby/CreateGame";
import { CheckGame } from "@components/Lobby/CheckGame";
import type { Game } from "@src/types";
import { useAudio, TrackEntry } from "@providers";

export type LobbyState = "JOIN" | "CHECK" | "CREATE";

const Lobby = () => {
    const { gameList, handleCreateGame, handleJoinGame } = useLobby();
    const [state, setState] = useState<LobbyState>("JOIN");
    const [checkGame, setCheckGame] = useState<Game | null>(null);

    const {
        trackList,
        currentTrack,
        setCurrentTrack,
        musicOn,
        toggleMusic,
    } = useAudio();

    // Nur Lobby-Tracks filtern
    const lobbyTracks = useMemo<TrackEntry[]>(
        () => trackList.filter(t => t.key.startsWith("Lobby/")),
        [trackList]
    );

    // Zufälligen Track beim Laden auswählen
    useEffect(() => {
        if (lobbyTracks.length === 0) return;
        if (lobbyTracks.some(t => t.key === currentTrack)) return;

        const idx = Math.floor(Math.random() * lobbyTracks.length);
        setCurrentTrack(lobbyTracks[idx].key);
    }, [lobbyTracks, currentTrack, setCurrentTrack]);

    const goBack = () => {
        setState("JOIN");
        setCheckGame(null);
    };

    return (
        <div className="w-screen h-screen text-white p-6 flex flex-col gap-4">
            {/* Musiksteuerung oben */}
            <div className="flex items-center justify-between bg-dark-primary p-3 rounded-lg">
                <div className="flex items-center gap-4">
                    <Button
                        size="sm"
                        onClick={toggleMusic}
                        className={`${musicOn ? 'bg-green-600' : 'bg-red-600'} hover:opacity-90`}
                    >
                        {musicOn ? "🔊 Music On" : "🔇 Music Off"}
                    </Button>

                    <div className="flex items-center gap-2">
                        <span>Track:</span>
                        <select
                            value={currentTrack}
                            onChange={(e) => setCurrentTrack(e.target.value)}
                            className="bg-dark-secondary text-light-primary p-1 rounded"
                        >
                            {lobbyTracks.map((t) => (
                                <option key={t.key} value={t.key}>
                                    {t.name}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>

                <h1 className="text-xl font-bold text-gold">Game Lobby</h1>
            </div>

            {/* Hauptinhalt - Behält dein ursprüngliches Layout bei */}
            <div className="flex flex-1 gap-4">
                <div className="w-1/3 bg-purple-dark border border-dark-primary border-2 rounded-xl p-4 shadow-lg flex flex-col justify-between">
                    <div className="overflow-y-auto h-full px-1">
                        <h2 className="text-lg font-semibold mb-4">Available Games</h2>
                        {gameList?.map((game) => (
                            <GameCard
                                key={game.id}
                                game={game}
                                setCheckGame={setCheckGame}
                                setState={setState}
                            />
                        ))}
                    </div>
                    <Button
                        onClick={() => setState("CREATE")}
                        className="text-white bg-gold border-none bg-opacity-60 hover:bg-opacity-100 hover:bg-gold"
                    >
                        Create New Game
                    </Button>
                </div>

                <div className="w-2/3 bg-purple-dark border border-dark-primary border-2 rounded-xl p-4 shadow-lg flex flex-col justify-between">
                    {state === "JOIN" && <SelectLobby handleJoinGame={handleJoinGame} />}
                    {state === "CHECK" && <CheckGame handleJoinGame={handleJoinGame} checkGame={checkGame} goBack={goBack} />}
                    {state === "CREATE" && <CreateGame handleCreateGame={handleCreateGame} goBack={goBack}  />}
                </div>
            </div>
        </div>
    );
};

export default Lobby;