package org.magmafoundation.magma.installer;

import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.dependency.Dependency;
import dev.vankka.dependencydownload.path.CleanupPathProvider;
import dev.vankka.dependencydownload.path.DependencyPathProvider;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;
import org.magmafoundation.magma.MagmaConstants;
import org.magmafoundation.magma.utils.JarLoader;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.MD5;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Inspired by Shawiiz_z (https://github.com/Shawiizz)
 */
public class MagmaInstaller extends AbstractMagmaInstaller {

    private static final List<String> loadedLibsPaths = new ArrayList<>();

    private final String magmaVersion = MagmaConstants.VERSION;

    public final File fmlloader = new File(libPath + "net/minecraftforge/fmlloader/" + mcVer + "-" + forgeVer + "/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
    public final File fmlcore = new File(libPath + "net/minecraftforge/fmlcore/" + mcVer + "-" + forgeVer + "/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
    public final File javafmllanguage = new File(libPath + "net/minecraftforge/javafmllanguage/" + mcVer + "-" + forgeVer + "/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
    public final File mclanguage = new File(libPath + "net/minecraftforge/mclanguage/" + mcVer + "-" + forgeVer + "/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
    public final File lowcodelanguage = new File(libPath + "net/minecraftforge/lowcodelanguage/" + mcVer + "-" + forgeVer + "/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

    public final File mojmap = new File(otherStart + "-mappings.txt");
    public final File mc_unpacked = new File(otherStart + "-unpacked.jar");

    public final File mergedMapping = new File(mcpStart + "-mappings-merged.txt");

    public MagmaInstaller() {
        try {
            downloadInternalLibraries();
            new Dependencies(mcVer, mcpVer, minecraft_server);
            install();
        } catch (Exception ignored){}
    }

    //Inspired by Mohist 1.19 installer
    private void install() throws Exception {
        System.out.println("Checking the installation, please wait.");
        copyFileFromJar(lzma, "data/server.lzma");
        copyFileFromJar(universalJar, "data/forge-" + mcVer + "-" + forgeVer + "-universal.jar");
        copyFileFromJar(fmlloader, "data/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
        copyFileFromJar(fmlcore, "data/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
        copyFileFromJar(javafmllanguage, "data/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
        copyFileFromJar(mclanguage, "data/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
        copyFileFromJar(lowcodelanguage, "data/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

        if (magmaVersion == null || mcpVer == null) {
            System.out.println("Installer was interrupted because of a missing version. Try to run it again.");
            System.exit(1);
        }

        if (minecraft_server.exists()) {
            mute();
            launchService("net.minecraftforge.installertools.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", libPath, "--libraries")),
                    stringToUrl(loadedLibsPaths));
            unmute();
            if (!mc_unpacked.exists()) {
                System.out.println("Extract bundler");
                mute();
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", mc_unpacked.getAbsolutePath(), "--jar-only")),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
        } else {
            System.out.println("Can't run the installation because the minecraft server file doesn't exists !");
        }

        if (mcpZip.exists()) {
            if (!mcpTxt.exists()) {

                // MAKE THE MAPPINGS TXT FILE

                System.out.println("Creating mappings txt file");
                mute();
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "MCP_DATA", "--input", mcpZip.getAbsolutePath(), "--output", mcpTxt.getAbsolutePath(), "--key", "mappings")),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
        } else {
            System.out.println("Can't run the installation because the mcp config file doesn't exists !");
            System.exit(0);
        }

        if (isCorrupted(extra)) extra.delete();
        if (isCorrupted(slim)) slim.delete();
        if (isCorrupted(srg)) srg.delete();

        if (!mojmap.exists()) {
            System.out.println("Download mojang mapping");
            mute();
            launchService("net.minecraftforge.installertools.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--task", "DOWNLOAD_MOJMAPS", "--version", mcVer, "--side", "server", "--output", mojmap.getAbsolutePath())),
                    stringToUrl(loadedLibsPaths));
            unmute();
        }

        if (!mergedMapping.exists()) {
            System.out.println("Create merged mapping");
            mute();
            launchService("net.minecraftforge.installertools.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--task", "MERGE_MAPPING", "--left", mcpTxt.getAbsolutePath(), "--right", mojmap.getAbsolutePath(), "--output", mergedMapping.getAbsolutePath(), "--classes", "--reverse-right")),
                    stringToUrl(loadedLibsPaths));
            unmute();
        }

        if (!slim.exists() || !extra.exists()) {
            System.out.println("Creating extra and slim jar files. This can take 1 minute.");
            mute();
            launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--input", minecraft_server.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                    stringToUrl(loadedLibsPaths));
            launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--input", mc_unpacked.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                    stringToUrl(loadedLibsPaths));
            unmute();
        }

        if (!srg.exists()) {
            System.out.println("Creating srg jar file. This can take 1 minute.");
            mute();
            launchService("net.minecraftforge.fart.Main",
                    new ArrayList<>(Arrays.asList("--input", slim.getAbsolutePath(), "--output", srg.getAbsolutePath(), "--names", mergedMapping.getAbsolutePath(), "--ann-fix", "--ids-fix", "--src-fix", "--record-fix")),
                    stringToUrl(loadedLibsPaths));
            unmute();
        }

        String storedServerMD5 = null;
        String storedMagmaMD5 = null;
        String serverMD5 = MD5.getMd5(serverJar);
        String magmaMD5 = MD5.getMd5(JarTool.getFile());

        if (installInfo.exists()) {
            List<String> infoLines = Files.readAllLines(installInfo.toPath());
            if (infoLines.size() > 0)
                storedServerMD5 = infoLines.get(0);
            if (infoLines.size() > 1)
                storedMagmaMD5 = infoLines.get(1);
        }

        if (!serverJar.exists()
                || storedServerMD5 == null
                || storedMagmaMD5 == null
                || !storedServerMD5.equals(serverMD5)
                || !storedMagmaMD5.equals(magmaMD5)) {
            System.out.println("Creating forge server jar. This can take 1 minute.");
            mute();
            launchService("net.minecraftforge.binarypatcher.ConsoleTool",
                    new ArrayList<>(Arrays.asList("--clean", srg.getAbsolutePath(), "--output", serverJar.getAbsolutePath(), "--apply", lzma.getAbsolutePath())),
                    stringToUrl(new ArrayList<>(Arrays.asList(
                            libPath + "net/minecraftforge/binarypatcher/1.0.12/binarypatcher-1.0.12.jar",
                            libPath + "commons-io/commons-io/2.4/commons-io-2.4.jar",
                            libPath + "com/google/guava/guava/25.1-jre/guava-25.1-jre.jar",
                            libPath + "net/sf/jopt-simple/jopt-simple/5.0.4/jopt-simple-5.0.4.jar",
                            libPath + "com/github/jponge/lzma-java/1.3/lzma-java-1.3.jar",
                            libPath + "com/nothome/javaxdelta/2.0.1/javaxdelta-2.0.1.jar",
                            libPath + "com/google/code/findbugs/jsr305/3.0.2/jsr305-3.0.2.jar",
                            libPath + "org/checkerframework/checker-qual/2.0.0/checker-qual-2.0.0.jar",
                            libPath + "com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar",
                            libPath + "com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar",
                            libPath + "org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar",
                            libPath + "trove/trove/1.0.2/trove-1.0.2.jar"
                    ))));
            unmute();
            serverMD5 = MD5.getMd5(serverJar);
        }

        FileWriter fw = new FileWriter(installInfo);
        fw.write(serverMD5 + "\n");
        fw.write(magmaMD5);
        fw.close();

        System.out.println("Finished the install verification process !");
    }


    public static void downloadInternalLibraries() throws Exception {
        HashMap<File, List<String>> dependencies = new HashMap<>();
        dependencies.put(new File("libraries/dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar"), new ArrayList<>(Arrays.asList("e6b19d4a5e3687432530a0aa9edf6fcc", "https://repo1.maven.org/maven2/dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar")));
        dependencies.put(new File("libraries/dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar"), new ArrayList<>(Arrays.asList("e8cee80f1719c02ef3076ff42bab0ad9", "https://s01.oss.sonatype.org/content/repositories/snapshots/dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar")));

        for (File lib : dependencies.keySet()) {
            if(lib.exists() && Objects.equals(MD5.getMd5(lib), dependencies.get(lib).get(0))) {
                //System.out.println("Loading library " + lib.getName());
                new JarLoader().loadJar(lib);
                continue;
            }
            //System.out.println("Downloading " + lib.getName() + "...");
            lib.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile(dependencies.get(lib).get(1), lib, dependencies.get(lib).get(0));
                new JarLoader().loadJar(lib);
                //System.out.println("Downloaded and loaded " + lib.getName() + ". md5: " + MD5.getMd5(lib));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class Dependencies {

        private String mcVersion;
        private String mcpVersion;
        private File minecraft_server;

        public Dependencies(String mcVersion, String mcpVersion, File minecraft_server) throws IOException {
            this.mcVersion = mcVersion;
            this.mcpVersion = mcpVersion;
            this.minecraft_server = minecraft_server;

            downloadLibraries();
        }

        public void downloadLibraries() throws IOException {
            DependencyPathProvider dependencyPathProvider = new CleanupPathProvider() {

                public final Path baseDirPath = JarTool.getJarDir().toPath();

                @Override
                public Path getCleanupPath() {
                    return baseDirPath;
                }

                @Override
                public Path getDependencyPath(Dependency dependency, boolean relocated) {
                    return baseDirPath.resolve("libraries")
                            .resolve(dependency.getGroupId().replace(".", "/"))
                            .resolve(dependency.getArtifactId())
                            .resolve(dependency.getVersion())
                            .resolve(dependency.getFileName());
                }
            };

            DependencyManager manager = new DependencyManager(dependencyPathProvider);
            manager.loadFromResource(new URL("jar:file:" + JarTool.getJarPath() + "!/data/magma_libraries.txt"));

            Executor executor = Executors.newCachedThreadPool();
            List<Repository> standardRepositories = new ArrayList<>();
            standardRepositories.add(new StandardRepository("https://maven.minecraftforge.net/"));
            standardRepositories.add(new StandardRepository("https://repo1.maven.org/maven2/"));
            standardRepositories.add(new StandardRepository("https://raw.github.com/Hexeption/Magma-Repo/master/"));

            manager.downloadAll(executor, standardRepositories).join();
            manager.loadAll(executor, path -> {
                try {
                    new JarLoader().loadJar(path.toFile());
                    loadedLibsPaths.add(path.toFile().getAbsolutePath());
                    //System.out.println("Loaded " + path.toFile().getName() + ".");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).join();

            downloadMcp(mcVersion, mcpVersion);
            downloadMinecraftServer(minecraft_server);
        }

        public void downloadMcp(String mc_version, String mcp_version) {
            File mcp_config = new File("libraries/de/oceanlabs/mcp/mcp_config/" + mc_version + "-" + mcp_version + "/mcp_config-" + mc_version + "-" + mcp_version + ".zip");
            if (Files.exists(mcp_config.toPath()))
                return;
            mcp_config.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile("https://maven.minecraftforge.net/de/oceanlabs/mcp/mcp_config/"+ mc_version + "-"+ mcp_version + "/mcp_config-"+ mc_version + "-"+ mcp_version +".zip",
                        mcp_config);
            } catch (Exception e) {
                System.out.println("Can't find mcp_config");
                e.printStackTrace();
            }
        }

        public void downloadMinecraftServer(File minecraft_server) throws IOException {
            if (Files.exists(minecraft_server.toPath()))
                return;
            minecraft_server.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile("https://launcher.mojang.com/v1/objects/c8f83c5655308435b3dcf03c06d9fe8740a77469/server.jar", minecraft_server);
            } catch (Exception e) {
                System.out.println("Can't download minecraft_server");
                e.printStackTrace();
            }
        }
    }
}
