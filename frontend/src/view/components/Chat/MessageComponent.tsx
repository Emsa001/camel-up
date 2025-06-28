import { ChatMessage } from "@src/types/chat";
import React from "react";

export const MessageComponent = ({ message }: { message: ChatMessage }) => {
    const getColoredText = (text: string) => (
        <span className="font-bold uppercase" style={{ color: text }}>
            {text}
        </span>
    );

    const renderText = () => {
        switch (message.action.toLowerCase()) {
            case "take":
                return (
                    <span className="italic text-gray-400">
                        Took {getColoredText(message.text)} Camel Card
                    </span>
                );
            case "dice":
                return <span className="italic text-gray-400">Used Dice</span>;
            case "bet":
                return (
                    <span className="italic text-gray-400">Bet on {getColoredText(message.text)}</span>
                );
            case "tile":
                return <span className="italic text-gray-400">placed tile: {getColoredText(message.text)}</span>;
            case "move":
                const parts = message.text.split(" ");

                return (
                    <span className="italic text-gray-400">
                        {/* GREEN */}
                        <span className="font-bold uppercase">{getColoredText(parts[0])}</span>{" "}
                        moved{" "}
                        {/* 1 */}
                        <span className="font-black text-white">{parts[1]}</span>{" "}
                        spaces
                        {/* Optional tile event */}
                        {parts.length > 2 && (
                            <>
                                {" "}
                                <span className="font-bold">{parts[2]}</span> {/* And */}
                                {" "}
                                stepped in to{" "}
                                {parts[3]} {/* a/an */}
                                {" "}
                                <span className="font-bold">{parts[4]}</span> {/* Oasis/Mirage */}
                                {" "}
                                {parts[5] && <span className="font-bold">{parts[5]}</span>} {/* (+1)/(-1) */}
                            </>
                        )}
                    </span>
                );
            case "system":
                return (
                    <span className="italic text-gray-400">
                        {message.text}
                    </span>
                );
            case "message":
                return <span className="text-gray-100">{message.text}</span>;
        }
    };

    return (
        <div className="border-b border-gray-700 py-1">
            <div>
                {message.playerName && (
                    <strong className="text-white">{message.playerName}: </strong>
                )}
                {renderText()}
            </div>
        </div>
    );
};