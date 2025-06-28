import React from "react";
import { Button } from "react-daisyui";

export const AuthorsFooter = () => {
    const authors = [
        { name: "Emanuel" },
        { name: "Beqa" },
        { name: "Thies" },
        { name: "Marcel" },
        { name: "Konrad" },
        { name: "Wladimir" },
    ];

    return (
        <div className="fixed bottom-10 z-[10]">
            <h1 className="text-center">Created By:</h1>
            <div className="flex items-center justify-center gap-2 mt-2">
                {authors.map((author, index) => (
                    <Button key={index} className="bg-purple-dark" size="sm">
                        {author.name}
                    </Button>
                ))}
            </div>
        </div>
    );
};
