package org.magmafoundation.magma.installer;

import org.magmafoundation.magma.MagmaConstants;
import org.magmafoundation.magma.MagmaStart;
import org.magmafoundation.magma.utils.JarTool;
import org.magmafoundation.magma.utils.MD5;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm)
 * @date 03.07.2022 - 17:19
 *
 * Inspired by Shawiiz_z (https://github.com/Shawiizz)
 */
public abstract class AbstractMagmaInstaller {

    private PrintStream origin = System.out;
    public String forgeVer;
    public String mcpVer;
    public String mcVer;
    public String libPath = new File(JarTool.getJarDir(), "libraries").getAbsolutePath() + "/";

    public String forgeStart;
    public File universalJar;
    public File serverJar;

    public File lzma;
    public File installInfo;

    public String otherStart;
    public File extra;
    public File slim;
    public File srg;

    public String mcpStart;
    public File mcpZip;
    public File mcpTxt;

    public File minecraft_server;

    protected AbstractMagmaInstaller() {
        this.forgeVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[1];
        this.mcpVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[3];
        this.mcVer = MagmaConstants.FORGE_VERSION_FULL.split("-")[0];

        this.forgeStart = libPath + "net/minecraftforge/forge/" + mcVer + "-" + forgeVer + "/forge-" + mcVer + "-" + forgeVer;
        this.universalJar = new File(forgeStart + "-universal.jar");
        this.serverJar = new File(forgeStart + "-server.jar");

        this.lzma = new File(libPath + "org/magma/install/data/server.lzma");
        this.installInfo = new File(libPath + "org/magma/install/installInfo");

        this.otherStart = libPath + "net/minecraft/server/" + mcVer + "-" + mcpVer + "/server-" + mcVer + "-" + mcpVer;

        this.extra = new File(otherStart + "-extra.jar");
        this.slim = new File(otherStart + "-slim.jar");
        this.srg = new File(otherStart + "-srg.jar");

        this.mcpStart = libPath + "de/oceanlabs/mcp/mcp_config/" + mcVer + "-" + mcpVer + "/mcp_config-" + mcVer + "-" + mcpVer;
        this.mcpZip = new File(mcpStart + ".zip");
        this.mcpTxt = new File(mcpStart + "-mappings.txt");

        this.minecraft_server = new File(libPath + "minecraft_server." + mcVer + ".jar");
    }

    protected void launchService(String mainClass, List<String> args, List<URL> classPath) throws Exception {
        System.out.println(getParentClassloader() == null);
        try {
            Class.forName(mainClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class.forName(mainClass, true, new URLClassLoader(classPath.toArray(new URL[0]), getParentClassloader())).getDeclaredMethod("main", String[].class).invoke(null, (Object) args.toArray(new String[0]));
    }

    private ClassLoader getParentClassloader() {
        try {
            return (ClassLoader) ClassLoader.class.getDeclaredMethod("getPlatformClassLoader").invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    protected List<URL> stringToUrl(List<String> strs) throws Exception {
        List<URL> temp = new ArrayList<>();
        for (String t : strs)
            temp.add(new File(t).toURI().toURL());
        return temp;
    }

    /*
    THIS IS TO NOT SPAM CONSOLE WHEN IT WILL PRINT A LOT OF THINGS
     */
    protected void mute() throws Exception {
        File out = new File(libPath + "org/magma/installLog/install.log");
        if(!out.exists()) {
            out.getParentFile().mkdirs();
            out.createNewFile();
        }
        System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(out))));
    }

    protected void unmute() {
        System.setOut(origin);
    }

    protected void copyFileFromJar(File file, String pathInJar) throws Exception {
        InputStream is = MagmaStart.class.getClassLoader().getResourceAsStream(pathInJar);
        if(!file.exists() || !MD5.getMd5(file).equals(MD5.getMd5(is)) || file.length() <= 1) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            if(is != null) Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            else {
                System.out.println("The file " + file.getName() + " doesn't exists in the this jar !");
                System.exit(0);
            }
        }
    }

    protected boolean isCorrupted(File f) {
        try {
            JarFile j = new JarFile(f);
            j.close();
            return false;
        } catch (IOException e) {
            return true;
        }
    }

}