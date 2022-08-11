/*
 * Magma Server
 * Copyright (C) 2019-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.patcher.impl;

import org.bukkit.Material;
import org.magmafoundation.magma.patcher.Patcher;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * WorldEditPatcher
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/12/2021 - 06:50 pm
 */
@Patcher.PatcherInfo(name = "WorldEdit", description = "Patches WorldEdit")
public class WorldEditPatcher extends Patcher {

    @Override
    public byte[] transform(String className, byte[] basicClass) {
        switch (className) {
            case "com.sk89q.worldedit.bukkit.WorldEditPlugin":
                System.setProperty("worldedit.bukkit.adapter", "com.sk89q.worldedit.bukkit.adapter.impl.v1_18_R2.PaperweightAdapter");
                return patchWorldEditPlugin(basicClass);
            case "com.sk89q.worldedit.bukkit.BukkitAdapter":
                return patchBukkitAdapter(basicClass);
            case "com.sk89q.worldedit.bukkit.adapter.impl.v1_18_R2.PaperweightAdapter":
                return patchSpigot_v1_18_R2(basicClass);
        }
        return basicClass;
    }

    public static Material adaptHook(String type) {
        checkNotNull(type);
        return Material.getMaterial((!type.startsWith("minecraft:") ? type.replace(":", "_") : type.substring(10)).toUpperCase(Locale.ROOT));
    }

    private byte[] patchWorldEditPlugin(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for (MethodNode methodNode : node.methods) {
            injectGetKeyForge(methodNode);
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    private byte[] patchBukkitAdapter(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals("adapt") && methodNode.desc.equals("(Lcom/sk89q/worldedit/world/block/BlockType;)Lorg/bukkit/Material;")) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/sk89q/worldedit/world/block/BlockType", "getId", "()Ljava/lang/String;"));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(WorldEditPatcher.class), "adaptHook", "(Ljava/lang/String;)Lorg/bukkit/Material;"));
                insnList.add(new InsnNode(Opcodes.ARETURN));
                methodNode.instructions = insnList;
            }

            if (methodNode.name.equals("adapt") && methodNode.desc.equals("(Lcom/sk89q/worldedit/world/item/ItemType;)Lorg/bukkit/Material;")) {
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/sk89q/worldedit/world/item/ItemType", "getId", "()Ljava/lang/String;"));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(WorldEditPatcher.class), "adaptHook", "(Ljava/lang/String;)Lorg/bukkit/Material;"));
                insnList.add(new InsnNode(Opcodes.ARETURN));
                methodNode.instructions = insnList;
            }

            injectGetKeyForge(methodNode);
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    private void injectGetKeyForge(MethodNode methodNode) {
        for (AbstractInsnNode insnNode : methodNode.instructions) {
            if (insnNode instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                if (methodInsnNode.owner.equals("org/bukkit/Material") && methodInsnNode.name.equals("getKey") && methodInsnNode.desc.equals("()Lorg/bukkit/NamespacedKey;")) {
                    methodInsnNode.name = "getKeyForge";
                }
            }
        }
    }

    private byte[] patchSpigot_v1_18_R2(byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        for1:
        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.equals("getProperties") && methodNode.desc.equals("(Lcom/sk89q/worldedit/world/block/BlockType;)Ljava/util/Map;")) {
                for (AbstractInsnNode insnNode : methodNode.instructions) {
                    if (insnNode instanceof InsnNode) {
                        if (insnNode.getOpcode() == Opcodes.ATHROW) {
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.POP));
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyMap", "()Ljava/util/Map;"));
                            insnList.add(new InsnNode(Opcodes.ARETURN));
                            methodNode.instructions.insertBefore(insnNode, insnList);
                            methodNode.instructions.remove(insnNode);
                            break for1;
                        }
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }
}
