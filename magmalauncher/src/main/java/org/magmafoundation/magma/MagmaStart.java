package org.magmafoundation.magma;/*
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

import org.magmafoundation.magma.betterui.BetterUI;
import org.magmafoundation.magma.installer.MagmaInstaller;
import org.magmafoundation.magma.updater.MagmaUpdater;
import org.magmafoundation.magma.utils.BootstrapLauncher;
import org.magmafoundation.magma.common.utils.JarTool;
import org.magmafoundation.magma.utils.ServerInitHelper;
import org.magmafoundation.magma.common.utils.SystemType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.magmafoundation.magma.common.MagmaConstants.*;


/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm) / Hexeption
 * @date 03.07.2022 - 17:19
 */
public class MagmaStart {

    public static List<String> mainArgs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        mainArgs.addAll(List.of(args));
        BetterUI.printTitle(NAME, BRAND, System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")", VERSION, BUKKIT_VERSION, FORGE_VERSION);

        //Temporary warning for people using the new server jar
        System.err.println("WARNING: The new server jar is still under development and will be unstable! If you experience any issues, please report them to the developers.");
        System.err.println("WARNING: If the server crashes while installing, try removing the libraries folder and launching the server again.");
        //Temporary warning for people using the new server jar

        if(!BetterUI.checkEula(Paths.get("eula.txt"))) System.exit(0);

        List<String> launchArgs = JarTool.readFileLinesFromJar("data/" + (SystemType.getOS().equals(SystemType.OS.WINDOWS) ? "win" : "unix") + "_args.txt");
        List<String> forgeArgs = new ArrayList<>();
        launchArgs.stream().filter(s -> s.startsWith("--launchTarget") || s.startsWith("--fml.forgeVersion") || s.startsWith("--fml.mcVersion") || s.startsWith("--fml.forgeGroup") || s.startsWith("--fml.mcpVersion")).toList().forEach(arg -> {
            forgeArgs.add(arg.split(" ")[0]);
            forgeArgs.add(arg.split(" ")[1]);
        });

        new MagmaInstaller();

        ServerInitHelper.init(launchArgs);

        if(Arrays.stream(args).anyMatch(s -> s.contains("-dau")))
            Arrays.stream(args).filter(s -> s.contains("-dau")).findFirst().ifPresent(o -> mainArgs.remove(o));
        else MagmaUpdater.checkForUpdates();

        String[] invokeArgs = Stream.concat(forgeArgs.stream(), mainArgs.stream()).toArray(String[]::new);
        if(BootstrapLauncher.startServer(invokeArgs)) {
            try {
                Class.forName("org.jline.terminal.Terminal");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
