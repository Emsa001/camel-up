/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/**/*.{js,jsx,ts,tsx}", 
        "node_modules/daisyui/dist/**/*.js", 
        "node_modules/react-daisyui/dist/**/*.js"
    ],
    theme: {
        extend: {
            colors: {
                "yellow-primary": "#E8CC7B",
                "yellow-secondary": "#E6C17C",
                "yellow-dark": "#BF9455",
                "yellow-mat": "#8B7841",
                "gold": "#C4A85C",
                "blue-mat": "#59769F",
                "purple-primary": "#383242",
                "purple-dark": "#2D2834",
                "purple-light": "#4A4554",
                "green-primary": "#00FF44",
                "dark-primary": "#1B1B1B",
            },
        },
    },
    plugins: [require('daisyui')],
};
