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
import org.magmafoundation.magma.utils.BSLPreInit;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.SystemType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.magmafoundation.magma.MagmaConstants.*;

/**
 * org.magmafoundation.magma.MagmaStart
 *
 * @author Hexeption admin@hexeption.co.uk / Malcolm
 * @since 24/05/2022 - 7:21 pm
 */
public class MagmaStart {

    public static List<String> mainArgs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        mainArgs.addAll(List.of(args));
        BetterUI.printTitle(NAME, BRAND, System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")", VERSION, BUKKIT_VERSION, FORGE_VERSION);

        if(Float.parseFloat(System.getProperty("java.class.version")) < 61) {
            System.out.println("[MAGMA] Your Java version is not supported. Please update to Java 17 or higher.");
            System.exit(1);
        }

        if(mainArgs.contains("-noserver")) {
            System.out.println("[MAGMA] Argument -noserver detected. Skipping server startup.");
            System.exit(1); //-noserver -> Do not run the Minecraft server, only let the installation running.
        }

        new MagmaInstaller();

        List<String> launchArgs = JarTool.readFileLinesFromJar("data/" + (SystemType.getOS().equals(SystemType.OS.WINDOWS) ? "win" : "unix") + "_args.txt");
        List<String> forgeArgs = new ArrayList<>();
        launchArgs.stream()
                .filter(s -> s.startsWith("--launchTarget") ||
                        s.startsWith("--fml.forgeVersion") ||
                        s.startsWith("--fml.mcVersion") ||
                        s.startsWith("--fml.forgeGroup") ||
                        s.startsWith("--fml.mcpVersion")).
                findAny().
                ifPresent(s -> {
                    forgeArgs.add(s.split(" ")[0]);
                    forgeArgs.add(s.split(" ")[1]);
                });

        BSLPreInit bslPreInit = new BSLPreInit();
        bslPreInit.setupStartup(launchArgs);

        if(!BetterUI.checkEula(Paths.get("eula.txt")))
            System.exit(0);

        if(Arrays.stream(args).
                anyMatch(s -> s.contains("-dau")))
            Arrays.stream(args).
                    filter(s -> s.contains("-dau")).
                    findFirst().
                    ifPresent(o -> mainArgs.remove(o));
        else
            MagmaUpdater.checkForUpdates();

        try {
            var cl = Class.forName("cpw.mods.bootstraplauncher.BootstrapLauncher");
            var method = cl.getMethod("main", String[].class);
            method.invoke(null, (Object) Stream.concat(forgeArgs.stream(), mainArgs.stream()).toArray(String[]::new));
        } catch (Exception e) {
            System.out.println("Installation finished, please restart server.");
            System.exit(0);
        }
    }

}
