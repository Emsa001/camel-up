import { CamelColor } from "./camel";
export type BetType = "WINNER" | "LOSER";
export interface BettingCard {
    camel: CamelColor;
    value: number;
    playerId: number | null;
    label: number;
    order:number;
    betType: BetType
}