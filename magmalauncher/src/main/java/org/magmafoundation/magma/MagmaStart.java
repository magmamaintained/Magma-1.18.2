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

import org.magmafoundation.magma.action.v_1_18_2.v_1_18_2;
import org.magmafoundation.magma.betterui.BetterUI;
import org.magmafoundation.magma.libraries.DefaultLibraries;
import org.magmafoundation.magma.updater.MagmaUpdater;
import org.magmafoundation.magma.utils.DataParser;
import org.magmafoundation.magma.utils.MagmaModuleManager;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

        DataParser.parseVersions();
        DataParser.parseLaunchArgs();
        DataParser.parseLibrariesClassPath();

        BetterUI.printTitle(NAME, BRAND, System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")", VERSION, BUKKIT_VERSION, FORGE_VERSION);

        if(Float.parseFloat(System.getProperty("java.class.version")) < 61){
            System.out.println("Your Java version is not supported. Please update to Java 17 or higher.");
            System.exit(1);
        }

        DefaultLibraries.downloadRepoLibs();
        new v_1_18_2().run();

        new MagmaModuleManager(DataParser.launchArgs);

        if(mainArgs.contains("-noserver"))
            System.exit(0); //-noserver -> Do not run the Minecraft server, only let the installation running.


        if (!BetterUI.checkEula(Paths.get("eula.txt")))
            System.exit(0);

        Path path = Paths.get("magma.yml");
        if (Files.exists(path)) {
            try (InputStream stream = Files.newInputStream(path)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(stream);
                Map<String, Object> forge = (Map<String, Object>) data.get("magma");
                MagmaUpdater updater = new MagmaUpdater();
                if (Arrays.stream(args).noneMatch(s -> s.equalsIgnoreCase("-dau"))) {
                    System.out.println("Checking for updates...");
                    if (updater.versionChecker() && forge.get("auto-update").equals(true))
                        updater.downloadJar();
                }
            }
        }

        List<String> forgeArgs = new ArrayList<>();
        for(String arg : DataParser.launchArgs.stream()
                .filter(s -> s.startsWith("--launchTarget") || s.startsWith("--fml.forgeVersion") || s.startsWith("--fml.mcVersion") || s.startsWith("--fml.forgeGroup") || s.startsWith("--fml.mcpVersion")).collect(Collectors.toList())) {
            forgeArgs.add(arg.split(" ")[0]);
            forgeArgs.add(arg.split(" ")[1]);
        }

        String[] args_ = Stream.concat(forgeArgs.stream(), Arrays.stream(args)).toArray(String[]::new);

        var cl = Class.forName("cpw.mods.bootstraplauncher.BootstrapLauncher");
        var method = cl.getMethod("main", String[].class);
        method.invoke(null, (Object) args_);
    }


}
