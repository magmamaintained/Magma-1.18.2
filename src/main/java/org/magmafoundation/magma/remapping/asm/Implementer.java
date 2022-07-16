package org.magmafoundation.magma.remapping.asm;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.tree.ClassNode;

/**
 * Project: Magma
 * <p>
 * Author: Malcolm (M1lc0lm)
 * Date: 16.07.2022 - 23:28
 */
public interface Implementer {

    boolean processClass(ClassNode node, ILaunchPluginService.ITransformerLoader transformerLoader);
}