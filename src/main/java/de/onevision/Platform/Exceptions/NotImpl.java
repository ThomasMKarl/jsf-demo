package de.onevision.Platform.Exceptions;

public class NotImpl extends Base {
    public NotImpl(String errorMessage) {
        super(errorMessage);
    }

    public NotImpl(String errorMessage, String formatString) {
        super(errorMessage, formatString);
    }

    public NotImpl(String errorMessage, String formatString, String debugMessage) {
        super(errorMessage, formatString, debugMessage);
    }

    @Override
    public void handle() {
        super.handle();
    }
}
