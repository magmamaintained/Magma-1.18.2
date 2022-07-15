package org.magmafoundation.magma.installer;

import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.dependency.Dependency;
import dev.vankka.dependencydownload.path.CleanupPathProvider;
import dev.vankka.dependencydownload.path.DependencyPathProvider;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.magmafoundation.magma.common.MagmaConstants;
import org.magmafoundation.magma.common.utils.JarTool;
import org.magmafoundation.magma.common.utils.MD5;
import org.magmafoundation.magma.utils.LibHelper;
import org.magmafoundation.magma.utils.ServerInitHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        } catch (Exception ignored) {}
    }

    //Inspired by the Mohist 1.19 installer
    private void install() throws Exception {
        if (installInfo.exists()) {
            String magmaMD5 = MD5.getMd5(JarTool.getFile());
            List<String> lines = Files.readAllLines(installInfo.toPath());
            String storedMagmaMD5 = lines.get(1);
            if (magmaMD5.equals(storedMagmaMD5)) //Latest patch is installed
                return;
            else purge();
        }

        ProgressBarBuilder builder = new ProgressBarBuilder()
                .setTaskName("Patching server...")
                .setStyle(ProgressBarStyle.ASCII)
                .setUpdateIntervalMillis(100)
                .setInitialMax(8);

        try (ProgressBar pb = builder.build()) {
            copyFileFromJar(lzma, "data/server.lzma");
            copyFileFromJar(universalJar, "data/forge-" + mcVer + "-" + forgeVer + "-universal.jar");
            copyFileFromJar(fmlloader, "data/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(fmlcore, "data/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(javafmllanguage, "data/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(mclanguage, "data/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(lowcodelanguage, "data/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

            if (magmaVersion == null || mcpVer == null) {
                System.out.println("The server has an invalid version and cannot proceed. Please report this to the developer.");
                System.exit(-1);
            }

            if (minecraft_server.exists()) {
                mute();
                System.out.println("Extracting bundled resources...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", libPath, "--libraries")),
                        stringToUrl(loadedLibsPaths));
                unmute();
                pb.step();
                if (!mc_unpacked.exists()) {
                    mute();
                    System.out.println("Extracting jars...");
                    launchService("net.minecraftforge.installertools.ConsoleTool",
                            new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", mc_unpacked.getAbsolutePath(), "--jar-only")),
                            stringToUrl(loadedLibsPaths));
                    unmute();
                }
                pb.step();
            } else {
                System.err.println("The server is missing essential files to install properly, delete your libraries folder and try again.");
                System.exit(-1);
            }

            if (mcpZip.exists()) {
                if (!mcpTxt.exists()) {
                    mute();
                    System.out.println("Getting mappings...");
                    launchService("net.minecraftforge.installertools.ConsoleTool",
                            new ArrayList<>(Arrays.asList("--task", "MCP_DATA", "--input", mcpZip.getAbsolutePath(), "--output", mcpTxt.getAbsolutePath(), "--key", "mappings")),
                            stringToUrl(loadedLibsPaths));
                    unmute();
                }
            } else {
                System.err.println("The server is missing essential files to install properly, delete your libraries folder and try again.");
                System.exit(-1);
            }
            pb.step();

            if (isCorrupted(extra)) extra.delete();
            if (isCorrupted(slim)) slim.delete();
            if (isCorrupted(srg)) srg.delete();

            if (!mojmap.exists()) {
                mute();
                System.out.println("Downloading mojang mappings...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "DOWNLOAD_MOJMAPS", "--version", mcVer, "--side", "server", "--output", mojmap.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
            pb.step();

            if (!mergedMapping.exists()) {
                mute();
                System.out.println("Merging mappings...");
                launchService("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "MERGE_MAPPING", "--left", mcpTxt.getAbsolutePath(), "--right", mojmap.getAbsolutePath(), "--output", mergedMapping.getAbsolutePath(), "--classes", "--reverse-right")),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
            pb.step();

            if (!slim.exists() || !extra.exists()) {
                mute();
                System.out.println("Splitting server jar...");
                launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", minecraft_server.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                launchService("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", mc_unpacked.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
            pb.step();

            if (!srg.exists()) {
                mute();
                System.out.println("Creating srg jar file...");
                launchService("net.minecraftforge.fart.Main",
                        new ArrayList<>(Arrays.asList("--input", slim.getAbsolutePath(), "--output", srg.getAbsolutePath(), "--names", mergedMapping.getAbsolutePath(), "--ann-fix", "--ids-fix", "--src-fix", "--record-fix")),
                        stringToUrl(loadedLibsPaths));
                unmute();
            }
            pb.step();

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
                mute();
                System.out.println("Patching forge jar...");
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
            pb.step();

            FileWriter fw = new FileWriter(installInfo);
            fw.write(serverMD5 + "\n");
            fw.write(magmaMD5);
            fw.close();
        }
        System.out.println("The server has been patched successfully, launch the server again to play.");
        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }

    private void purge() {
        System.out.println("WARNING: It appears you have installed a new version of magma, please remove the libraries folder before continuing. The server will now shut down.");
        System.exit(0);
    }


    private void downloadInternalLibraries() {
        HashMap<File, List<String>> dependencies = new HashMap<>();
        dependencies.put(new File(libPath + "dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar"), Arrays.asList("e6b19d4a5e3687432530a0aa9edf6fcc", "https://repo1.maven.org/maven2/dev/vankka/dependencydownload-common/1.2.1/dependencydownload-common-1.2.1.jar"));
        dependencies.put(new File(libPath + "dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar"), Arrays.asList("e8cee80f1719c02ef3076ff42bab0ad9", "https://s01.oss.sonatype.org/content/repositories/snapshots/dev/vankka/dependencydownload-runtime/1.2.2-SNAPSHOT/dependencydownload-runtime-1.2.2-20220425.122523-9.jar"));
        dependencies.put(new File(libPath + "org/jline/jline/3.21.0/jline-3.21.0.jar"), Arrays.asList("859778f9cdd3bd42bbaaf0f6f7fe5e6a", "https://repo1.maven.org/maven2/org/jline/jline/3.21.0/jline-3.21.0.jar"));
        dependencies.put(new File(libPath + "me/tongfei/progressbar/0.9.3/progressbar-0.9.3.jar"), Arrays.asList("25d3101d2ca7f0847a804208d5411d78", "https://repo1.maven.org/maven2/me/tongfei/progressbar/0.9.3/progressbar-0.9.3.jar"));

        for (File lib : dependencies.keySet()) {
            if(lib.exists() && Objects.equals(MD5.getMd5(lib), dependencies.get(lib).get(0))) {
                ServerInitHelper.addToPath(lib.toPath());
                continue;
            }
            lib.getParentFile().mkdirs();
            try {
                NetworkUtils.downloadFile(dependencies.get(lib).get(1), lib, dependencies.get(lib).get(0));
                ServerInitHelper.addToPath(lib.toPath());
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

            List<Dependency> dependencies = manager.getDependencies();

            ProgressBarBuilder builder = new ProgressBarBuilder()
                    .setTaskName("Loading libraries...")
                    .setStyle(ProgressBarStyle.ASCII)
                    .setUpdateIntervalMillis(100)
                    .setInitialMax(dependencies.size());

            //AtomicReference<Throwable> error = new AtomicReference<>(null);
            ProgressBar.wrap(dependencies.stream(), builder).forEach(dep -> {
                try {
                    LibHelper.downloadDependency(manager, dep, standardRepositories);
                    LibHelper.loadDependency(manager, dep, path -> loadedLibsPaths.add(path.toFile().getAbsolutePath()));
                } catch (IOException | NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            });

            downloadMcp(mcVersion, mcpVersion);
            downloadMinecraftServer(minecraft_server);
        }

        public void downloadMcp(String mc_version, String mcp_version) {
            File mcp_config = new File(libPath + "de/oceanlabs/mcp/mcp_config/" + mc_version + "-" + mcp_version + "/mcp_config-" + mc_version + "-" + mcp_version + ".zip");
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
