package org.magmafoundation.magma.commands;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.util.IgnoreUtil;

import java.util.Collections;

public final class DispatcherRedirector {

    //We don't want nor need mutability in these lists currently.
    private static final ObjectImmutableList<String> BYPASSED_CLASSES = ObjectImmutableList.of("org.popcraft.chunky.ChunkyForge", "team.creative.playerrevive.PlayerRevive");


    public static boolean shouldBypass() {
        if(MagmaConfig.instance.debugOverrideDispatRedirector.getValues())
            return false;
        //use a StackWalker, in hopes it has better performance than
        //`Thread.currentThread().getStackTrace()` or `(new Exception()).getStackTrace()`.
        StackWalker walker = StackWalker.getInstance(Collections.emptySet());
        return walker.walk(stackFrameStream ->
            stackFrameStream.map(StackWalker.StackFrame::getClassName)
                    //ideally the order of checking BYPASSED_CLASSES and IgnoreUtil.shouldCheck should not matter
                    //and also BYPASSED_CLASSES should ideally be distinct from the classes in IgnoreUtil.DO_NOT_CHECK
                    //
                    //at the time of writing this, this should be the case, and BYPASSED_CLASSES is substancially smaller than IgnoreUtil.DO_NOT_CHECK.
                    //so I reversed the order of checking to make it faster.
                    .anyMatch(className -> BYPASSED_CLASSES.contains(className) && IgnoreUtil.shouldCheck(className))
        );
    }
}
