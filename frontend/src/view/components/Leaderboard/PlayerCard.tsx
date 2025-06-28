import React from "react";
import {BettingCard, Player} from "@src/types";
import { PlayerPopup } from "./PlayerPopup";
import { CharacterIcon } from "./CharacterIcon";
import {HammerIcon} from "@components/Player/PlayerProfile";


type Props = {
    player: Player;
    isHost: boolean;
    isCurrentPlayer: boolean;
    isYou: boolean;
    isPopupOpen: boolean;
    onTogglePopup: () => void;
    playerCards:BettingCard[]

};

export const PlayerCard: React.FC<Props> = ({
    player,
    isHost,
    isCurrentPlayer,
    isYou,
    isPopupOpen,
    onTogglePopup,
    playerCards,

}) => {
    const borderClass = isCurrentPlayer
        ? "bg-yellow-300 border-green-600"
        : "bg-yellow-200 border-yellow-dark";

    return (
        <div
            className={`relative flex items-center justify-between p-0.5 border-2 ${borderClass} rounded-2xl `}
        >
            <div className="relative" onClick={onTogglePopup}>
                {isHost && (
                    <HammerIcon className="absolute -top-[5px] -left-[5px] text-blue-mat text-xl z-[15]"/>
                )}
                <CharacterIcon character={player.character} />
                {isPopupOpen && (
                    <PlayerPopup
                        player={player}
                        isHost={isHost}
                        isYou={isYou}
                        isCurrentPlayer={isCurrentPlayer}
                        playerCards={playerCards}
                    />
                )}
            </div>

            <span>
                {player.name} {isYou && "(you)"}
            </span>
            <span>💰{player.money}</span>
        </div>
    );
};
