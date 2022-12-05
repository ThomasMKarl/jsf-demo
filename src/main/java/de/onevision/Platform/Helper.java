package de.onevision.Platform;

import java.util.Optional;

public class Helper {
    static public <T> T valueOr(Optional<T> in, T alt) {
        if (in.isPresent()) {
            return in.get();
        }
        return alt;
    }
}
