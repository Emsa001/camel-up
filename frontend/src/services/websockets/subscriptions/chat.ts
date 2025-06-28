import service from "@services/websockets/socket";
import { ChatMessage } from "@src/types/chat";
import { IMessage } from "@stomp/stompjs";

export const subscribeToChat = async (
    roomCode: string,
    setMessages: React.Dispatch<React.SetStateAction<ChatMessage[]>>
) => {
    const playSoundEffect = (action: string) => {
        const sounds: { [key: string]: string } = {
            bet: "/sounds/draw_a_card.mp3",
            move: "/sounds/camel_running.mp3",
            tile: "/sounds/placing_a_tile.mp3",
        };

        const sound = sounds[action.toLowerCase()];
        if (sound) {
            const audio = new Audio(sound);
            audio.play();
        }
    };

    return await service.subscribe(`/topic/game/${roomCode}/chat`, (message: IMessage) => {
        const data: ChatMessage = JSON.parse(message.body);

        playSoundEffect(data.action);
        setMessages((prev) => [...prev, data]);
    });
};
