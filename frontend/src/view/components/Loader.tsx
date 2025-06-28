import React from "react"
import { Loading } from "react-daisyui";

export const FullScreenLoader = () => {
    return (
        <div className="flex items-center justify-center h-screen w-screen">
            <div className="flex flex-col items-center">
                <Loading size="lg" />
                <p className="mt-4 text-lg">Loading...</p>
            </div>
        </div>
    );
}