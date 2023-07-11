package org.magmafoundation.magma.deobf;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

@Plugin(name = "DeobfRewritePolicy", category = Core.CATEGORY_NAME, elementType = "rewritePolicy", printObject = true)
public class DeobfRewritePolicy implements RewritePolicy {

    private static final IStackTraceDeobfuscator noop = new IStackTraceDeobfuscator() {
        public void deobf(Throwable throwable) {}
        public StackTraceElement[] deobf(StackTraceElement[] stackTrace) { return stackTrace; }
    };

    private static IStackTraceDeobfuscator deobfuscator;

    public static void setDeobfuscator(IStackTraceDeobfuscator deobfuscator) {
        DeobfRewritePolicy.deobfuscator = deobfuscator;
    }

    public static IStackTraceDeobfuscator getDeobfuscator() {
        return deobfuscator != null ? deobfuscator : noop;
    }

    private DeobfRewritePolicy() {}

    public LogEvent rewrite(LogEvent source) {
        Throwable throwable = source.getThrown();
        if (throwable != null) {
            getDeobfuscator().deobf(throwable);
            return new Log4jLogEvent.Builder(source).setThrownProxy(null).build();
        }
        return source;
    }

    @PluginFactory
    public static DeobfRewritePolicy createPolicy() {
        return new DeobfRewritePolicy();
    }
}
