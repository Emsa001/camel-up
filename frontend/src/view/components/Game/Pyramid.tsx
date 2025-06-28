import React from "react";

import { useGame, useDice } from "@src/hooks";
import DiceRoller from "../Dice/DiceCanvas";
import { toast } from "react-toastify";

export const Pyramid = () => {
    const { roomCode, currentPlayerId, player } = useGame();
    const dice = useDice(player!, roomCode!);

    const handleClick = () => {
        if (currentPlayerId !== player?.id) {
            toast.error("It's not your turn!");
            return;
        }

        dice.requestDice();
    };

    return (
        <div className="px-2">
            <img
                src="/gameimg/pyramid.png"
                alt="Pyramid"
                className="object-contain mt-auto max-h-full cursor-pointer hover:scale-[1.03] transition-transform duration-200 ease-in-out z-[100]"
                onClick={handleClick}
            />
            <DiceRoller dice={dice} />
        </div>
    );
};
