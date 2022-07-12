package org.magmafoundation.magma.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.magmafoundation.magma.common.MagmaConstants;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 */
public class MagmaUpdater {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String newSha;
    private String currentSha;

    private String latestVersionURL = "https://api.magmafoundation.org/api/v2/1.18/latest/";

    public boolean versionChecker() {
        try {
            URL url = new URL(latestVersionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            Date created_at = Date.from(Instant.parse(root.get("created_at").getAsString()));
            String date = new SimpleDateFormat("dd-MM-yyyy").format(created_at);
            String time = new SimpleDateFormat("H:mm a").format(created_at);

            newSha = root.get("tag_name").getAsString();
            currentSha = MagmaConstants.VERSION;

            if(currentSha.equals(newSha)) {
                System.out.printf("[Magma] No update found, latest version: (%s) current version: (%s)%n", currentSha, newSha);
                return false;
            } else {
                System.out.printf("[Magma] The latest Magma version is (%s) but you have (%s). The latest version was built on %s at %s.%n", newSha, currentSha, date, time);
                return true;
            }
        } catch (IOException e) {
            System.out.println("[Magma] Failed to check for updates.");
            return false;
        }
    }

    public void downloadJar() {
        String url = latestVersionURL + newSha + "/download";
        try {
            Path path = Paths.get(MagmaUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            System.out.println("[Magma] Updating Magma Jar ...");
            System.out.println("[Magma] Downloading " + url + " ...");
            URL website = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path.toFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException | URISyntaxException e) {
            System.out.println("[Magma] Failed to download update! Starting old version.");
            return;
        }
        System.out.println("[Magma] Download Complete! Please restart the server.");
    }

    public static void checkForUpdates() throws IOException {
        Path path = Paths.get("magma.yml");
        if(Files.exists(path)) {
            try (InputStream stream = Files.newInputStream(path)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(stream);
                Map<String, Object> forge = (Map<String, Object>) data.get("magma");
                MagmaUpdater updater = new MagmaUpdater();
                System.out.println("Checking for updates...");
                if(updater.versionChecker() && forge.get("auto-update").equals(true))
                    updater.downloadJar();
            }
        }
    }
}
