import React, { ReactNode, useState } from "react";
import { Alert } from "react-daisyui";

import { AlertContext, IAlert } from "hooks/useAlert";

export const AlertProvider = ({ children }: { children: ReactNode }) => {
    const [alerts, setAlerts] = useState<IAlert[]>([]);

    const addAlert = ({ message, status }: IAlert) => {
        setAlerts((prev) => {
            const existingAlert = prev.find(
                (alert) =>
                    alert.message === message &&
                    alert.status === status
            );
            if (existingAlert) {
                return prev;
            }

            // Add the new alert
            return [
                ...prev,
                {
                    message,
                    status,
                },
            ];
        });
    };

    const clearAlerts = () => {
        setAlerts([]);
    }

    return (
        <AlertContext.Provider
            value={{ addAlert, clearAlerts }}
        >

            <div className="absolute top-0 left-0 z-50 flex flex-col">
                {alerts.map((alert, index) => (
                    <Alert key={index} status={alert.status} className="m-2">
                        {alert.message}
                    </Alert>
                ))}
            </div>

            {children}
        </AlertContext.Provider>
    );
};
