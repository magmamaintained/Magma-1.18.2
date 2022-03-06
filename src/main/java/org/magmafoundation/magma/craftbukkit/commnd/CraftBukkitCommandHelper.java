package org.magmafoundation.magma.craftbukkit.commnd;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * CraftBukkitCommandHelper
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 18/02/2022 - 06:18 am
 */
public class CraftBukkitCommandHelper {

    private static final Field BRIGADIER_CHILDREN;
    private static final Field BRIGADIER_LITERALS;
    private static final Field BRIGADIER_ARGUMENTS;

    static {
        Field children = null;
        Field literals = null;
        Field arguments = null;
        try {
            children = CommandNode.class.getDeclaredField("children");
            literals = CommandNode.class.getDeclaredField("literals");
            arguments = CommandNode.class.getDeclaredField("arguments");
            children.setAccessible(true);
            literals.setAccessible(true);
            arguments.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        BRIGADIER_CHILDREN = children;
        BRIGADIER_LITERALS = literals;
        BRIGADIER_ARGUMENTS = arguments;
    }

    public static void removeCommand(RootCommandNode commandNode, String name) {
        try {
            ((Map<?, ?>) BRIGADIER_CHILDREN.get(commandNode)).remove(name);
            ((Map<?, ?>) BRIGADIER_LITERALS.get(commandNode)).remove(name);
            ((Map<?, ?>) BRIGADIER_ARGUMENTS.get(commandNode)).remove(name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
