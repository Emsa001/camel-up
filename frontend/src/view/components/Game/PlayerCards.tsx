import { useGame } from "@src/hooks";
import React from "react";

/*
This is just a mockup of the PlayerCards (bets).
TODO: Implement the actual PlayerCards with the correct logic and styles.
*/

export const PlayerCards = () => {
    const { legBettingCards, player } = useGame();
    if (!player) return null;

    const playerCards = legBettingCards.filter((card) => card.playerId === player.id);

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
            className="flex pl-16 whitespace-nowrap"
            style={{
                overflowX: numberOfCards > 0 && finalPopupWidth < calculatedCardsContainerWidth ? "auto" : "visible",
            }}
        >
            {playerCards.map((card, idx) => {
                const displayValue = valueMap.get(card.label) || card.label;

                return (
                    <div
                        key={idx}
                        className={`relative ${
                            idx > 0 ? "-ml-12" : ""
                        }`}
                        style={{
                            width: cardWidth,
                            height: 120, // Fixheight für Kartenbegrenzung
                        }}
                    >
                        <img
                            src={`/camel/cards/${card.camel.toLowerCase()}/${displayValue + "v2"}.png`}
                            alt={`Card failed to load`}
                            className={`
                                object-contain cursor-pointer absolute left-0 top-0 
                                hover:scale-150 hover:-translate-y-12 
                                transition-transform duration-200 ease-in-out 
                                border-2 border-yellow-dark rounded max-h-[120px]
                            `}
                        />
                    </div>
                );
            })}
        </div>
    );
};