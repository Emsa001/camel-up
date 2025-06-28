import { RawPlayer } from "@src/types";
import React, { useState, useRef } from "react";
import { Button, Input, Range } from "react-daisyui";
import { toast } from "react-toastify";

export function SelectLobby({
    handleJoinGame,
}: {
    handleJoinGame: (roomCode: string, player: RawPlayer) => Promise<void>;
}) {
    const [name, setName] = useState<string>("");
    const [age, setAge] = useState<number>(10);
    const [roomCode, setRoomCode] = useState<string[]>(Array(6).fill(""));

    const inputRefs = useRef<Array<HTMLInputElement | null>>([]);

    const handleCodeChange = (value: string, index: number) => {
        if (!/^\d*$/.test(value)) return;

        const chars = value.split("");
        const newCode = [...roomCode];

        if (chars.length > 1) {
            for (let i = 0; i < chars.length && index + i < 6; i++) {
                if (/^\d$/.test(chars[i])) {
                    newCode[index + i] = chars[i];
                }
            }
            setRoomCode(newCode);

            const nextIndex = Math.min(index + chars.length, 5);
            inputRefs.current[nextIndex]?.focus();
        } else {
            newCode[index] = value;
            setRoomCode(newCode);

            if (value && index < 5) {
                inputRefs.current[index + 1]?.focus();
            }
        }
    };

    const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
        const paste = e.clipboardData.getData("text").replace(/\D/g, "");
        if (paste.length === 0) return;

        e.preventDefault();
        const newCode = [...roomCode];
        for (let i = 0; i < paste.length && i < 6; i++) {
            newCode[i] = paste[i];
        }
        setRoomCode(newCode);
        inputRefs.current[Math.min(paste.length, 5)]?.focus();
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === "Backspace" && roomCode[index] === "" && index > 0) {
            inputRefs.current[index - 1]?.focus();
        }
    };

    const joinPrivateGame = async () => {
        if (roomCode.join("").length < 6) {
            toast.error("Please enter a valid game code.");
            return;
        }

        const player: RawPlayer = {
            name: name.trim() || "Guest",
            age: age,
        }
        
        await handleJoinGame(roomCode.join(""), player);
    };

    return (
        <div className="flex flex-col h-full text-white">
            <div>
                <h2 className="text-2xl font-bold mb-6">Join Private Game</h2>

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Your Name</label>
                    <Input
                        type="text"
                        placeholder="Enter your name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full bg-purple-light"
                    />
                </div>

                <div className="mb-4">
                    <label className="block mb-2 text-sm font-medium">Your Age</label>
                    <div className="flex items-center gap-4">
                        <Range
                            value={age}
                            size="xs"
                            color="primary"
                            onChange={(e) => setAge(Number(e.target.value))}
                            min={5}
                            max={100}
                        />
                        <span className="text-sm">{age}</span>
                    </div>
                </div>
                <div className="mb-4 flex flex-col items-center">
                    <label className="block mb-2 text-lg font-medium">Game Code</label>
                    <div className="flex gap-2">
                        {roomCode.map((digit, index) => (
                            <Input
                                key={index}
                                type="text"
                                maxLength={1}
                                value={digit}
                                className="w-12 text-center bg-purple-light text-white text-lg"
                                onChange={(e) => handleCodeChange(e.target.value, index)}
                                onKeyDown={(e) => handleKeyDown(e, index)}
                                onPaste={handlePaste}
                                ref={(el) => {
                                    inputRefs.current[index] = el;
                                }}
                            />
                        ))}
                    </div>
                </div>
            </div>

            <div className="mt-auto flex gap-2 pt-6">
                <Button className="text-white flex-1 border-none bg-blue-mat bg-opacity-80 hover:bg-blue-mat hover:bg-opacity-100" onClick={joinPrivateGame}>
                    Join Game
                </Button>
            </div>
        </div>
    );
}
