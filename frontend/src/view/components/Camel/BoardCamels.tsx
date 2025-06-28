import React, { useEffect, useRef, useState } from "react";
import { motion } from "framer-motion";
import { useGame } from "@src/hooks";
import { getPosition } from "../Game/Positions";
import { Camel, DesertTile } from "@src/types";

interface BoardCamelProps {
    camel: Camel;
    camels: Camel[];
    desertTiles: DesertTile[];
}

const BoardCamel = ({ camel, camels, desertTiles }: BoardCamelProps) => {
    const [animatedPosition, setAnimatedPosition] = useState(camel.position);
    const [hovered, setHovered] = useState(false);
    const prevPositionRef = useRef(camel.position);

    useEffect(() => {
        const from = prevPositionRef.current;
        const to = camel.position;

        if (from === to) return;

        let movedBy = Math.abs(from - to);
        if (from > to) {
            movedBy = 16 - movedBy;
        }
        console.log(camel.color, "from", from, "to", to, "moved by", movedBy);

        let step = from;
        const stepThrough = () => {
            step = step === 16 ? 1 : step + 1;
            setAnimatedPosition(step);
            if (step !== to) {
                setTimeout(stepThrough, 300);
            }
        };

        stepThrough();
        prevPositionRef.current = to;
    }, [camel.position]);

    const style = getPosition(animatedPosition);

    return (
        <>
            {hovered && (
                <div
                    className="absolute"
                    style={{
                        top: style.top,
                        left: style.left ?? "auto",
                        right: style.right ?? "auto",
                        transform: "translate(5%, -100%)",
                        zIndex: camel.stackPosition + 10,
                    }}
                >
                    <div className="bg-yellow-200 text-black px-2 py-1 rounded shadow">
                        {camels.sort((a, b) => a.racePosition - b.racePosition).map((c) => (
                            <div key={c.color} className="flex items-center">
                                <img
                                    src={`/camel/board/${c.color.toLowerCase()}.png`}
                                    className="object-contain w-6 h-6 mr-2"
                                    alt={c.color}
                                />
                                <span>{c.color}</span>
                                <span className="ml-2">{c.racePosition}</span>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            <motion.img
                src={`/camel/board/${camel.color.toLowerCase()}.png`}
                className="object-contain absolute z-10"
                style={{
                    width: "15%",
                    scale: 1 - (camel.stackPosition - 1) * 0.1,
                    zIndex: camel.stackPosition + 10,
                }}
                animate={{
                    top: style.top,
                    left: style.left ?? "auto",
                    right: style.right ?? "auto",
                    rotate: style.rotate,
                }}
                transition={{ duration: 0.3, ease: "easeInOut" }}
                onMouseEnter={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
            />
        </>
    );
};
export const setRacePositions = (camels: Camel[]) => {
    const camelsCopy = camels.map(camel => ({ ...camel }));
    const sortedCamels = camelsCopy.sort((a, b) => {
        if (b.rawPosition !== a.rawPosition) {
            return b.rawPosition - a.rawPosition;
        }
        return b.stackPosition - a.stackPosition;
    });

    for (let i = 0; i < sortedCamels.length; i++) {
        sortedCamels[i].racePosition = i + 1;
    }

    return sortedCamels;
};


export const BoardCamels = ({ desertTiles }: { desertTiles: DesertTile[] }) => {
    const { camels } = useGame();

    const sortedCamels = setRacePositions(camels);

    return (
        <div className="absolute top-0 left-0 w-full h-full">
            {sortedCamels.map((camel) => {
                const samePositionCamels = sortedCamels.filter((c) => c.position === camel.position)

                return (
                    <BoardCamel
                        key={camel.color}
                        camel={camel}
                        camels={samePositionCamels}
                        desertTiles={desertTiles}
                    />
                );
            })}
        </div>
    );
};
