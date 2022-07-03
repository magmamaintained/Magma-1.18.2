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

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Made with help of Hexeption
 */
public class JarUtils {

	public static String getJarPath() {
		File file = getFile();
		if (file == null) {
			return null;
		}
		return file.getAbsolutePath();
	}

	public static String getJarDir() {
		File file = getFile();
		if (file == null) {
			return null;
		}
		return getFile().getParent();
	}

	public static String getJarName() {
		File file = getFile();
		if (file == null) {
			return null;
		}
		return getFile().getName();
	}

	public static File getFile() {

		String path = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		try {
			path = java.net.URLDecoder.decode(path, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return new File(path);
	}
}
