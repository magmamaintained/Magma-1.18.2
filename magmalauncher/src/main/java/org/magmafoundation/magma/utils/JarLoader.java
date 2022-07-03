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
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class JarLoader {


    /**
     * Parameters of the method to add an URL to the System classes.
     */
    private static final Class<?>[] parameters = new Class[]{URL.class};


    private static Instrumentation inst = null;

    public JarLoader() {
    }

    public static void agentmain(final String a, final Instrumentation inst) {
        JarLoader.inst = inst;
    }


    public static void premain(String agentArgs, Instrumentation inst) {
        JarLoader.inst = inst;
    }

    /**
     * Adds a file to the classpath.
     *
     * @param s a String pointing to the file
     */
    public static void loadJar(String s) throws IOException {
        File f = new File(s);
        loadJar(f);
    }

    /**
     * Adds a file to the classpath
     *
     * @param f the file to be added
     */
    public static void loadJar(File f) throws IOException {
        addURL(f);
    }

    /**
     * Adds the content pointed by the URL to the classpath.
     *
     * @param path the path pointing to the file
     */
    public static void addURL(File path) throws IOException {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (!path.getName().equals( "minecraft_server.1.16.5.jar" )) {
            try {
                if (!(cl instanceof URLClassLoader)) {
                    if (inst == null) {
                        System.exit( 1 );
                    }
                    inst.appendToSystemClassLoaderSearch( new JarFile( path ) );
                } else {
                    Method m = URLClassLoader.class.getDeclaredMethod( "addURL", parameters);
                    m.setAccessible( true );
                    m.invoke( (URLClassLoader) cl, path.toURI().toURL() );
                }
            } catch (Throwable t) {
                t.printStackTrace();
                throw new IOException( "Error, could not add URL to system classloader" );
            }
        }
    }
}
