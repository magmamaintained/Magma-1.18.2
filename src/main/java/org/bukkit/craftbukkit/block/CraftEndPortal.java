package org.bukkit.craftbukkit.block;

import net.minecraft.world.level.block.entity.TileEntityEnderPortal;
import org.bukkit.World;

public class CraftEndPortal extends CraftBlockEntityState<TileEntityEnderPortal> {

    public CraftEndPortal(World world, TileEntityEnderPortal tileEntity) {
        super(world, tileEntity);
    }
}
