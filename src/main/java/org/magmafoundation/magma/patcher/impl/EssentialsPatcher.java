package org.magmafoundation.magma.patcher.impl;

import org.magmafoundation.magma.patcher.Patcher;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.List;

@Patcher.PatcherInfo(name = "Essentials", description = "Patches LegacyQueryHandler errors")
public class EssentialsPatcher extends Patcher {

    public byte[] transform(String className, byte[] clazz) {
        if (className.equals("com.earth2me.essentials.EssentialsServerListener"))
            return patchServerListener(clazz);

        return clazz;
    }

    private byte[] patchServerListener(byte[] clazz) {
        ClassNode node = new ClassNode();
        new ClassReader(clazz).accept(node, 0);
        new ArrayChanger(node);

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);

        return writer.toByteArray();
    }

    static class ArrayChanger extends ClassVisitor {

        private final List<String> ignoredSLPECallers = new ArrayList<>();

        protected ArrayChanger(ClassNode node) {
            super(Opcodes.ASM9);
            node.accept(this);

            ignoredSLPECallers.add(".LegacyQueryHandler.channelRead("); //Forge version of this class
            ignoredSLPECallers.add(".LegacyPingHandler.channelRead(");
            ignoredSLPECallers.add("de.dytanic.cloudnet.bridge.BukkitBootstrap");
            ignoredSLPECallers.add("de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetBridgePlugin");
        }

        public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
            if (name.equals("ignoredSLPECallers"))
                return super.visitField(access, name, descriptor, signature, ignoredSLPECallers);

            return super.visitField(access, name, descriptor, signature, value);
        }
    }
}
