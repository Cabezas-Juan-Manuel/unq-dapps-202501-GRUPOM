package ar.edu.unq.pronostico.deportivo.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppLogger {

    private static final Logger logger = Logger.getLogger("pronosticoDeportivo");

    private AppLogger() {
    }

    static {
        // Remove the handlers by default
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();

        Formatter customFormatter = new Formatter() {
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            @Override
            public String format(LogRecord logRecord) {
                return String.format("%s [%-7s] %s.%s: %s%n",
                        formatter.format(new Date(logRecord.getMillis())),
                        logRecord.getLevel().getLocalizedName(),
                        logRecord.getSourceClassName(),
                        logRecord.getSourceMethodName(),
                        logRecord.getMessage());
            }
        };
        handler.setFormatter(customFormatter);

        logger.addHandler(handler);

        // Logger default level
        logger.setLevel(Level.INFO);
    }

    private static void log(Level level, String className, String methodName, String message) {
        logger.logp(level, className, methodName, message);
    }

    public static void info(String className, String methodName, String message) {
        log(Level.INFO, className, methodName, message);
    }

    public static void debug(String className, String methodName, String message) {
        log(Level.FINE, className, methodName, message);
    }

    public static void warn(String className, String methodName, String message) {
        log(Level.WARNING, className, methodName, message);
    }

    public static void error(String className, String methodName, String message) {
        log(Level.SEVERE, className, methodName, message);
    }
}
