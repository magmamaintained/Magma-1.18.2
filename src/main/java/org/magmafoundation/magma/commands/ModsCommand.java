package org.magmafoundation.magma.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.magmafoundation.magma.common.MagmaConstants;

public class ModsCommand extends BukkitCommand {

    public ModsCommand(@NotNull String name) {
        super(name);
        this.description = "Gets a list of mods running on the server";
        this.usageMessage = "/mods";
        this.setPermission("magma.command.mods");
        this.setAliases(Arrays.asList("modlist"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String currentAlias, @NotNull String[] args) {
        if (!testPermission(sender)) return true;

        sender.sendMessage("Mods " + getModList());
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    @NotNull
    private String getModList() {
        StringBuilder modList = new StringBuilder();
        List<IModInfo> mods = MagmaConstants.modInfoList;

        for (IModInfo mod : mods) {
            if (modList.length() > 0) {
                modList.append(ChatColor.WHITE);
                modList.append(", ");
            }

            modList.append(ChatColor.GREEN + mod.getDisplayName());
            modList.append(ChatColor.WHITE + " (" + mod.getModId() + " v." + mod.getVersion() + ")");
        }

        return "(" + mods.size() + "): " + modList.toString();
    }
}
