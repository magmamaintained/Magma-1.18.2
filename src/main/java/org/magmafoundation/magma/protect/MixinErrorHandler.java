package org.magmafoundation.magma.protect;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinErrorHandler implements IMixinErrorHandler {

    public ErrorAction onPrepareError(IMixinConfig config, Throwable th, IMixinInfo mixin, ErrorAction action) {
        return action; //Use mixin default handling
    }

    public ErrorAction onApplyError(String targetClassName, Throwable th, IMixinInfo mixin, ErrorAction action) {
        if (action == ErrorAction.ERROR)
            InjectProtect.mixinInjectCaught(mixin, th);
        return action;
    }
}
