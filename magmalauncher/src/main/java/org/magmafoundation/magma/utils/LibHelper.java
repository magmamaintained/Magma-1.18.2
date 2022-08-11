package org.magmafoundation.magma.utils;

import dev.vankka.dependencydownload.DependencyManager;
import dev.vankka.dependencydownload.classpath.ClasspathAppender;
import dev.vankka.dependencydownload.dependency.Dependency;
import dev.vankka.dependencydownload.repository.Repository;
import org.magmafoundation.magma.common.utils.Hash;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

// Code copied from package dev.vankka.dependencydownload, all credit goes to the author
// Check them out: https://github.com/Vankka/DependencyDownload
public class LibHelper {

    public static void downloadDependency(DependencyManager manager, Dependency dependency, List<Repository> repositories) throws IOException, NoSuchAlgorithmException {
        Path dependencyPath = manager.getPathForDependency(dependency, false);

        if (!Files.exists(dependencyPath.getParent())) {
            Files.createDirectories(dependencyPath.getParent());
        }

        if (Files.exists(dependencyPath)) {
            String fileHash = Hash.getHash(dependencyPath.toFile(), dependency.getHashingAlgorithm());
            if (!fileHash.equals(dependency.getHash()))
                Files.delete(dependencyPath);
            else return;
        }
        Files.createFile(dependencyPath);

        RuntimeException failure = new RuntimeException("All provided repositories failed to download dependency");
        for (Repository repository : repositories) {
            try {
                MessageDigest digest = MessageDigest.getInstance(dependency.getHashingAlgorithm());
                downloadFromRepository(dependency, repository, dependencyPath, digest);

                String hash = Hash.getHash(digest);
                String dependencyHash = dependency.getHash();
                if (!hash.equals(dependencyHash)) {
                    throw new RuntimeException("Failed to verify file hash: " + hash + " should've been: " + dependencyHash);
                }

                // Success
                return;
            } catch (Throwable e) {
                Files.deleteIfExists(dependencyPath);
                failure.addSuppressed(e);
            }
        }
        throw failure;
    }

    public static void loadDependency(DependencyManager manager, Dependency dependency, ClasspathAppender classpathAppender) throws MalformedURLException {
        classpathAppender.appendFileToClasspath(manager.getPathForDependency(dependency, false));
    }

    private static void downloadFromRepository(Dependency dependency, Repository repository, Path dependencyPath, MessageDigest digest) throws Throwable {
        HttpsURLConnection connection = repository.openConnection(dependency);

        byte[] buffer = new byte[4096];
        try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream())) {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(dependencyPath))) {
                int total;
                while ((total = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, total);
                    digest.update(buffer, 0, total);
                }
            }
        }
    }
}
