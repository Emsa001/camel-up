package tuc.isse.utils;

public class Logger {
    private static final String RESET = "\u001B[0m";

    private static final String RED_LABEL = "\u001B[38;2;255;0;0m";         // Bright red
    private static final String GREEN_LABEL = "\u001B[38;2;0;200;0m";       // Medium green
    private static final String YELLOW_LABEL = "\u001B[38;2;255;200;0m";    // Bright yellow
    private static final String CYAN_LABEL = "\u001B[38;2;0;180;255m";      // Bright cyan

    private static final String RED_MSG = "\u001B[38;2;255;180;180m";       // Pastel red
    private static final String GREEN_MSG = "\u001B[38;2;200;255;200m";     // Pastel green
    private static final String YELLOW_MSG = "\u001B[38;2;255;255;200m";    // Pastel yellow
    private static final String CYAN_MSG = "\u001B[38;2;200;240;255m";      // Pastel cyan

    public static void log(String message) {
        System.out.println(CYAN_LABEL + "[LOG] " + CYAN_MSG + message + RESET);
    }

    public static void error(String message) {
        System.err.println(RED_LABEL + "[ERROR] " + RED_MSG + message + RESET);
    }

    public static void warn(String message) {
        System.out.println(YELLOW_LABEL + "[WARN] " + YELLOW_MSG + message + RESET);
    }

    public static void info(String message) {
        System.out.println(GREEN_LABEL + "[INFO] " + GREEN_MSG + message + RESET);
    }
}