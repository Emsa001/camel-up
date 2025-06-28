/*

This is very ugly code for copying text to clipboard.
It is ugly because browsers are stupid and do not allow copying using navigator.clipboard unless it's https.
So for http we have to create a textarea, select it, and use document.execCommand('copy').

*/

export const copyText = (text: string) => {
    if (navigator.clipboard?.writeText) {
        // Modern Clipboard API (works in HTTPS or localhost)
        navigator.clipboard.writeText(text).catch((err) => {
            console.error("Failed to copy text: ", err);
        });
    } else {
        // Fallback for HTTP or older browsers
        const textArea = document.createElement("textarea");
        textArea.value = text;
        // Avoid scrolling to bottom
        textArea.style.position = "fixed";
        textArea.style.top = "0";
        textArea.style.left = "0";
        textArea.style.width = "2em";
        textArea.style.height = "2em";
        textArea.style.padding = "0";
        textArea.style.border = "none";
        textArea.style.outline = "none";
        textArea.style.boxShadow = "none";
        textArea.style.background = "transparent";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
            const successful = document.execCommand("copy");
            if (!successful) {
                console.error("Fallback: Copy command was unsuccessful");
            }
        } catch (err) {
            console.error("Fallback: Unable to copy", err);
        }

        document.body.removeChild(textArea);
    }
};