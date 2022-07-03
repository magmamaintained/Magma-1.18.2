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

import org.magmafoundation.magma.utils.MD5Util;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.magmafoundation.magma.download.NetworkUtil.getConn;

public class UpdateUtils {


	public static void downloadFile(String URL, File f) throws Exception {
		downloadFile(URL, f, null);
	}

	public static void downloadFile(String URL, File f, String md5) throws Exception {
		URLConnection conn = getConn(URL);
		ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
		FileChannel fc = FileChannel.open(f.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		int fS = conn.getContentLength();
		fc.transferFrom(rbc, 0, Long.MAX_VALUE);
		fc.close();
		rbc.close();
		String MD5 = MD5Util.getMd5(f);
		if(md5 != null && MD5 != null && !MD5.equals(md5.toLowerCase())) {
			f.delete();
			throw new Exception("md5");
		}
	}

	public static String getSize(long size) {
		return (size >= 1048576L) ? (float) size / 1048576.0F + "MB" : ((size >= 1024) ? (float) size / 1024.0F + " KB" : size + " B");
	}

	public static long getSizeOfDirectory(File path) throws IOException {
		return Files.walk(path.toPath()).parallel()
				.map(Path::toFile)
				.filter(File::isFile)
				.mapToLong(File::length)
				.sum();
	}
}
