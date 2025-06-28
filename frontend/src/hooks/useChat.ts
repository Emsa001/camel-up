import { useState, useRef, useEffect } from "react";
import service from "@services/websockets/socket";
import { subscribeToChat } from "@services/websockets/subscriptions/chat";
import { toast } from "react-toastify";
import { ChatMessage } from "@src/types/chat";

export const useChat = (roomCode: string) => {
    const [isOpen, setIsOpen] = useState(false);
    const [messages, setMessages] = useState<ChatMessage[]>([]);
   
    const inputRef = useRef<HTMLInputElement>(null);
    const popupRef = useRef<HTMLDivElement>(null);
    const cooldownRef = useRef(false);

    const scrollToMessages = () => {
        if (popupRef.current) {
            popupRef.current.scrollTo({
                top: popupRef.current.scrollHeight - 10,
                behavior: "smooth",
            });
        }
        if (inputRef.current) {
            inputRef.current.scrollTo({
                top: inputRef.current.scrollHeight - 10,
                behavior: "smooth",
            });
        }
    }

    const addMessage = (msg: ChatMessage) => {
        if (cooldownRef.current) {
            toast.error("Please wait before sending another message.");
            return;
        }
        
        if (!msg.text.trim()) {
            toast.error("Message cannot be empty.");
            return;
        }

        if (msg.text.length > 100) {
            toast.error("Message cannot exceed 100 characters.");
            return;
        }

        service.publish({
            destination: `/app/game/${roomCode}/chat`,
            message: msg,
        });

        cooldownRef.current = true;
        setTimeout(() => {
            cooldownRef.current = false;
        }, 1000);
    };

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as Node;

            const clickedOutsidePopup = popupRef.current && !popupRef.current.contains(target);
            const clickedOutsideInput = inputRef.current && !inputRef.current.contains(target);

            if (clickedOutsidePopup && clickedOutsideInput) {
                setIsOpen(false);
            }
        };

        if (!isOpen) {
            inputRef.current?.scrollTo({
                top: inputRef.current.scrollHeight - 10,
            });
        }

        if (isOpen) {
            popupRef.current?.scrollTo({
                top: popupRef.current.scrollHeight - 10,
            });
            document.addEventListener("mousedown", handleClickOutside);
            inputRef.current?.focus();
        }

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [isOpen]);

    useEffect(() => {
        scrollToMessages();
    }, [messages]);

    useEffect(() => {
        subscribeToChat(roomCode, setMessages);

        return () => {
            service.unsubscribe(`/topic/game/${roomCode}/chat`);
        };
    }, [roomCode])

    return {
        isOpen,
        setIsOpen,
        messages,
        addMessage,
        inputRef,
        popupRef,
    };
};
