import React from "react";

import { ChatPopup } from "./ChatPopup";
import { ChatInput } from "./ChatInput";
import { useChat } from "@hooks";

import { CiChat1 } from "react-icons/ci";
import { IconBaseProps } from "react-icons";

const Icon = CiChat1 as (props: IconBaseProps) => any;

export const Chat = ({ roomCode }: { roomCode: string }) => {
    const { isOpen, setIsOpen, messages, addMessage, inputRef, popupRef } = useChat(roomCode);

    return (
        <>
            <div className="hidden lg:flex max-w-[240px] w-full h-[70px] items-center justify-center relative">
                <ChatPopup messages={messages} isOpen={isOpen} popupRef={popupRef} />
                <ChatInput
                    messages={messages}
                    isOpen={isOpen}
                    setIsOpen={setIsOpen}
                    inputRef={inputRef}
                    onSend={addMessage}
                />
            </div>
            <Icon className="lg:hidden"/>
        </>
    );
};