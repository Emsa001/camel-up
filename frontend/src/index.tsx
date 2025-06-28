// frontend/src/index.tsx
import "./view/styles/index.css";

import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";

import MainLayout from "@layouts/Main";
import App        from "@pages/Home/App";
import Lobby      from "@pages/Lobby/Lobby";
import Game       from "@pages/Game/Game";

import { AlertProvider } from "providers/AlertProvider";
import { GameProvider }  from "@providers";
import { AudioProvider } from "@providers";

const root = ReactDOM.createRoot(document.getElementById("root")!);

root.render(
    <React.StrictMode>
        <AlertProvider>
            <BrowserRouter>
                <GameProvider>
                    <AudioProvider>           {/* ← Hier beginnen */}
                        <Routes>
                            <Route path="/" element={<MainLayout />}>
                                <Route index element={<App />} />
                                <Route path="lobby" element={<Lobby />} />
                                <Route path="game/:roomCode" element={<Game />} />
                            </Route>
                        </Routes>
                    </AudioProvider>          {/* ← Hier enden */}
                </GameProvider>
            </BrowserRouter>
            <ToastContainer />
        </AlertProvider>
    </React.StrictMode>
);
