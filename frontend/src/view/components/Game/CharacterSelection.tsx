import { useGame } from "@hooks";
import React from "react";
import ApiService from "@services/api";
import { CharacterName, Player } from "@src/types";
import {toast} from "react-toastify";

/*

This is just a mockup of the CharacterSelection.
TODO: Implement the actual CharacterSelection with the correct logic and styles.

*/

const characters: CharacterName[] = [
    "ALICE_WEIDEL",
    "ANGELA_MERKEL",
    "BORIS_PISTORIUS",
    "CHRISTIAN_LINDNER",
    "KARL_LAUTERBACH",
    "MARKUS_SOEDER",
    "OLAF_SCHOLZ",
    "PHILIPP_AMTHOR",
];

// Implement the CharacterSelection component
export const CharacterSelection = ({ players }: { players: Player[] }) => {
    const { player, setCharacter, selectedCharacter } = useGame();

    // Erstelle Liste aller bereits gewählten Charaktere
    const selectedCharacters = players
        .map((p) => p.character)
        .filter((c): c is CharacterName => !!c);

    const myCharacter = player?.character || null;

    if (!player) return null;

    return (
        <div className="col-span-2 max-w-fit h-48 flex items-center justify-self-end gap-1.5 p-4 bg-purple-dark  border-2 border-[#1B1B1B] rounded-2xl">
            {characters.map((character) => {
                const isSelectedByMe = myCharacter === character;
                const isSelectedByOthers =
                    selectedCharacters.includes(character) && !isSelectedByMe;
                const isAvailable = !isSelectedByOthers;

                return (
                    <div key={character} className="relative">
                        <img
                            src={`/characters/cards/${character.toLowerCase()}.png`}
                            alt={`Character ${character}`}
                            onClick={() => {
                                if (isAvailable) {
                                    setCharacter(character);
                                    ApiService.updatePlayerCharacter(player.id, character);
                                }


                            }}
                            className={`w-32 h-44 rounded-lg cursor-pointer transition-all
                                ${isSelectedByOthers ? "opacity-50 grayscale pointer-events-none" : ""}
                                ${isAvailable ? "hover:scale-105" : "cursor-not-allowed"}
                                ${isSelectedByMe ? "ring-4 ring-blue-400 pointer-events-none" : ""}        
                            `}
                        />
                    </div>
                );
            })}
        </div>
    );
};
