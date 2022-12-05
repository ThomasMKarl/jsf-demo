package de.onevision.Platform.Exceptions;

import org.apache.logging.log4j.*;

public abstract class Base extends Exception {
    public Base(String errorMessage) {
        super(errorMessage.trim());
        removeLB(super.getMessage());
    }

    public Base(String errorMessage, String formatString) {
        super(errorMessage.trim());
        removeLB(super.getMessage());
        this.formatString = removeLB(formatString.trim());
    }

    public Base(String errorMessage, String formatString, String debugMessage) {
        super(errorMessage.trim());
        removeLB(super.getMessage());
        this.formatString = removeLB(formatString.trim());
        this.debugMessage = removeLB(debugMessage.trim());
    }

    public void handle() {
        logger.error(super.getMessage());
        if (!formatString.isEmpty())
            logger.info(formatString);
        if (!debugMessage.isEmpty())
            logger.debug(debugMessage);
        logger.debug(this.getStackTrace());
    }

    private String removeLB(String in) {
        while (in.endsWith("\n")) {
            in = in.substring(0, in.length() - 1);
        }
        return in;
    }

    private String formatString = new String();
    private String debugMessage = new String();

    private static Logger logger = LogManager.getLogger(Base.class.getName());
}
