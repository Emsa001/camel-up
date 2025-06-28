import { BettingCard, Camel, DesertTile, Player,  } from "./";

export type GameState = "WAITING" | "IN_PROGRESS" | "FINISHED";

export interface RawGame {
    name: string;
    isPrivate: boolean;
}

export interface Game extends RawGame {
    id?: number;
    roomCode: string | null;
    state: GameState;

    round: number;
    turn: number;
    currentPlayerId: number | null;
    hostId: number | null;

    players: Player[];
    camels: Camel[];
    legBettingCards: BettingCard[];
    raceBettingCards: BettingCard[];
    desertTiles: DesertTile[];
}
