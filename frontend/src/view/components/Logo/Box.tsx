import React from "react";

export const LogoBox = ({ className }: { className?: string }) => {
    return (
        <div className={"flex flex-col gap-2 " + className}>
            <div className="w-[200px] h-[200px] rounded-2xl bg-yellow-mat flex items-center justify-center shadow-2xl">
                <span className="text-white uppercase font-bold text-2xl"
                    style={{ fontFamily: "Montserrat, sans-serif" }}>
                    Camel Up
                </span>
            </div>
            <div className="w-[160px] h-[5px] rounded-full bg-yellow-mat mx-auto" />
        </div>
    )
}