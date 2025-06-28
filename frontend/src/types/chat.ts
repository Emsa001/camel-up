export interface ChatMessage {
    action: "bet" | "move" | "tile" | "system" | "message";
    playerName: string;
    text: string;
}