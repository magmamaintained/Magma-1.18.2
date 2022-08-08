package org.magmafoundation.magma.commands;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

/**
 * Project: Magma
 * <p>
 * Author: Malcolm (M1lc0lm)
 * Date: 08.08.2022 - 15:15
 */
public class MagmaCommandNode {

    public static <S> boolean canUse(CommandNode<S> node, S source) {
        if (source instanceof CommandSourceStack) {
            try {
                ((CommandSourceStack)source).currentCommand = node;
                return node.canUse(source);
            }
            finally {
                ((CommandSourceStack)source).currentCommand = null;
            }
        }
        return node.canUse(source);
    }



}
