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

package org.magmafoundation.magma.utils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm) / Hexeption
 * @date 03.07.2022 - 17:19
 */
public class JarLoader {

    private static Instrumentation inst = null;

    public JarLoader() {
    }

    public static void agentmain(final String a, final Instrumentation inst) {
        JarLoader.inst = inst;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        JarLoader.inst = inst;
    }

    public static void loadJar(File file) {
        loadJar(file.toPath());
    }

    public static void loadJar(Path path) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if(!(cl instanceof URLClassLoader)) {
                if(inst == null) {
                    System.exit(1);
                }
                inst.appendToSystemClassLoaderSearch(new JarFile(path.toFile()));
            } else {
                Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                m.setAccessible(true);
                m.invoke((URLClassLoader) cl, path.toUri().toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
