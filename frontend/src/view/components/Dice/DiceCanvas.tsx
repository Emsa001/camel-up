import React, { useRef, useEffect } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import * as THREE from "three";
import { useLoader } from "@react-three/fiber";
import { Phase, useDice } from "@src/hooks/useDice";

function Dice({
    isRolling,
    targetRotation,
    phase,
    color,
    onFinish,
}: {
    isRolling: boolean;
    targetRotation: [number, number, number];
    phase: Phase;
    color: string;
    onFinish: () => void;
}) {
    const meshRef = useRef<THREE.Mesh>(null);
    const positionRef = useRef<THREE.Vector3>(new THREE.Vector3(0, 0, 0));

    useFrame(() => {
        if (!meshRef.current) return;

        if (phase === "launching") {
            meshRef.current.rotation.x += 0.1;
            meshRef.current.rotation.y += 0.1;
            meshRef.current.rotation.z += 0.1;
        }

        if (phase === "settling") {
            meshRef.current.rotation.x = THREE.MathUtils.lerp(
                meshRef.current.rotation.x,
                targetRotation[0],
                0.1
            );
            meshRef.current.rotation.y = THREE.MathUtils.lerp(
                meshRef.current.rotation.y,
                targetRotation[1],
                0.1
            );
            meshRef.current.rotation.z = THREE.MathUtils.lerp(
                meshRef.current.rotation.z,
                targetRotation[2],
                0.1
            );

            const dist =
                Math.abs(meshRef.current.rotation.x - targetRotation[0]) +
                Math.abs(meshRef.current.rotation.y - targetRotation[1]) +
                Math.abs(meshRef.current.rotation.z - targetRotation[2]);

            if (dist < 0.05) {
                positionRef.current.y = 1;
                onFinish();
            }
        }

        meshRef.current.position.copy(positionRef.current);
    });

    const textures = useLoader(THREE.TextureLoader, [
        `/camel/dice/${color}/face2.png`,
        `/camel/dice/${color}/face2.png`,
        `/camel/dice/${color}/face1.png`,
        `/camel/dice/${color}/face1.png`,
        `/camel/dice/${color}/face3.png`,
        `/camel/dice/${color}/face3.png`,
    ]);

    const materials = textures.map((texture) => new THREE.MeshStandardMaterial({ map: texture }));

    useEffect(() => {
        return () => {
            positionRef.current.set(0, 0, 0);
            if (meshRef.current) {
                meshRef.current.rotation.set(0, 0, 0);
            }
        };
    }, []);

    if (!isRolling) {
        return null;
    }

    return (
        <mesh ref={meshRef} position={[0, 0, 0]}>
            <boxGeometry args={[2.2, 2.2, 2.2]} />
            {materials.map((mat, i) => (
                <primitive attach={`material-${i}`} object={mat} key={i} />
            ))}
        </mesh>
    );
}

function CameraAnimator({ phase }: { phase: Phase }) {
    const { camera } = useThree();

    useFrame(() => {
        if (phase === "done") {
            const topView = new THREE.Vector3(0, 5, 0.001);
            camera.position.lerp(topView, 0.2);
            camera.lookAt(0, 0, 0);
        } else {
            const defaultView = new THREE.Vector3(5, 5, 5);
            camera.position.lerp(defaultView, 0.1);
            camera.lookAt(0, 0, 0);
        }
    });

    return null;
}

export default function DiceRoller({ dice }: { dice: ReturnType<typeof useDice> }) {
    const { isRolling, phase, targetRotation, color, setPhase } = dice;

    return (
        <div
            className={`absolute top-[53%] left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[400px] h-[400px]`}
        >
            <Canvas camera={{ position: [0, 0, 5], fov: 60 }}>
                <ambientLight intensity={1.5} color={0xffffff} />
                <Dice
                    isRolling={isRolling}
                    targetRotation={targetRotation}
                    phase={phase}
                    color={color}
                    onFinish={() => setPhase("done")}
                />
                <CameraAnimator phase={phase} />
            </Canvas>
        </div>
    );
}
