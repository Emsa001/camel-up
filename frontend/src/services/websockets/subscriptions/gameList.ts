import { IMessage } from "@stomp/stompjs";
import service from "@services/websockets/socket";
import { Game } from "@src/types";

export const subscribeToGamesList = async (
    setGamesList: (React.Dispatch<React.SetStateAction<Game[]>>),
) => {
    return await service.subscribe("/topic/games", (message: IMessage) => {
        const game: Game = JSON.parse(message.body);

        console.log("Debug: Game list updated:", game);

        // Update the game list
        setGamesList((prev) => {
            const existingGameIndex = prev.findIndex((g) => g.id === game.id);

            // if game is already in the list, update it
            if (existingGameIndex !== -1) {

                // if the game is empty, remove it from the list
                if(game.players.length == 0){
                    return prev.filter((g) => g.id !== game.id);
                }

                const updatedGames = [...prev];
                updatedGames[existingGameIndex] = game;
                return updatedGames;
            }
            // if game is not in the list, add it
            return [...prev, game];
        });
    });
};
