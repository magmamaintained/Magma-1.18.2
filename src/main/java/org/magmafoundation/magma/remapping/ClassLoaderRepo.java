package org.magmafoundation.magma.remapping;

import net.md_5.specialsource.repo.ClassRepo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class ClassLoaderRepo implements ClassRepo {

    private final ClassLoader classLoader;

    public ClassLoaderRepo(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassNode findClass(String internalName) {
        return findClass(internalName, ClassReader.SKIP_CODE);
    }

    public ClassNode findClass(String internalName, int parsingOptions) {
        URL url = classLoader instanceof URLClassLoader
            ? ((URLClassLoader) classLoader).findResource(internalName + ".class") // search local
            : classLoader.getResource(internalName + ".class");
        if (url == null) return null;
        try {
            URLConnection connection = url.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                ClassReader reader = new ClassReader(inputStream);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, parsingOptions);
                return classNode;
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
