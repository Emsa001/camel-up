import React, { useState, useRef, useEffect } from "react";
import { Button, Tooltip } from "react-daisyui";
import { useAudio, TrackEntry } from "@providers";

import { MdMusicNote, MdMusicOff } from "react-icons/md";

const IconExit = MdMusicNote as any;
const IconMusicOff = MdMusicOff as any;

export function MusicMenu() {
    const {
        trackList,
        currentTrack,
        setCurrentTrack,
        musicOn,
        toggleMusic,
        soundOn,
        toggleSound
    } = useAudio();

    const gameTracks: TrackEntry[] = trackList.filter((t) =>
        t.key.startsWith("Game/")
    );

    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    // Schließen des Menüs bei Klick außerhalb
    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const toggleMenu = () => setIsOpen(!isOpen);

    return (
        <div className="relative" ref={dropdownRef}>
            <Tooltip message="Music Player" className="tooltip tooltip-bottom">
                <button onClick={toggleMenu} className={`p-4 bg-purple-800 hover:scale-105 rounded-md text-xl`}>
                    <IconExit />
                </button>
            </Tooltip>

            {isOpen && (
                <div className="absolute top-full left-0 mt-2 z-50">
                    <ul className="menu p-2 shadow bg-dark-primary text-light-primary rounded-box w-48">
                        <li>
                            <button
                                onClick={() => {
                                    toggleMusic();
                                    setIsOpen(false);
                                }}
                                className="hover:bg-dark-hover w-full text-left p-2"
                            >
                                {musicOn ? "Mute Music" : "Unmute Music"}
                            </button>
                        </li>
                        <li>
                            <button
                                onClick={() => {
                                    toggleSound();
                                    setIsOpen(false);
                                }}
                                className="hover:bg-dark-hover w-full text-left p-2"
                            >
                                {soundOn ? "Mute SFX" : "Unmute SFX"}
                            </button>
                        </li>
                        <li className="menu-title flex justify-between items-center mt-2 p-2">
                            <span>Track</span>
                            <select
                                value={currentTrack}
                                onChange={(e) => setCurrentTrack(e.target.value)}
                                className="bg-dark-secondary text-light-primary px-2 py-1 rounded ml-2"
                            >
                                {gameTracks.map((t) => (
                                    <option key={t.key} value={t.key}>
                                        {t.name}
                                    </option>
                                ))}
                            </select>
                        </li>
                    </ul>
                </div>
            )}
        </div>
    );
}