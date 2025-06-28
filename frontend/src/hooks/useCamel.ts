import { createContext, useContext } from "react";

interface CamelContextType {
    error: string;
    color: string;
    setColor: (value: string) => void;

    betValues: number[];
    betValue: number;
    
    setBetValue: (value: number) => void;
    handleBet: () => void;
}

export const CamelContext = createContext<CamelContextType | undefined>(undefined);

export const useCamel = () => {
    const context = useContext(CamelContext);
    if (context === undefined) {
        throw new Error("useCamel must be used within a CamelProvider");
    }

    return context;
};
