package de.onevision.Platform.Exceptions;

public class Nomatch extends Base {
    public Nomatch(String errorMessage) {
        super(errorMessage);
    }

    public Nomatch(String errorMessage, String formatString) {
        super(errorMessage, formatString);
    }

    public Nomatch(String errorMessage, String formatString, String debugMessage) {
        super(errorMessage, formatString, debugMessage);
    }

    @Override
    public void handle() {
        super.handle();
    }
}
