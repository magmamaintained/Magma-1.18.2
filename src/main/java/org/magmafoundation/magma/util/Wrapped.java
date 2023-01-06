package org.magmafoundation.magma.util;

//Utility class for wrapping random values, this is used to bypass LuckPerms grabbing the wrong dispatcher on boot
public class Wrapped<T> {

    private final T value;

    private Wrapped(T value) {
        this.value = value;
    }

    public static <T> Wrapped<T> wrap(T value) {
        return new Wrapped<>(value);
    }

    public T unwrap() {
        return value;
    }
}
