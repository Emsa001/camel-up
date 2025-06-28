import React, { ReactNode, useEffect, useState } from "react";

import { useGame } from "@hooks";
import { CamelContext } from "hooks/useCamel";

import service from "@services/websockets/socket";

export const CamelProvider = ({ children }: { children: ReactNode }) => {
    const betValues = [1, 2, 3];
    
    const [error, setError] = useState<string>("");
    const [color, setColor] = useState<string>("");
    const [betValue, setBetValue] = useState<number>(0);
    const { player } = useGame();

    useEffect(() => {
        setError("");
    }, [color, betValue]);

    async function handleBet() {
        if (betValues.indexOf(betValue) === -1) {
            setError("Select bet value");
            return;
        }

        if(!color){
            setError("Select camel color");
            return;
        }

        // Webscoket send data to server
        await service.publish({
            destination: "/app/game/bet",
            message: { playerId: player!.id, color, value: betValue },
        });
    }

    return (
        <CamelContext.Provider
            value={{
                error,
                color,
                setColor,
                betValues,
                betValue,
                setBetValue,
                handleBet,
            }}
        >
            {children}
        </CamelContext.Provider>
    );
};
