import { BettingCard, Camel, CharacterName, DesertTile, Game, GameState, Player } from "@src/types";
import { createContext, useContext } from "react";

interface GameContextType extends Game {

    setId: (id: number) => void;
    setState: (state: GameState) => void;
    setPlayers: (players: Player[]) => void;
    setTurn: (turn: number) => void;
    setCurrentPlayerId: (currentPlayer: number | null) => void;
    setRound: (round: number) => void;
    setRoomCode: (roomCode: string | null) => void;
    setName: (name: string) => void;
    setIsPrivate: (isPrivate: boolean) => void;

    setLegBettingCards: (cards: BettingCard[]) => void;
    setRaceBettingCards: (cards: BettingCard[]) => void;

    setDesertTiles: (tiles: DesertTile[]) => void;
    
    
    player: Player | null;
    setPlayer: (player: Player) => void;

    selectedCharacter: CharacterName | null;
    setCharacter: (character: CharacterName) => void;

    loading: boolean;

    handleStartGame: () => void;
    handleLeaveGame: () => void;
    handleRestart:   () => void;
}


export const GameContext = createContext<GameContextType | undefined>(undefined);

export const useGame = () => {
    const context = useContext(GameContext);
    if (context === undefined) {
        throw new Error("useGame must be used within a GameProvider");
    }
    return context;
};
