import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

import { useGame, useAlert } from "@hooks";

import ApiService from "@services/api";
import service from "@services/websockets/socket";

import { subscribeToGamesList } from "@services/websockets/subscriptions/gameList";
import { Game, Player, RawGame, RawPlayer } from "@src/types";

export type LobbyStateType = "JOIN" | "CHECK" | "CREATE";

export const useLobby = () => {
    const navigate = useNavigate();
    const { setRoomCode, setPlayer } = useGame();
    const { addAlert } = useAlert();

    const [gameList, setGameList] = useState<Game[]>([]);
    const [selectedGame, setSelectedGame] = useState<Game | null>(null);
    const [lobbyState, setLobbyState] = useState<LobbyStateType>("JOIN");

    const handleJoinGame = async (roomCode: string, player: RawPlayer) => {
        if (!player) {
            toast.error("Player is not registered");
            return;
        }

        setRoomCode(null);
        const response = await ApiService.joinGame(player, roomCode);
        if (response.status !== 200) {
            toast.error(response.data.error || "Failed to join game");
            return;
        }

        const userPlayer = response.data.players.find((p: Player) => p.name === player.name);
        userPlayer.gameId = response.data.id;
        
        setPlayer(userPlayer);
        service.addPlayer(userPlayer);
        await navigate(`/game/${roomCode}`);
    };

    const handleCreateGame = async (player: RawPlayer, game: RawGame) => {
        const response = await ApiService.createGame(player, game);
        if (response.status != 201) {
            toast.error(response.data.error || "Failed to create game");
            return;
        }

        const { host, roomCode } = response.data;
        host.gameId = response.data.id;

        setPlayer(host);
        service.addPlayer(host);
        await navigate(`/game/${roomCode}`);
    };

    /*

        We hollow this approach:

        1. Fetch the initial game list using the REST API.
        2. Subscribe to the game list updates using WebSocket.
        3. On any new change update user using websocket.

    */

    const handleSubscribeToGameList = async () => {
        // Fetch initial game list with rest api
        const response = await ApiService.allGames();
        if (response.status != 200) {
            addAlert({
                message: "❗️ Server connection error, please try again later.",
                status: "error",
            });
            return;
        }

        const data = response.data;
        setGameList(data);

        // Subscribe to game list updates
        await subscribeToGamesList(setGameList);
    };

    // when is rendered first time
    useEffect(() => {
        handleSubscribeToGameList();

        // Unsubscribe from game list updates when component is unmounted
        return () => {
            service.unsubscribe("/topic/games");
        };
    }, []);

    return {
        lobbyState,
        setLobbyState,

        selectedGame,
        setSelectedGame,

        gameList,
        handleCreateGame,
        handleJoinGame,
    };
};
