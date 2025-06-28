export type CamelColor = "BLUE" | "YELLOW" | "GREEN" | "ORANGE" | "WHITE";

export interface Camel {
    color: CamelColor;

    position: number;

    stackPosition: number;
    racePosition: number;
    lastRoll: number;

    moved: boolean;
    legWinner: boolean;
    raceWinner: boolean;
    rawPosition: number;
}
