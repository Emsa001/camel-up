import React from 'react';

export const CharacterIcon = ({ character }: { character: string | null }) => (
    <img
        src={`/characters/icons/${character?.toLowerCase()}_icon.png`}
        alt="Character"
        className="flex object-contain cursor-pointer hover:scale-105 transition-transform duration-200 ease-in-out border border-2 border-yellow-dark rounded-2xl max-h-[60px]"
    />
);
