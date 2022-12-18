package org.magmafoundation.magma.common.utils;

public class ShortenedStackTrace {

    private final StackTraceElement[] stackTrace;
    private final int maxElements;

    public ShortenedStackTrace(Throwable error, int maxElements) {
        this.stackTrace = findCause(error).getStackTrace();
        this.maxElements = maxElements;
    }

    public static Throwable findCause(Throwable error) {
        while (error.getCause() != null) {
            error = error.getCause();
        }
        return error;
    }

    public void print() {
        for (int i = 0; i < maxElements; i++) {
            System.out.println(stackTrace[i]);
        }
    }
}
