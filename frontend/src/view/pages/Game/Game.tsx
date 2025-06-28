import React, { useEffect, useState, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import gameConfig from "@src/config/game.json";

import { WaitingRoom } from "@components/Game/Waiting";
import { OptionsMenu } from "@components/Game/OptionsMenu";
import { MusicMenu } from "@components/Game/MusicMenu";
import { EndScore } from "@components/Game/EndScore";
import { FullScreenLoader } from "@components/Loader";

import { useGame } from "@hooks";
import GameRoom from "@components/Game/GameRoom";
import { useAudio, TrackEntry } from "@providers";

const Game = () => {
    const navigate = useNavigate();
    const { roomCode } = useParams();
    const { id, state, loading, player, players, setRoomCode, handleLeaveGame, handleRestart } = useGame();

    const { trackList, currentTrack, setCurrentTrack, musicOn, toggleMusic, soundOn, toggleSound } =
        useAudio();

    // Nur Game-Tracks filtern
    const gameTracks = useMemo<TrackEntry[]>(
        () => trackList.filter((t) => t.key.startsWith("Game/")),
        [trackList]
    );

    // Beim ersten Laden einen zufälligen Track wählen
    useEffect(() => {
        if (!gameTracks.length) return;
        if (gameTracks.some((t) => t.key === currentTrack)) return;
        const idx = Math.floor(Math.random() * gameTracks.length);
        setCurrentTrack(gameTracks[idx].key);
    }, [gameTracks, currentTrack, setCurrentTrack]);

    // Set room code when component mounts or roomCode changes
    useEffect(() => {
        setRoomCode(roomCode || null);
    }, [roomCode, setRoomCode]);

    // EndScore-Logik
    const [showEndScore, setShowEndScore] = useState(false);
    useEffect(() => {
        if (!id) return;

        if (state === "FINISHED") {
            if (players.length < gameConfig.minPlayers) {
                toast.error("The game has ended because there are not enough players to continue.");
            }
            setTimeout(() => {
                setShowEndScore(true);
            }, 1500);
        } else {
            setShowEndScore(false);
        }
    }, [state, id, players]);

    // Redirect to lobby if player not found
    useEffect(() => {
        if (!player) {
            navigate("/lobby");
        }
    }, [player, navigate]);

    // Show loader while game data is loading
    if (loading) return <FullScreenLoader />;

    // Handle edge cases where game or player data is missing
    if (!id || !player) return null;

    // Show waiting room if game is in "WAITING" state
    if (state === "WAITING") return <WaitingRoom />;

    return (
        <div className="w-screen h-screen flex items-center justify-center bg-purple-dark">
            <div className="relative flex flex-col items-center justify-center h-screen w-screen gap-4 overflow-hidden max-w-[1600px] max-h-[1000px] 2xl:border-2 border-yellow-dark rounded-lg bg-yellow-light">
                <div className="absolute top-6 left-6 flex items-center gap-2 z-20 text-white">
                    <OptionsMenu onRestart={handleRestart} onLeave={handleLeaveGame} />
                    <MusicMenu />
                </div>

                {!showEndScore ? (
                    <GameRoom /> // Main game room display
                ) : (
                    <EndScore /> // Show end score when the game is finished
                )}
            </div>
        </div>
    );
};

export default Game;
