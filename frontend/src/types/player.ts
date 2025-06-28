export type CharacterName = "ALICE_WEIDEL" | "ANGELA_MERKEL" | "BORIS_PISTORIUS" | "CHRISTIAN_LINDNER" | "KARL_LAUTERBACH" | "MARKUS_SOEDER" | "OLAF_SCHOLZ" | "PHILIPP_AMTHOR";

export interface RawPlayer {
    name: string;
    age: number;
}

export interface Player extends RawPlayer {
    id: number;
    money: number;

    gameId: number | null;
    character: CharacterName | null;
}