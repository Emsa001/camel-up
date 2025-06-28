import React, { useState } from "react";
import { motion, Variants } from "framer-motion";
import { Button } from "react-daisyui";
import { AuthorsFooter } from "@components/Footer/Authors";
import { LogoMain } from "@components/Logo/Main";
import { useNavigate } from "react-router-dom";
import { useAudio } from "@providers";

function App() {
    const [entering, setEntering] = useState(false);
    const navigate = useNavigate();
    const { musicOn, toggleMusic, playTrack, currentTrack } = useAudio();

    // Musiksteuerung
    const handleUnmute = () => {
        if (!musicOn) {
            toggleMusic();
            playTrack(currentTrack);
        }
    };

    const handleMute = () => {
        if (musicOn) toggleMusic();
    };

    // Animationen
    const pyramidVariants: Variants = {
        initial: {
            scale: 1,
            opacity: 0.2,
        },
        enter: {
            scale: 6,
            opacity: 1,
            transition: {
                duration: 1.3,
                ease: "easeInOut",
            },
        },
    };

    const uiVariants: Variants = {
        initial: { opacity: 1 },
        exit: {
            opacity: 0,
            transition: {
                duration: 1,
                ease: [0.42, 0, 0.58, 1],
            },
        },
    };

    return (
        <div className="flex flex-col items-center justify-center h-screen w-screen gap-12 p-4 overflow-hidden relative">
            {/* Musiksteuerung oben rechts */}
            <div className="absolute top-4 right-4 flex gap-2 z-30">
                <Button size="sm" onClick={handleMute} disabled={!musicOn}>
                    Mute Music
                </Button>
                <Button size="sm" onClick={handleUnmute} disabled={musicOn}>
                    Unmute Music
                </Button>
            </div>

            {/* Hauptinhalt */}
            <motion.div
                variants={uiVariants}
                initial="initial"
                animate={entering ? "exit" : "initial"}
                className="flex flex-col items-center gap-12 z-10"
            >
                <LogoMain />
                <Button
                    className="bg-blue-mat hover:bg-blue-mat border-none text-white rounded-2xl"
                    wide
                    onClick={() => setEntering(true)}
                >
                    Enter the game
                </Button>
                <AuthorsFooter />
            </motion.div>

            {/* Pyramiden-Hintergrund */}
            <motion.img
                src="/pyramid-bg.png"
                alt="pyramid"
                className="absolute w-[600px] z-0"
                variants={pyramidVariants}
                initial="initial"
                animate={entering ? "enter" : "initial"}
                onAnimationComplete={() => {
                    if (entering) navigate("/lobby");
                }}
            />
        </div>
    );
}

export default App;