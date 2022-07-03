/*
 * MohistMC
 * Copyright (C) 2018-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.libraries;

import org.magmafoundation.magma.download.UpdateUtils;
import org.magmafoundation.magma.utils.JarLoader;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.MD5Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

public class DefaultLibraries {

	public static void downloadRepoLibs() throws Exception {
		HashMap<File, List<String>> dependencies = new HashMap<>();
		dependencies.put(new File("libraries/dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar"), new ArrayList<>(Arrays.asList("e6b19d4a5e3687432530a0aa9edf6fcc", "https://repo1.maven.org/maven2/dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar")));
		dependencies.put(new File("libraries/dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar"), new ArrayList<>(Arrays.asList("e8cee80f1719c02ef3076ff42bab0ad9", "https://s01.oss.sonatype.org/content/repositories/snapshots/dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar")));

		for (File lib : dependencies.keySet()) {
			if(lib.exists() && Objects.equals(MD5Util.getMd5(lib), dependencies.get(lib).get(0))) {
				System.out.println("Loading library " + lib.getName());
				new JarLoader().loadJar(lib);
				continue;
			}
			System.out.println("Downloading " + lib.getName() + "...");
			lib.getParentFile().mkdirs();
			try {
				UpdateUtils.downloadFile(dependencies.get(lib).get(1), lib, dependencies.get(lib).get(0));
				new JarLoader().loadJar(lib);
				System.out.println("Downloaded and loaded " + lib.getName() + ". md5: " + MD5Util.getMd5(lib));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static LinkedHashMap<File, String> getDefaultLibs() throws Exception {
		LinkedHashMap<File, String> temp = new LinkedHashMap<>();
		BufferedReader b = new BufferedReader(new InputStreamReader(Objects.requireNonNull(DefaultLibraries.class.getClassLoader().getResourceAsStream("data/magma-libraries.txt"))));
		String str;
		while ((str = b.readLine()) != null) {
			String[] s = str.split("\\|");
			temp.put(new File(JarTool.getJarDir(), s[0]), s[1]);
		}
		b.close();
		return temp;
	}
}
