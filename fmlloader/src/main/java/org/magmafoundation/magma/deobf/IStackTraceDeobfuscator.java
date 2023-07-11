package org.magmafoundation.magma.deobf;

public interface IStackTraceDeobfuscator {

    void deobf(Throwable throwable);
    StackTraceElement[] deobf(StackTraceElement[] stackTrace);
}
