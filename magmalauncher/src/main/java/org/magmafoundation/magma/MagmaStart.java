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

import com.google.common.reflect.ClassPath;
import org.magmafoundation.magma.betterui.BetterUI;
import org.magmafoundation.magma.installer.MagmaInstaller;
import org.magmafoundation.magma.updater.MagmaUpdater;
import org.magmafoundation.magma.utils.BSLPreInit;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.SystemType;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.magmafoundation.magma.MagmaConstants.*;


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

        if(mainArgs.contains("-noserver")) {
            System.out.println("[MAGMA] Argument -noserver detected. Skipping server startup.");
            System.exit(1); //-noserver -> Do not run the Minecraft server, only let the installation running.
        }

        List<String> launchArgs = JarTool.readFileLinesFromJar("data/" + (SystemType.getOS().equals(SystemType.OS.WINDOWS) ? "win" : "unix") + "_args.txt");
        List<String> forgeArgs = new ArrayList<>();
        launchArgs.stream()
                .filter(s -> s.startsWith("--launchTarget") ||
                        s.startsWith("--fml.forgeVersion") ||
                        s.startsWith("--fml.mcVersion") ||
                        s.startsWith("--fml.forgeGroup") ||
                        s.startsWith("--fml.mcpVersion")).
                toList().
                forEach(arg -> {
                    forgeArgs.add(arg.split(" ")[0]);
                    forgeArgs.add(arg.split(" ")[1]);
                });

        new MagmaInstaller();

        BSLPreInit.setupStartup(launchArgs);
        BSLPreInit.addExports("cpw.mods.securejarhandler", "cpw.mods.niofs.union", "ALL-UNNAMED");

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
            Class<?> bsl = Class.forName("cpw.mods.bootstraplauncher.BootstrapLauncher");
            Method main = bsl.getDeclaredMethod("main", String[].class);
            String[] invokeArgs = Stream.concat(forgeArgs.stream(), mainArgs.stream()).toArray(String[]::new);
            main.invoke(null, (Object) invokeArgs);
            //MethodHandle main = Unsafe.lookup().findStatic(bsl, "main", methodType(void.class, String[].class));
            //main.invokeExact(((String[]) Stream.concat(forgeArgs.stream(), mainArgs.stream()).toArray(String[]::new)));
        } catch (Throwable e) {
            System.out.println("[MAGMA] If you freshly installed Magma, you just need to restart the server.");
            System.err.println("[MAGMA] If not report that error to us: "+e);
            System.exit(1);
        }

        ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("org.jline").forEach(classInfo -> {
            try {
                Thread.currentThread().getContextClassLoader().loadClass(classInfo.getName());
                System.out.println("[MAGMA] Loaded class: " + classInfo.getName());
            } catch (ClassNotFoundException e) {
                System.out.println("[MAGMA] Failed to load class: " + classInfo.getName());
            }
        });

        //Thread.currentThread().setContextClassLoader(Thread.currentThread().getContextClassLoader());

    }

}
