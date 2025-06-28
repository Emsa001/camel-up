import React from "react";

import { CamelProvider } from "@providers";
import { useGame, useTiles } from "@hooks";
import { useState } from "react";

import { LegBet } from "@src/view/components/Camel/LegBet";
import { CamelBetRace } from "@src/view/components/Camel/RaceBet";
import { Chat } from "@components/Chat/Chat";

import { GameBoard } from "./GameBoard";
import { DesertTiles } from "./DesertTiles";
import { Pyramid } from "./Pyramid";
import { Dices } from "../Dice/Dices";
import { PlayerProfile } from "../Player/PlayerProfile";
import { PlayerCards } from "./PlayerCards";
import { LeaderBoard } from "../Leaderboard";
import { DesertTile } from "@src/types";

const LeftSidebar = () => {
    const { round } = useGame();

    return (
        <div className="col-span-2 mt-24 bg-yellow-secondary border-yellow-dark border-2 rounded-br-lg rounded-tr-lg">
            <p className="text-black font-semibold text-center"> Leg: {round}</p>
            <LeaderBoard />
        </div>
    );
};

const RightSidebar = ({
    selectedTile,
    onTilePlaced,
}: {
    selectedTile: DesertTile | null;
    onTilePlaced: (tile: DesertTile | null) => void;
}) => {
    return (
        <div className="col-span-2 flex flex-col items-center justify-between bg-yellow-secondary border-yellow-dark border-2 py-2 rounded-tl-lg rounded-bl-lg w-full">
            <CamelBetRace />
            <DesertTiles selectedTile={selectedTile} onSelectTile={onTilePlaced} />
            <Pyramid />
        </div>
    );
};

const MainSection = ({
    addDesertTile,
    desertTiles,
    selectedTile,
    onTilePlaced,
}: {
    addDesertTile: (tile: DesertTile) => void;
    desertTiles: DesertTile[];
    selectedTile: DesertTile | null;
    onTilePlaced: (tile: DesertTile | null) => void;
}) => {
    return (
        <div className="col-span-8 flex flex-col">
            <LegBet />
            <div className="w-full h-full flex items-center justify-center">
                <GameBoard
                    selectedTile={selectedTile}
                    onTilePlaced={onTilePlaced}
                    desertTiles={desertTiles}
                    addDesertTile={addDesertTile}
                />
            </div>
        </div>
    );
};

export default function GameRoom() {
    const { roomCode, player } = useGame();
    const { desertTiles, addDesertTile } = useTiles();
    const [selectedTile, onTilePlaced] = useState<DesertTile | null>(null);

    if (!roomCode || !player) return null;

    return (
        <CamelProvider>
            <div className="w-full h-full grid grid-cols-12 gap-4 mt-3">
                <LeftSidebar />
                <MainSection
                    addDesertTile={addDesertTile}
                    desertTiles={desertTiles}
                    selectedTile={selectedTile}
                    onTilePlaced={() => onTilePlaced(null)}
                />
                <RightSidebar selectedTile={selectedTile} onTilePlaced={onTilePlaced} />
            </div>
            <div className="bg-purple-dark w-full mt-auto grid grid-cols-4 gap-4 items-center text-white px-6 h-[100px]">
                <div className="flex items-center w-full col-span-1 p-1 gap-4">
                    <PlayerProfile player={player} />
                    <Chat roomCode={roomCode} />
                </div>
                <Dices />
                <PlayerCards />
            </div>
            {/* <MusicPlayer /> */}
        </CamelProvider>
    );
}
