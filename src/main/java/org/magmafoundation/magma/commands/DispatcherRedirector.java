package org.magmafoundation.magma.commands;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.util.IgnoreUtil;

import java.util.Collections;
import java.util.Iterator;

public final class DispatcherRedirector {

    //We don't want nor need mutability in these lists currently.
    private static final ObjectImmutableList<String> BYPASSED_CLASSES = ObjectImmutableList.of("org.popcraft.chunky.ChunkyForge", "team.creative.playerrevive.PlayerRevive");


    public static boolean shouldBypass() {
        if (MagmaConfig.instance.debugOverrideDispatRedirector.getValues())
            return false;
        //use a StackWalker, in hopes it has better performance than
        //`Thread.currentThread().getStackTrace()` or `(new Exception()).getStackTrace()`.
        StackWalker walker = StackWalker.getInstance(Collections.emptySet(), 17);
        return walker.walk(stackFrameStream -> {
                    Iterator<StackWalker.StackFrame> stackFrameIterator = stackFrameStream.iterator();
                    while (stackFrameIterator.hasNext()) {
                        final String className = stackFrameIterator.next().getClassName();
                        if (BYPASSED_CLASSES.contains(className) && IgnoreUtil.shouldCheck(className))
                            return true;
                    }
                    return false;
                }
        );
    }
}
