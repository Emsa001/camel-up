import React, { ReactNode, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { GameContext } from "@hooks";
import service from "@services/websockets/socket";
import ApiService from "@services/api";
import { subscribeToGameState } from "@src/services/websockets/subscriptions/gameState";
import { subscribeToBet } from "@services/websockets/subscriptions/Bet";
import { BettingCard, Camel, CharacterName, DesertTile, Game, GameState, Player } from "@src/types";

export const GameProvider = ({ children }: { children: ReactNode }) => {
    const navigate = useNavigate();

    const [id, setId] = useState<number | undefined>(undefined);
    const [state, setState] = useState<GameState>("WAITING");
    const [players, setPlayers] = useState<Player[]>([]);
    const [hostId, setHostId] = useState<number | null>(null);
    const [turn, setTurn] = useState<number>(0);
    const [currentPlayerId, setCurrentPlayerId] = useState<number | null>(null);
    const [round, setRound] = useState<number>(0);
    const [camels, setCamels] = useState<Camel[]>([]);

    const [loading, setLoading] = useState(false);
    const [player, setPlayer] = useState<Player | null>(null);
    const [roomCode, setRoomCode] = useState<string | null>("");
    const [name, setName] = useState<string>("");
    const [isPrivate, setIsPrivate] = useState<boolean>(false);

    const [legBettingCards, setLegBettingCards] = useState<BettingCard[]>([]);
    const [raceBettingCards, setRaceBettingCards] = useState<BettingCard[]>([]);

    const [desertTiles, setDesertTiles] = useState<DesertTile[]>([]);

    const [selectedCharacter, setSelectedCharacter] = useState<CharacterName | null>(null);

    const setCharacter = (character: CharacterName) => {
        if (player && id) {
            const updatedPlayer = { ...player, character, gameId: id };
            setPlayer(updatedPlayer);
            service.updatePlayer(updatedPlayer);
        }
    };

    const handleStartGame = async () => {
        if (!player) {
            toast.error("You must be a player to start the game.");
            return;
        }

        const error = await ApiService.startGame(player);
        if (error) {
            toast.error(error.data);
            return;
        }
    };

    const handleLeaveGame = async () => {
        if (!player) return;

        service.unsubscribe(`/topic/game/${roomCode}`);
        await ApiService.leaveGame(player);
        navigate("/");
    };

    const handleRestart = async () => {
        if (!player) return;
        await ApiService.restartGame(player);
    };

    const updateGameState = async (game: Game) => {
        if (!player) return;

        console.log("Updating game state:", game);

        if (game.id !== undefined) setId(game.id);
        if (game.state !== undefined) setState(game.state);
        if (game.players !== undefined) setPlayers(game.players);
        if (game.turn !== undefined) setTurn(game.turn);
        if (game.currentPlayerId !== undefined) setCurrentPlayerId(game.currentPlayerId);
        if (game.round !== undefined) setRound(game.round);
        if (game.camels !== undefined) setCamels(game.camels.sort((a, b) => a.color.localeCompare(b.color)));
        if (game.roomCode !== undefined) setRoomCode(game.roomCode);
        if (game.isPrivate !== undefined) setIsPrivate(game.isPrivate);
        if (game.legBettingCards !== undefined) setLegBettingCards(game.legBettingCards || []);
        if (game.raceBettingCards !== undefined) setRaceBettingCards(game.raceBettingCards || []);
        if (game.desertTiles !== undefined) setDesertTiles(game.desertTiles || []);
        if (game.hostId !== undefined) setHostId(game.hostId || null);

        if (game.players) {
            // Merge mit bestehenden Player-Daten
            setPlayers((prev) =>
                game.players!.map((newPlayer) => ({
                    ...(prev.find((p) => p.id === newPlayer.id) || {}),
                    ...newPlayer,
                }))
            );

            // Aktuellen Spieler aktualisieren
            if (player) {
                const updatedPlayer = game.players.find((p) => p.id === player.id);
                updatedPlayer!.gameId = game.id || null;
                if (updatedPlayer) setPlayer(updatedPlayer);
            }
        }
    };

    const handleWebSocketSubscription = async () => {
        if (!player || !roomCode) return;

        setLoading(true);

        const response = await ApiService.getGame(roomCode);
        if (response.status !== 200) {
            toast.error("Error fetching game data");
            return;
        }
        updateGameState(response.data);

        await subscribeToGameState(roomCode, (game: Game) => {
            // Füge diesen Handler hinzu
            if (game.players) {
                const currentPlayer = game.players.find((p) => p.id === player?.id);
                if (currentPlayer?.character) {
                    setSelectedCharacter(currentPlayer.character);
                }
            }
            updateGameState(game);
        });
        setLoading(false);
    };

    useEffect(() => {
        if (!roomCode) return;

        subscribeToBet(roomCode);

        return () => {
            service.unsubscribe(`/topic/game/${roomCode}/handleBetting`);
        };
    }, [roomCode]);

    useEffect(() => {
        handleWebSocketSubscription();

        return () => {
            if (!roomCode) return;
            service.unsubscribe(`/topic/game/${roomCode}`);
        };
    }, [roomCode]);

    return (
        <GameContext.Provider
            value={{
                id,
                state,
                players,
                turn,
                currentPlayerId,
                round,
                camels,
                setId,
                setState,
                setPlayers,
                setTurn,
                setCurrentPlayerId,
                setRound,

                legBettingCards,
                setLegBettingCards,

                // Add default value for raceBettingCards
                raceBettingCards,
                setRaceBettingCards,

                desertTiles,
                setDesertTiles,

                roomCode,
                setRoomCode,

                player,
                setPlayer,

                name,
                isPrivate,
                loading,

                setName,
                setIsPrivate,

                selectedCharacter,
                setCharacter,

                handleStartGame,
                handleLeaveGame,
                handleRestart,

                hostId,
            }}
        >
            {children}
        </GameContext.Provider>
    );
};
