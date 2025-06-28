import React, { useEffect, useRef, useState } from "react";

const MusicPlayer = () => {
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const [playlist, setPlaylist] = useState<string[]>([]);
    const [currentTrackIndex, setCurrentTrackIndex] = useState<number>(0);
    const [isPlaying, setIsPlaying] = useState(false);

    // ⬇️ Playlist laden
    useEffect(() => {
        fetch("/music/playlist.json")
            .then((res) => res.json())
            .then((data: string[]) => {
                setPlaylist(data);
                const randomIndex = Math.floor(Math.random() * data.length);
                setCurrentTrackIndex(randomIndex);
            });
    }, []);

    const play = () => {
        if (!audioRef.current) return;
        audioRef.current.volume = 0.25;
        audioRef.current.play().catch((err) => {
            console.warn("Fehler beim Abspielen:", err);
        });
        setIsPlaying(true);
    };

    const pause = () => {
        if (!audioRef.current) return;
        audioRef.current.pause();
        setIsPlaying(false);
    };

    const togglePlay = () => {
        isPlaying ? pause() : play();
    };

    const nextTrack = () => {
        setCurrentTrackIndex((prev) => (prev + 1) % playlist.length);
        setIsPlaying(false);
    };

    const prevTrack = () => {
        setCurrentTrackIndex((prev) => (prev - 1 + playlist.length) % playlist.length);
        setIsPlaying(false);
    };

    // ⬇️ Automatisch abspielen beim Trackwechsel mit richtiger Lautstärke
    useEffect(() => {
        const playCurrentTrack = async () => {
            if (!audioRef.current) return;

            audioRef.current.pause();
            audioRef.current.load();
            audioRef.current.volume = 0.25;

            try {
                await audioRef.current.play();
                setIsPlaying(true);
            } catch (err) {
                console.warn("Autoplay blockiert:", err);
                setIsPlaying(false);
            }
        };

        playCurrentTrack();
    }, [currentTrackIndex]);

    if (playlist.length === 0) {
        return <div className="text-center">🎶 Lade Playlist...</div>;
    }

    const currentTrack = playlist[currentTrackIndex];

    return (
        <div className="card w-96 bg-base-100 shadow-xl p-4">
            <h2 className="text-xl font-bold mb-4 text-center truncate">{currentTrack}</h2>

            <audio ref={audioRef} preload="auto">
                <source src={`/music/${currentTrack}`} type="audio/mpeg" />
                Dein Browser unterstützt kein Audio.
            </audio>

            <div className="flex justify-center items-center gap-4 mt-2">
                <button
                    className="btn btn-square bg-blue-500 text-black hover:bg-blue-400 border-none"
                    onClick={prevTrack}
                >
                    ⏮️
                </button>

                <button
                    className="btn btn-primary px-10 text-lg font-bold"
                    onClick={togglePlay}
                >
                    {isPlaying ? "⏸️" : "▶️"}
                </button>

                <button
                    className="btn btn-square bg-blue-500 text-white hover:bg-blue-400 border-none"
                    onClick={nextTrack}
                >
                    ⏭️
                </button>
            </div>
        </div>
    );
};

export default MusicPlayer;
