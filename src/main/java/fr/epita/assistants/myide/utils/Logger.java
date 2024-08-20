package fr.epita.assistants.myide.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger
{
    
    private static final String RESET_TEXT = "\u001B[0m";
    private static final String RED_TEXT = "\u001B[31m";
    private static final String CYAN_TEXT = "\u001B[36m";
    private static final String PURPLE_TEXT = "\u001B[35m";
    private static final String GREEN_TEXT = "\u001B[32m";

    private static String getDate()
    {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static void log(String message)
    {
        System.out.println("[" + getDate() + "] " + message);
    }

    public static void logInfo(String message)
    {
        System.err.println(CYAN_TEXT + "[" + getDate() + "] INFO: " + message + RESET_TEXT);
    }

    public static void logWarn(String message)
    {
        System.err.println(PURPLE_TEXT + "[" + getDate() + "] WARN: " + message + RESET_TEXT);
    }

    public static void logDebug(String message)
    {
        System.err.println(GREEN_TEXT + "[" + getDate() + "] DEBUG: " + message + RESET_TEXT);
    }
    
    public static void logError(String message)
    {
        System.err.println(RED_TEXT + "[" + getDate() + "] ERROR: " + message + RESET_TEXT);
    }

    public static void logParamEndpoint(String endpoint, String... params) {
        StringBuilder message = new StringBuilder("Endpoint: " + endpoint + " and its parameters: ");
        for (String param : params) {
            message.append(param);
            message.append(" ");
        }
        log(message.toString());
    }
}