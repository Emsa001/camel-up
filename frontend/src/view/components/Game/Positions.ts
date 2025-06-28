const getPosition = (index: number) => {
    if (index < 3 || index > 14) {

        if(index > 14){
            index = index - 16;
        }

        return {
            top: `${(index + 1) * 20}%`,
            right: "3%",
            rotate: "180deg",
        };
    }

    if (index < 7) {
        return {
            top: "82%",
            right: `${(index - 3) * 20 + 3}%`,
            rotate: "270deg",
        };
    }

    if (index < 11) {
        return {
            top: `${(11 - index) * 20}%`,
            left: `3%`,
            rotate: "360deg",
        };
    }

    if(index < 15){
        return {
            top: "-2%",
            left: `${(index - 11) * 20 + 3}%`,
            rotate: "450deg",
        };
    }

    return {
        top: "0%",
        left: "0%",
        rotate: "0deg",
    }
};

const getPlacePosition = (index: number) => {
    if (index < 3 || index > 14) {

        if(index > 14){
            index = index - 16;
        }

        return {
            top: `${(index + 1.2) * 21}%`,
            right: "7%",
            rotate: "180deg",
        };
    }

    if (index < 7) {
        return {
            top: "88%",
            right: `${(index - 3) * 20 + 7}%`,
            rotate: "270deg",
        };
    }

    if (index < 11) {
        return {
            top: `${(11 - index) * 22}%`,
            left: `7%`,
            rotate: "360deg",
        };
    }

    if(index < 15){
        return {
            top: "4%",
            left: `${(index - 11) * 20 + 7}%`,
            rotate: "450deg",
        };
    }

    return {
        top: "5%",
        left: "0%",
        rotate: "0deg",
    }
};

const getTilePosition = (index: number) => {
    if (index < 3 || index > 14) {

        if(index > 14){
            index = index - 16;
        }

        return {
            top: `${(index + 1.1) * 20}%`,
            right: "5%",
            rotate: "180deg",
        };
    }

    if (index < 7) {
        return {
            top: "84%",
            right: `${(index - 3) * 20 + 5}%`,
            rotate: "270deg",
        };
    }

    if (index < 11) {
        return {
            top: `${(11 - index) * 21}%`,
            left: `4%`,
            rotate: "360deg",
        };
    }

    if(index < 15){
        return {
            top: "2%",
            left: `${(index - 11) * 20 + 4}%`,
            rotate: "450deg",
        };
    }

    return {
        top: "5%",
        left: "0%",
        rotate: "0deg",
    }
};

export { getPosition, getPlacePosition, getTilePosition };