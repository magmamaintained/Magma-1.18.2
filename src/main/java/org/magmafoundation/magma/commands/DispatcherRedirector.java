package org.magmafoundation.magma.commands;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.util.IgnoreUtil;

import java.util.Collections;
import java.util.Spliterator;

public final class DispatcherRedirector {

    //We don't want nor need mutability in these lists currently.
    private static final ObjectImmutableList<String> BYPASSED_CLASSES = ObjectImmutableList.of("org.popcraft.chunky.ChunkyForge", "team.creative.playerrevive.PlayerRevive");


    public static boolean shouldBypass() {
        if (MagmaConfig.instance.debugOverrideDispatcherRedirector.getValues())
            return false;
        //use a StackWalker, in hopes it has better performance than
        //`Thread.currentThread().getStackTrace()` or `(new Exception()).getStackTrace()`.
        StackWalker walker = StackWalker.getInstance(Collections.emptySet(), 17);
        return walker.walk(stackFrameStream -> {
                    Spliterator<StackWalker.StackFrame> stackFrameIterator = stackFrameStream.spliterator();
                    final boolean[] failFast = {false};
                    do {
                    } while (
                            stackFrameIterator.tryAdvance(element -> {
                                final String className = element.getClassName();
                                //ideally the order of checking BYPASSED_CLASSES and IgnoreUtil.shouldCheck should not matter
                                //and also BYPASSED_CLASSES should ideally be distinct from the classes in IgnoreUtil.DO_NOT_CHECK
                                //
                                //at the time of writing this, this should be the case, and BYPASSED_CLASSES is substantially smaller than IgnoreUtil.DO_NOT_CHECK.
                                //so I reversed the order of checking to make it faster.
                                failFast[0] = BYPASSED_CLASSES.contains(className) && IgnoreUtil.shouldCheck(className);
                            }) && !failFast[0]
                    );
                    return failFast[0];
                }
        );
    }
}
