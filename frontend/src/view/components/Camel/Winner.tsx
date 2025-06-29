import React, { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Camel, Game } from "@src/types";

export const CamelWinner = ({ game }: { game: Game }) => {
    const [winner, setWinner] = useState<Camel | null>(null);
    const [isVisible, setIsVisible] = useState(true);

    useEffect(() => {
        // Check if there is a new winner
        const newWinner = game.camels.find((camel) => camel.legWinner || camel.raceWinner) || null;
        if(!newWinner) return ;
        
        if (newWinner != winner) {
            setWinner(newWinner);
            setIsVisible(true);

            const timer = setTimeout(() => {
                // Hide the modal after 3 seconds
                setIsVisible(false);
            }, 3000);

            return () => clearTimeout(timer);
        }else{
            setWinner(null);
        }
    }, [game.round]);

    if (!winner) return null; // Don't render anything if there's no winner

    const winnerType = winner.raceWinner ? "🏆 Race Winner!" : "🎯 Leg Winner!";

    return (
        <AnimatePresence>
            {isVisible && (
                <div className="fixed inset-0 flex items-center justify-center z-50 bg-gradient-to-br from-gray-800/70 via-blue-900/70 to-gray-900/70 backdrop-blur-md">
                    <motion.div
                        initial={{ opacity: 0, scale: 0.5, rotate: -180 }}
                        animate={{ opacity: 1, scale: 1, rotate: 0 }}
                        exit={{ opacity: 0, scale: 0.5, rotate: 180 }}
                        transition={{ duration: 0.5, ease: "easeOut" }}
                        onAnimationComplete={() => {
                            if (!isVisible) {
                                setWinner(null);
                            }
                        }}
                        className="bg-gray-900/80 backdrop-blur-xl border border-blue-300/20 rounded-2xl shadow-xl p-8 max-w-md w-full text-white relative"
                    >
                        <h2 className="text-2xl font-extrabold text-center mb-2">
                            {winnerType}
                        </h2>
                        <p className="text-center text-lg mb-6 text-blue-100">And the winner is...</p>
                        <div className="flex flex-col items-center justify-center">
                            <motion.img
                                src={`/camel/${winner.color}.png`}
                                alt="Winning Camel"
                                className="rounded-xl border-4 border-yellow-400 shadow-lg"
                                initial={{ y: 0 }}
                                animate={{ y: [0, -10, 0] }}
                                transition={{
                                    repeat: Infinity,
                                    duration: 2,
                                    ease: "easeInOut",
                                }}
                            />
                        </div>
                    </motion.div>
                </div>
            )}
        </AnimatePresence>
    );
};