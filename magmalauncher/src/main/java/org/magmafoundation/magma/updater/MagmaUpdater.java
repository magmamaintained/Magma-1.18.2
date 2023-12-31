package org.magmafoundation.magma.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.magmafoundation.magma.MagmaStart;
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

    private String versionURL = "https://api.github.com/repos/magmamaintained/Magma-1.18.2/releases/latest";
    private String assetURL;

    public boolean versionChecker() {
        try {
            URL url = new URL(versionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            newSha = root.get("tag_name").getAsString();
            assetURL = root.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
            currentSha = MagmaConstants.VERSION.split("-")[1];

            if(currentSha.equals(newSha)) {
                System.out.printf("[Magma] No update found, latest version: (%s) current version: (%s)%n", currentSha, newSha);
                return false;
            } else {
                System.out.printf("[Magma] The latest Magma version is (%s) but you have (%s).%n", newSha, currentSha);
                return true;
            }
        } catch (IOException e) {
            System.out.println("[Magma] Failed to check for updates.");
            return false;
        }
    }

    public void downloadJar() {
        try {
            Path path = Paths.get(MagmaUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            System.out.println("[Magma] Updating Magma Jar ...");
            System.out.println("[Magma] Downloading " + assetURL + " ...");
            URL website = new URL(assetURL);
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
        System.exit(0);
    }

    public static void checkForUpdates() throws IOException {
        Path path = Paths.get("magma.yml");
        if(Files.exists(path)) {
            try (InputStream stream = Files.newInputStream(path)) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(stream);
                Map<String, Object> forge = (Map<String, Object>) data.get("magma");
                if (!forge.get("auto-update").equals(true) || MagmaConstants.VERSION.equals("dev-env"))
                    return;

                MagmaUpdater updater = new MagmaUpdater();
                System.out.println("Checking for updates...");
                if(updater.versionChecker())
                    updater.downloadJar();
            }
        }
    }
}
