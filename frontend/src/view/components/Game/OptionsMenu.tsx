// OptionsMenu.tsx
import { useGame } from "@hooks";
import React, { useState } from "react";
import { Button, Card, Tooltip } from "react-daisyui";
import { IconBaseProps } from "react-icons";

import { IoIosExit } from "react-icons/io";
import { MdOutlineRestartAlt } from "react-icons/md";

const IconExit = IoIosExit as (props: IconBaseProps) => any;
const IconRestart = MdOutlineRestartAlt as (props: IconBaseProps) => any;

export interface OptionsMenuProps {
    onRestart: () => void;
    onLeave: () => void;
}

export function OptionsMenu({ onRestart, onLeave }: OptionsMenuProps) {
    const { hostId, player } = useGame();

    // State, um zu speichern, ob das Menü aufgeklappt ist oder nicht
    const [isOpen, setIsOpen] = useState(false);

    // Diese Funktion wechselt zwischen aufgeklappt und zugeklappt
    const toggleMenu = () => {
        setIsOpen(!isOpen);
    };

    const isHost = player?.id === hostId;

    const buttonClass = "p-4 hover:scale-105 rounded-md text-xl";

    return (
        <>
            {/* Button, der das Menü öffnet bzw. schließt */}
            {isHost && (
                <Tooltip message="Restart" className="tooltip tooltip-bottom">
                    <button onClick={onRestart} className={`bg-blue-600 ${buttonClass}`}>
                        <IconRestart />
                    </button>
                </Tooltip>
            )}
            <Tooltip message="Leave" className="tooltip tooltip-bottom">
                <button onClick={onLeave} className={`bg-red-900 ${buttonClass}`}>
                    <IconExit  />
                </button>
            </Tooltip>
        </>
    );
}
