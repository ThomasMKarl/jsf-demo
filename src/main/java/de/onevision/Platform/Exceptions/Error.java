package de.onevision.Platform.Exceptions;

public class Error extends Base {
    public Error(String errorMessage) {
        super(errorMessage);
    }

    public Error(String errorMessage, String formatString) {
        super(errorMessage, formatString);
    }

    public Error(String errorMessage, String formatString, String debugMessage) {
        super(errorMessage, formatString, debugMessage);
    }

    @Override
    public void handle() {
        super.handle();
    }
}
