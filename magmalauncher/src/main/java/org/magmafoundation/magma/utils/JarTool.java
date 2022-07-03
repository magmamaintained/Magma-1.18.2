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

import org.magmafoundation.magma.MagmaStart;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm) / Hexeption / Mgazul
 * @date 03.07.2022 - 17:19
 */
public class JarTool {

    public static String getJarPath() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public static File getJarDir() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getParentFile();
    }

    public static String getJarName() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getName();
    }

    public static File getFile() {

        String path = JarTool.class.getProtectionDomain().getCodeSource()
                .getLocation().getFile();
		path = java.net.URLDecoder.decode(path, StandardCharsets.UTF_8);
		return new File(path);
    }

    public static void inputStreamFile(InputStream inputStream, String targetFilePath) {
        File file = new File(targetFilePath);
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFileLinesFromJar(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(MagmaStart.class.getClassLoader().getResourceAsStream(path))))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null)
                lines.add(line);
            return lines;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
