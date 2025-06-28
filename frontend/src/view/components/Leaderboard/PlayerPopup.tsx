import React from "react";
import {BettingCard, Player} from "@src/types";
import { CharacterIcon } from "./CharacterIcon";
import { useGame } from "@src/hooks";

type Props = {
    player: Player;
    isHost: boolean;
    isYou: boolean;
    isCurrentPlayer: boolean;
    playerCards:BettingCard[]

};

export const PlayerPopup: React.FC<Props> = ({ player, isHost, isYou, isCurrentPlayer,playerCards }) => {
    const bgClass = isCurrentPlayer
        ? "bg-yellow-300 border-green-600"
        : "bg-yellow-200 border-yellow-dark";


    const valueMap = new Map([
        [5, 1],
        [3, 2],
        [2, 3],
    ]);

    const numberOfCards = playerCards.length;

    // Card height is 120 px width about 80
    // Overlap is -ml-12, so kinda 48px per card
    const cardWidth = 80;
    const overlap = 48;
    const initialPadding = 100;

    // Calculate needed width for Card-Containers
    let calculatedCardsContainerWidth = initialPadding; // Start with left Padding

    if (numberOfCards > 0) {
        calculatedCardsContainerWidth += cardWidth; // first cards need full width
        calculatedCardsContainerWidth += (numberOfCards - 1) * (cardWidth - overlap); // if there are more then 1 card they overlap
    }

    const finalPopupWidth = Math.min(calculatedCardsContainerWidth);

    return (
        <div
            className={`popup absolute -top-1.5 -left-1.5 ${bgClass} rounded-2xl border-2 p-2 z-20`} //rounded-2xl border-2 p-2 w-[250px] z-10`}
            // use final pop up size
            style={{
                width: `${finalPopupWidth}px`,
                minWidth: '256px' // min-w-64
            }}
        >
            <div className="text-center">
                <div className="flex justify-between items-center w-full mb-2">
                    <CharacterIcon character={player.character} />
                    <strong>
                        {player.name} {isYou && "(you)"}
                    </strong>
                    <span>💰{player.money}</span>
                </div>

                <div className="flex pl-16 whitespace-nowrap"
                     style={{ overflowX: numberOfCards > 0 && finalPopupWidth < calculatedCardsContainerWidth ? 'auto' : 'visible' }}
                >
                    {playerCards.map((card, idx) => { // 'idx' instead of 'index' to not get conflicts
                        const displayValue = valueMap.get(card.label) || card.label;

                        return (
                            <img
                                key={idx}
                                src={`/camel/cards/${card.camel.toLowerCase()}/${displayValue + "v2"}.png`}
                                alt={`Card failed to load`}
                                className={`object-contain cursor-pointer hover:scale-125 hover:-translate-y-2 transition-transform duration-200 ease-in-out border-2 border-yellow-dark rounded max-h-[120px] ${ idx > 0 ? '-ml-12' : '' }`}
                            />
                        );
                    })}
                </div>
            </div>
        </div>
    );
};
