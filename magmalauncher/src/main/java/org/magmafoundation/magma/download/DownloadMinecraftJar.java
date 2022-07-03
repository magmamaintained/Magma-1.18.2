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

package org.magmafoundation.magma.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Made with help of Shawizz
 */
public class DownloadMinecraftJar {

    public static void run(File minecraft_server) throws IOException {
        if (Files.exists(minecraft_server.toPath()))
            return;
        minecraft_server.getParentFile().mkdirs();
		try {
            UpdateUtils.downloadFile("https://launcher.mojang.com/v1/objects/c8f83c5655308435b3dcf03c06d9fe8740a77469/server.jar", minecraft_server);
        } catch (Exception e) {
            System.out.println("Can't download minecraft_server");
            e.printStackTrace();
        }
    }

}
