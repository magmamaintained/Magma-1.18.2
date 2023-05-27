package org.magmafoundation.magma.installer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Inspired by MohistMC (https://github.com/MohistMC)
 */
public class NetworkUtils {

    private static final ExecutorService downloadSrvc = java.util.concurrent.Executors.newFixedThreadPool(4);

    public static URLConnection getConnection(String URL) {
        URLConnection conn = null;
        try {
            conn = new URL(URL).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");

            int timeout = (int) TimeUnit.SECONDS.toMillis(20);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void downloadFile(String URL, File f) throws Exception {
        downloadFile(URL, f, null);
    }

    public static void downloadFile(String URL, File f, String md5) throws Exception {
        URLConnection conn = getConnection(URL);
        ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
        FileChannel fc = FileChannel.open(f.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        int fS = conn.getContentLength();
        CompletableFuture.supplyAsync(() -> {
            try {
                fc.transferFrom(rbc, 0, fS);
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }, downloadSrvc).get(20, TimeUnit.SECONDS);
        rbc.close();
        String MD5 = org.magmafoundation.magma.common.utils.MD5.getMd5(f);
        if(md5 != null && MD5 != null && !MD5.equals(md5.toLowerCase())) {
            f.delete();
            throw new Exception("md5");
        }
    }

}
