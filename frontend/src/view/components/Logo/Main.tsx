import React from "react";

export const LogoMain = ({ className }: { className?: string }) => {
    return (
        <div className={"flex flex-col gap-2 " + className}>
            <div className="w-[300px] h-[100px] rounded-2xl bg-yellow-primary flex items-center justify-between p-6 shadow-2xl">
                <span className="text-white uppercase font-black text-4xl"
                    style={{ fontFamily: "Montserrat, sans-serif" }}>
                    Camel
                </span>
                <div className="flex items-center justify-center text-black text-4xl font-black uppercase bg-white w-[80px] h-[80px] rounded-md">up</div>
            </div>
            <div className="w-[160px] h-[5px] rounded-full bg-yellow-mat mx-auto" />
        </div>
    )
}