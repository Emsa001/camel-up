import { useGame } from "@src/hooks";
import { Camel } from "@src/types";
import React from "react";

/*

This is just a mockup of the Dices.
TODO: Implement the actual dices with the correct logic and styles.

*/

const DiceBox = ({ camel }: { camel: Camel }) => {
    if (!camel.moved) {
        return (
            <div
                className={`w-16 h-16 rounded-lg opacity-50`}
                style={{ backgroundColor: camel.color.toLowerCase() }}
            />
        );
    }

    return (
        <img
            src={`/camel/dice/${camel.color.toLowerCase()}/face${camel.lastRoll}.png`}
            alt="Dice"
            className="w-16 h-16 rounded-lg"
        />
    );
};

export const Dices = () => {
    const { camels } = useGame();

    return (
        <div className="col-span-2 w-full h-full flex items-center justify-center gap-1">
            {camels.map((camel) => (
                <DiceBox key={camel.color} camel={camel} />
            ))}
        </div>
    );
};
