import { useGame } from "@hooks";
import { Player } from "@src/types";
import React from "react";
import { IconBaseProps } from "react-icons";
import { FaHammer } from "react-icons/fa";
import {CharacterIcon} from "@components/Leaderboard/CharacterIcon";
/*

This is just a mockup of the PlayerProfile.
TODO: Implement the actual PlayerProfile with the correct logic and styles.

*/

export const HammerIcon = FaHammer as (props: IconBaseProps) => any;

export const PlayerProfile = ({ player }: { player: Player }) => {
    const { hostId } = useGame();

    const isHost = player.id === hostId;

    return (
        <div className="w-[70px] min-w-[70px] h-full flex items-center relative">
            {isHost && (
                <HammerIcon className="absolute -top-[5px] -left-[5px] text-blue-mat text-xl z-[20]"/>
            )}
            <img
                src={`/characters/icons/${player.character?.toLowerCase()}_icon.png`}
                alt="Character"
                className=" object-contain cursor-pointer hover:scale-105 transition-transform duration-200 ease-in-out border border-2 border-yellow-dark rounded-2xl max-h-[70px]"
            />
        </div>
    );
};
