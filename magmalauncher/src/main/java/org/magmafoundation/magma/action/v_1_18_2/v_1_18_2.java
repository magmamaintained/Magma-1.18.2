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

package org.magmafoundation.magma.action.v_1_18_2;

import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.dependency.Dependency;
import dev.vankka.dependencydownload.path.CleanupPathProvider;
import dev.vankka.dependencydownload.path.DependencyPathProvider;
import dev.vankka.dependencydownload.repository.Repository;
import dev.vankka.dependencydownload.repository.StandardRepository;
import org.magmafoundation.magma.action.Action;
import org.magmafoundation.magma.action.Version;
import org.magmafoundation.magma.download.DownloadMcpConfig;
import org.magmafoundation.magma.download.DownloadMinecraftJar;
import org.magmafoundation.magma.utils.JarLoader;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.JarUtils;
import org.magmafoundation.magma.utils.MD5Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class v_1_18_2 implements Version {

	private static List<String> loadedLibsPaths = new ArrayList<>();

    @Override
    public void run() {
        try {
            new DownloadLibraries();
            new Install_1_18_2();
        } catch (Exception ignored) {}
    }

    static class DownloadLibraries extends Action {

        protected DownloadLibraries() throws IOException {
            super();
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
            manager.loadFromResource(new URL("jar:file:" + JarUtils.getJarPath() + "!/data/magma_libraries.txt"));

            Executor executor = Executors.newCachedThreadPool();
            List<Repository> standardRepositories = new ArrayList<>();
            standardRepositories.add(new StandardRepository("https://maven.minecraftforge.net/"));
            standardRepositories.add(new StandardRepository("https://repo.maven.apache.org/maven2/"));
            standardRepositories.add(new StandardRepository("https://repo1.maven.org/maven2/"));
            standardRepositories.add(new StandardRepository("https://maven.mohistmc.com/libraries/"));
            standardRepositories.add(new StandardRepository("https://raw.github.com/Hexeption/Magma-Repo/master/"));

            manager.downloadAll(executor, standardRepositories).join();
			manager.loadAll(executor, path -> {
				try {
					new JarLoader().loadJar(path.toFile());
					loadedLibsPaths.add(path.toFile().getAbsolutePath());
					System.out.println("Loaded " + path.toFile().getName() + ".");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}).join();

			DownloadMcpConfig.run(mcVer, mcpVer);
            DownloadMinecraftJar.run(minecraft_server);
        }
    }

    static class Install_1_18_2 extends Action {

        public File fmlloader;
        public File fmlcore;
        public File javafmllanguage;
        public File mclanguage;
        public File lowcodelanguage;

        public File mojmap;
        public File mc_unpacked;

        public File mergedMapping;

        public File win_args;
        public File unix_args;
        public File jvm_args;

        protected Install_1_18_2() throws Exception {
            super();
            this.fmlloader = new File(libPath + "net/minecraftforge/fmlloader/" + mcVer + "-" + forgeVer + "/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
            this.fmlcore = new File(libPath + "net/minecraftforge/fmlcore/" + mcVer + "-" + forgeVer + "/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
            this.javafmllanguage = new File(libPath + "net/minecraftforge/javafmllanguage/" + mcVer + "-" + forgeVer + "/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
            this.mclanguage = new File(libPath + "net/minecraftforge/mclanguage/" + mcVer + "-" + forgeVer + "/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
            this.lowcodelanguage = new File(libPath + "net/minecraftforge/lowcodelanguage/" + mcVer + "-" + forgeVer + "/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");
            this.mojmap = new File(otherStart + "-mappings.txt");
            this.mc_unpacked = new File(otherStart + "-unpacked.jar");
            this.mergedMapping = new File(mcpStart + "-mappings-merged.txt");

            this.win_args = new File(libPath + "net/minecraftforge/forge/win_args.txt");
            this.unix_args = new File(libPath + "net/minecraftforge/forge/unix_args.txt");
            this.jvm_args = new File(JarUtils.getJarDir(), "user_jvm_args.txt");

            install();
        }

        private void install() throws Exception {
            System.out.println("Checking the installation, please wait.");
            copyFileFromJar(lzma, "data/server.lzma");
			copyFileFromJar(universalJar, "data/forge-" + mcVer + "-" + forgeVer + "-universal.jar");
			copyFileFromJar(fmlloader, "data/fmlloader-" + mcVer + "-" + forgeVer + ".jar");
			copyFileFromJar(fmlcore, "data/fmlcore-" + mcVer + "-" + forgeVer + ".jar");
			copyFileFromJar(javafmllanguage, "data/javafmllanguage-" + mcVer + "-" + forgeVer + ".jar");
			copyFileFromJar(mclanguage, "data/mclanguage-" + mcVer + "-" + forgeVer + ".jar");
            copyFileFromJar(lowcodelanguage, "data/lowcodelanguage-" + mcVer + "-" + forgeVer + ".jar");

			//copyFileFromJar(win_args,"data/win_args.txt");
			//copyFileFromJar(unix_args,"data/unix_args.txt");
			//copyFileFromJar(jvm_args,"data/user_jvm_args.txt");

            if (magmaVer == null || mcpVer == null) {
                System.out.println("[Magma] There is an error with the installation, the forge / mcp version is not set.");
                System.exit(0);
            }

            if (minecraft_server.exists()) {
                mute();
                run("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "BUNDLER_EXTRACT", "--input", minecraft_server.getAbsolutePath(), "--output", libPath, "--libraries")),
                        stringToUrl(loadedLibsPaths));
                unmute();
                if (!mc_unpacked.exists()) {
                    System.out.println("Extract bundler");
                    mute();
                    run("net.minecraftforge.installertools.ConsoleTool",
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
                    run("net.minecraftforge.installertools.ConsoleTool",
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
                run("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "DOWNLOAD_MOJMAPS", "--version", mcVer, "--side", "server", "--output", mojmap.getAbsolutePath())),
						stringToUrl(loadedLibsPaths));
                unmute();
            }

            if (!mergedMapping.exists()) {
                System.out.println("Create merged mapping");
                mute();
                run("net.minecraftforge.installertools.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--task", "MERGE_MAPPING", "--left", mcpTxt.getAbsolutePath(), "--right", mojmap.getAbsolutePath(), "--output", mergedMapping.getAbsolutePath(), "--classes", "--reverse-right")),
						stringToUrl(loadedLibsPaths));
                unmute();
            }

            if (!slim.exists() || !extra.exists()) {
                System.out.println("Creating extra and slim jar files. This can take 1 minute.");
                mute();
                run("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", minecraft_server.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
						stringToUrl(loadedLibsPaths));
                run("net.minecraftforge.jarsplitter.ConsoleTool",
                        new ArrayList<>(Arrays.asList("--input", mc_unpacked.getAbsolutePath(), "--slim", slim.getAbsolutePath(), "--extra", extra.getAbsolutePath(), "--srg", mergedMapping.getAbsolutePath())),
						stringToUrl(loadedLibsPaths));
                unmute();
            }

            if (!srg.exists()) {
                System.out.println("Creating srg jar file. This can take 1 minute.");
                mute();
                run("net.minecraftforge.fart.Main",
                        new ArrayList<>(Arrays.asList("--input", slim.getAbsolutePath(), "--output", srg.getAbsolutePath(), "--names", mergedMapping.getAbsolutePath(), "--ann-fix", "--ids-fix", "--src-fix", "--record-fix")),
						stringToUrl(loadedLibsPaths));
                unmute();
            }

            String storedServerMD5 = null;
            String storedMohistMD5 = null;
            String serverMD5 = MD5Util.getMd5(serverJar);
            String magmaMD5 = MD5Util.getMd5(JarUtils.getFile());

            if (installInfo.exists()) {
                List<String> infoLines = Files.readAllLines(installInfo.toPath());
                if (infoLines.size() > 0)
                    storedServerMD5 = infoLines.get(0);
                if (infoLines.size() > 1)
                    storedMohistMD5 = infoLines.get(1);
            }

            if (!serverJar.exists()
                    || storedServerMD5 == null
                    || storedMohistMD5 == null
                    || !storedServerMD5.equals(serverMD5)
                    || !storedMohistMD5.equals(magmaMD5)) {
                System.out.println("Creating forge server jar. This can take 1 minute.");
                mute();
                run("net.minecraftforge.binarypatcher.ConsoleTool",
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
                serverMD5 = MD5Util.getMd5(serverJar);
            }

            FileWriter fw = new FileWriter(installInfo);
            fw.write(serverMD5 + "\n");
            fw.write(magmaMD5);
            fw.close();

            System.out.println("Finished the install verification process !");
        }

    }

}
