import React, { useState } from "react";

import { Input } from "react-daisyui";
import { MessageComponent } from "./MessageComponent";
import { useGame } from "@hooks";
import { ChatMessage } from "@src/types/chat";

interface Props {
    messages: ChatMessage[];
    isOpen: boolean;
    setIsOpen: (open: boolean) => void;
    inputRef: React.RefObject<HTMLInputElement | null>;
    onSend: (msg: ChatMessage) => void;
}

export const ChatInput = ({ messages, isOpen, setIsOpen, inputRef, onSend }: Props) => {
    const [message, setMessage] = useState("");

    const { player } = useGame();

    const handleSend = () => {
        if (!player) return;

        if (message.trim()) {
            onSend({ action: "message", playerName: player.name, text: message });
            setMessage("");
        }
    };

    if (!isOpen) {
        return (
            <div
                className="w-full h-[70px] bg-dark-primary bg-opacity-70 border border-yellow-dark rounded-lg overflow-y-auto p-2 pr-4 pt-3 text-sm cursor-pointer break-words"
                onClick={() => setIsOpen(true)}
                ref={inputRef}
            >
                {messages.map((message, index) => (
                    <MessageComponent key={index} message={message} />
                ))}

                {messages.length === 0 && (
                    <div className="text-center text-gray-500">
                        No messages yet. Click here to start chatting!
                    </div>
                )}
            </div>
        );
    }

    return (
        <Input
            ref={inputRef}
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            maxLength={100}
            placeholder="Type your message..."
            className="w-full h-[70px] p-2 pr-4 pt-3 bg-dark-primary text-white focus:outline-none focus:border-yellow-dark rounded-lg"
            onKeyDown={(e) => e.key === "Enter" && handleSend()}
        />
    );
};
