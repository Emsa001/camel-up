import React from "react";
import { MessageComponent } from "./MessageComponent";
import { ChatMessage } from "@src/types/chat";

export const ChatPopup = ({
    messages,
    isOpen,
    popupRef,
}: {
    messages: ChatMessage[];
    isOpen: boolean;
    popupRef: React.RefObject<HTMLDivElement | null>;
}) => {
    if (!isOpen) return null;

    return (
        <div
            ref={popupRef}
            className="absolute -top-[260px] left-0 h-[250px] overflow-y-auto bg-dark-primary rounded-lg border border-2 border-yellow-dark p-2 pr-4 w-full break-words"
        >
            {messages.map((message, index) => (
                <MessageComponent key={index} message={message} />
            ))}
        </div>
    );
};