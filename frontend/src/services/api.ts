import { CharacterName, Player, RawGame, RawPlayer } from "@src/types";
import axios, { AxiosResponse } from "axios";
import { toast } from "react-toastify";

export const REACT_APP_HOST = process.env.REACT_APP_HOST || "http://localhost:8000";
export const REACT_APP_API_HOST = `${REACT_APP_HOST}/api/v1`;

class ApiService {
    async allGames(): Promise<AxiosResponse> {
        try {
            const response = await axios.get(`${REACT_APP_API_HOST}/game/all`);
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async getGame(roomCode: string): Promise<AxiosResponse> {
        try {
            const response = await axios.get(`${REACT_APP_API_HOST}/game/${roomCode}`);
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async createGame(host: RawPlayer, game: RawGame): Promise<AxiosResponse> {
        try {
            const response = await axios.post(`${REACT_APP_API_HOST}/game/create`, {
                host,
                ...game,
            });
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async joinGame(player: RawPlayer, roomCode: string): Promise<AxiosResponse> {
        try {
            const response = await axios.post(
                `${REACT_APP_API_HOST}/game/join/${roomCode}`,
                player
            );
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async leaveGame(player: Player): Promise<AxiosResponse> {
        try {
            const response = await axios.post(`${REACT_APP_API_HOST}/game/leave`, player);
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async restartGame(player: Player): Promise<AxiosResponse> {
        try {
            const response = await axios.post(`${REACT_APP_API_HOST}/game/restart`, player);
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async startGame(player: Player): Promise<AxiosResponse> {
        try {
            const response = await axios.post(`${REACT_APP_API_HOST}/game/start`, player);
            return response;
        } catch (error: any) {
            return error.response || error;
        }
    }

    async updatePlayerCharacter(playerId: number, character: CharacterName): Promise<void> {
        try {
            const response = await axios.post(
                `${REACT_APP_API_HOST}/player/${playerId}/character`,
                JSON.stringify(character),
                {
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "*/*",
                    },
                }
            );

            if (response.status !== 200) {
                throw new Error("Character update failed");
            }

            toast.success("Character updated!");
        } catch (error) {
            let errorMessage = "Unknown error";
            if (error instanceof Error) {
                errorMessage = error.message;
            } else if ((error as any)?.response?.data?.message) {
                errorMessage = (error as any).response.data.message;
            }
            toast.error(`Error: ${errorMessage}`);
            throw error;
        }
    }
}

const instance = new ApiService();
export default instance;
