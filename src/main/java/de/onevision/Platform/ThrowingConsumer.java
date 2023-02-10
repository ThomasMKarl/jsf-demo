package de.onevision.Platform;

@FunctionalInterface
public interface ThrowingConsumer<U, V, E extends Exception> {
    void accept(U u, V v) throws E;
}