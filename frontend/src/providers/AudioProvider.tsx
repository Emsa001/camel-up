import React, {
    createContext,
    useContext,
    useState,
    useRef,
    useEffect,
} from "react";

export type TrackEntry = {
    key: string;
    name: string;
};

export type AudioContextType = {
    musicOn: boolean;
    toggleMusic: () => void;
    soundOn: boolean;
    toggleSound: () => void;
    currentTrack: string;
    setCurrentTrack: (trackKey: string) => void;
    trackList: TrackEntry[];
    playTrack: (trackKey: string) => void;
};

const AudioContext = createContext<AudioContextType | undefined>(undefined);

export const useAudio = (): AudioContextType => {
    const ctx = useContext(AudioContext);
    if (!ctx) throw new Error("useAudio must be used within an AudioProvider");
    return ctx;
};

export const AudioProvider: React.FC<React.PropsWithChildren<{}>> = ({
                                                                         children,
                                                                     }) => {
    const tracks = useRef<Record<string, HTMLAudioElement>>({});
    const prevKey = useRef<string | null>(null);

    // Musik ist standardmäßig aus (muted)
    const [musicOn, setMusicOn] = useState(false);
    const [soundOn, setSoundOn] = useState(true);
    const [trackList, setTrackList] = useState<TrackEntry[]>([]);
    const [currentTrack, setCurrentTrack] = useState<string>("");

    // 1) Playlist laden & preload
    useEffect(() => {
        fetch("/music/playlist.json")
            .then((res) => {
                if (!res.ok) throw new Error("Could not load playlist.json");
                return res.json();
            })
            .then((data: Record<string, string[]>) => {
                const list: TrackEntry[] = [];
                Object.entries(data).forEach(([category, files]) => {
                    files.forEach((filename) => {
                        const name = filename.replace(/\.[^/.]+$/, "");
                        const key = `${category}/${name}`;
                        const url = encodeURI(`/music/${category}/${filename}`);
                        const audio = new Audio(url);
                        audio.loop = true;
                        audio.volume = 0.5;
                        tracks.current[key] = audio;
                        list.push({ key, name });
                    });
                });
                setTrackList(list);
                // Standard-Track direkt Home/Home_Screen (erster Eintrag)
                if (list.length > 0) {
                    setCurrentTrack(list[0].key);
                    prevKey.current = list[0].key;
                }
            })
            .catch((err) => console.error("AudioProvider playlist load error:", err));
    }, []);

    // 2) Wechseln und Pausieren/Playen je nach State
    useEffect(() => {
        if (!currentTrack) return;
        // Wenn gemutet: nur pausieren
        if (!musicOn) {
            tracks.current[currentTrack]?.pause();
            return;
        }
        // Altes Track pausieren
        if (prevKey.current && prevKey.current !== currentTrack) {
            const prevAudio = tracks.current[prevKey.current];
            prevAudio?.pause();
            prevAudio!.currentTime = 0;
        }
        // Neuen Track spielen
        const currAudio = tracks.current[currentTrack];
        if (currAudio) {
            currAudio.play().catch(() => {/* ignore */});
            prevKey.current = currentTrack;
        }
    }, [currentTrack, musicOn]);

    // Direktes Play, z.B. im Klick-Handler
    const playTrack = (key: string) => {
        if (!musicOn) return; // falls noch gemutet
        // Alten Track pausieren
        if (prevKey.current && prevKey.current !== key) {
            const prev = tracks.current[prevKey.current];
            prev?.pause();
            prev!.currentTime = 0;
        }
        const audio = tracks.current[key];
        audio?.play().catch(() => {/* ignore */});
        prevKey.current = key;
        setCurrentTrack(key);
    };

    return (
        <AudioContext.Provider
            value={{
                musicOn,
                toggleMusic: () => setMusicOn((prev) => !prev),
                soundOn,
                toggleSound: () => setSoundOn((prev) => !prev),
                currentTrack,
                setCurrentTrack,
                trackList,
                playTrack,
            }}
        >
            {children}
        </AudioContext.Provider>
    );
};
