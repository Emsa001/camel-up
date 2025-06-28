import { useEffect, useState } from "react";
import service from "@services/websockets/socket";
import { subscribeToDice } from "@src/services/websockets/subscriptions/dice";
import { Camel, Player } from "@src/types";

export type Phase = "launching" | "settling" | "done";

const rotations: Record<number, [number, number, number]> = {
    1: [0, 0, 0],
    2: [-Math.PI * 2.5, -Math.PI / 2, 0],
    3: [-Math.PI / 2, 0, 0],
    4: [-Math.PI, Math.PI * 2, 0],
    5: [-Math.PI / 2, Math.PI * 2.5, 0],
    6: [-Math.PI / 2, Math.PI, 0],
};

export const useDice = (player: Player, roomCode: string) => {
    const [color, setColor] = useState<string>("white");
    const [isRolling, setIsRolling] = useState<boolean>(false);
    const [phase, setPhase] = useState<Phase>("launching");
    const [targetRotation, setTargetRotation] = useState<[number, number, number]>([0, 0, 0]);

    async function requestDice() {
        if (isRolling) return console.log("Already rolling");
        await service.publish({
            destination: `/app/game/${roomCode}/dice`,
            message: player,

        });
    }

    const handleDiceThrow = (camelColor: string, value: number, camels: Camel[]) => {
        if (isRolling) return console.log("Already rolling");

        // play sound effect
        const audio = new Audio("/sounds/dice_sound.mp3");
        audio.play();

        setColor(camelColor);
        setPhase("launching");
        const rotation = rotations[value];
        setTargetRotation(rotation);
        setIsRolling(true);

        setTimeout(() => setPhase("settling"), 500);
        setTimeout(() => {
            setIsRolling(false);
        }, 3000);
    };

    useEffect(() => {
        subscribeToDice(roomCode, handleDiceThrow);

        return () => {
            service.unsubscribe(`/topic/game/${roomCode}/dice`);
        };
    }, []);

    return {
        requestDice,

        color,
        isRolling,
        phase,
        targetRotation,

        setPhase,
        setIsRolling,
        setTargetRotation,
    };
};
