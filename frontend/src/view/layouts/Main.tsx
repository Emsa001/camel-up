import React from "react";
import { Outlet } from "react-router-dom";

/*

    The layout is the main component that wraps all the pages in the app.
    We can add stuff like navbar, footer, etc. here. And it will be visible in every page

*/

export default function MainLayout() {
    return (
        <div>
            <Outlet />
        </div>
    );
}
