import { createContext, useContext } from "react";

export interface IAlert{
    message: string;
    status: "success" | "error" | "warning" | "info";
}

interface AlertContextType {
    addAlert: (data: IAlert) => void;
    clearAlerts: () => void;
}

export const AlertContext = createContext<AlertContextType | undefined>(undefined);

export const useAlert = () => {
    const context = useContext(AlertContext);
    if (context === undefined) {
        throw new Error("useAlert must be used within a AlertProvider");
    }
    return context;
};
