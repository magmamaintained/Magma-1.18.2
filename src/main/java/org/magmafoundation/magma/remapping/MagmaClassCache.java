package org.magmafoundation.magma.remapping;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import io.izzel.tools.product.Product;
import io.izzel.tools.product.Product2;
import io.izzel.tools.product.Product4;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarFile;

public abstract class MagmaClassCache implements AutoCloseable {

    public abstract CacheSegment makeSegment(URLConnection connection) throws IOException;

    public abstract void save() throws IOException;

    public interface CacheSegment {

        Optional<byte[]> findByName(String name) throws IOException;

        void addToCache(String name, byte[] value);

        void save() throws IOException;
    }

    private static final Marker MARKER = MarkerManager.getMarker("CLCACHE");
    private static final MagmaClassCache INSTANCE = new Impl();

    public static MagmaClassCache instance() {
        return INSTANCE;
    }

    private static class Impl extends MagmaClassCache {

        private final ConcurrentHashMap<String, JarSegment> map = new ConcurrentHashMap<>();
        private final Path basePath = Paths.get(".magma/class_cache");

        @Override
        public CacheSegment makeSegment(URLConnection connection) throws IOException {
            if (connection instanceof JarURLConnection) {
                JarFile file = ((JarURLConnection) connection).getJarFile();
                return this.map.computeIfAbsent(file.getName(), LamdbaExceptionUtils.rethrowFunction(JarSegment::new));
            } else {
                return new EmptySegment();
            }
        }

        @Override
        public void save() throws IOException {
        }

        @Override
        public void close() throws Exception {
        }


        private class JarSegment implements CacheSegment {

            private final Map<String, Product2<Long, Integer>> rangeMap = new ConcurrentHashMap<>();
            private final ConcurrentLinkedQueue<Product4<String, byte[], Long, Integer>> savingQueue = new ConcurrentLinkedQueue<>();
            private final AtomicLong sizeAllocator;
            private final Path indexPath, blobPath;

            private JarSegment(String fileName) throws IOException {
                Path jarFile = new File(fileName).toPath();
                Hasher hasher = Hashing.sha256().newHasher();
                hasher.putBytes(Files.readAllBytes(jarFile));
                String hash = hasher.hash().toString();
                this.indexPath = basePath.resolve("index").resolve(hash);
                this.blobPath = basePath.resolve("blob").resolve(hash);
                if (!Files.exists(indexPath)) {
                    Files.createFile(indexPath);
                }
                if (!Files.exists(blobPath)) {
                    Files.createFile(blobPath);
                }
                sizeAllocator = new AtomicLong(Files.size(blobPath));
                read();
            }

            @Override
            public Optional<byte[]> findByName(String name) throws IOException {
                Product2<Long, Integer> product2 = rangeMap.get(name);
                if (product2 != null) {
                    long off = product2._1;
                    int len = product2._2;
                    try (SeekableByteChannel channel = Files.newByteChannel(blobPath)) {
                        channel.position(off);
                        ByteBuffer buffer = ByteBuffer.allocate(len);
                        channel.read(buffer);
                        return Optional.of(buffer.array());
                    }
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public void addToCache(String name, byte[] value) {
                int len = value.length;
                long off = sizeAllocator.getAndAdd(len);
                savingQueue.add(Product.of(name, value, off, len));
            }

            @Override
            public synchronized void save() throws IOException {
                if (savingQueue.isEmpty()) return;
                List<Product4<String, byte[], Long, Integer>> list = new ArrayList<>();
                while (!savingQueue.isEmpty()) {
                    list.add(savingQueue.poll());
                }
                try (OutputStream outIndex = Files.newOutputStream(indexPath, StandardOpenOption.APPEND);
                     DataOutputStream dataOutIndex = new DataOutputStream(outIndex);
                     SeekableByteChannel channel = Files.newByteChannel(blobPath, StandardOpenOption.WRITE)) {
                    for (Product4<String, byte[], Long, Integer> product4 : list) {
                        channel.position(product4._3);
                        channel.write(ByteBuffer.wrap(product4._2));
                        dataOutIndex.writeUTF(product4._1);
                        dataOutIndex.writeLong(product4._3);
                        dataOutIndex.writeInt(product4._4);
                        rangeMap.put(product4._1, Product.of(product4._3, product4._4));
                    }
                }
            }

            private synchronized void read() throws IOException {
                try (InputStream inputStream = Files.newInputStream(indexPath);
                     DataInputStream dataIn = new DataInputStream(inputStream)) {
                    while (dataIn.available() > 0) {
                        String name = dataIn.readUTF();
                        long off = dataIn.readLong();
                        int len = dataIn.readInt();
                        rangeMap.put(name, Product.of(off, len));
                    }
                }
            }
        }

        private static class EmptySegment implements CacheSegment {

            @Override
            public Optional<byte[]> findByName(String name) {
                return Optional.empty();
            }

            @Override
            public void addToCache(String name, byte[] value) {
            }

            @Override
            public void save() {
            }
        }
    }
}
