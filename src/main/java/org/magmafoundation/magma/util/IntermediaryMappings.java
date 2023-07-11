package org.magmafoundation.magma.util;

import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.magmafoundation.magma.error.StackTraceDeobfuscator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.magmafoundation.magma.error.StackTraceDeobfuscator.methodKey;

// Some of the code here is from https://github.com/PaperMC/Paper/blob/ver/1.18.2/patches/server/0413-Deobfuscate-stacktraces-in-log-messages-crash-report.patch
public final class IntermediaryMappings {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntermediaryMappings.class);

    private static Map<String, ClassMapping> mappings;

    public static void tryLoad() {
        try (InputStream mapping = IntermediaryMappings.class.getResourceAsStream("/mappings/server.txt")) {
            if (mapping == null)
                throw new FileNotFoundException("Could not find intermediary mappings");

            MemoryMappingTree tree = new MemoryMappingTree();
            MappingReader.read(new InputStreamReader(mapping, StandardCharsets.UTF_8), MappingFormat.PROGUARD, tree);

            String LEFT = "named";
            String RIGHT = "official";

            // Since the mapping works like 'named -> official', the source is technically the right side
            tree.setSrcNamespace(RIGHT);
            tree.setDstNamespaces(Collections.singletonList(LEFT));

            final Set<ClassMapping> classes = new HashSet<>();

            final StackTraceDeobfuscator.StringPool pool = new StackTraceDeobfuscator.StringPool();
            for (final MappingTree.ClassMapping cls : tree.getClasses()) {
                final Map<String, String> methods = new HashMap<>();

                for (final MappingTree.MethodMapping methodMapping : cls.getMethods()) {
                    methods.put(
                            pool.string(methodKey(methodMapping.getName(LEFT), methodMapping.getDesc(LEFT))),
                            pool.string(methodMapping.getName(RIGHT))
                    );
                }

                final ClassMapping map = new ClassMapping(
                        cls.getName(LEFT).replace('/', '.'),
                        cls.getName(RIGHT).replace('/', '.'),
                        Map.copyOf(methods)
                );
                classes.add(map);
            }

            mappings = Set.copyOf(classes).stream().collect(Collectors.toUnmodifiableMap(ClassMapping::deobfName, map -> map));
            LOGGER.info("Loaded {} intermediary mappings", mappings.size());
        } catch (Exception e) {
            LOGGER.warn("Failed to load intermediary mappings", e);
        }
    }

    public static String getFromMojang(String className, String mojangName, String desc) {
        if (mappings == null || mappings.isEmpty() || mojangName == null || mojangName.isBlank())
            return mojangName;

        ClassMapping mapping = mappings.get(className);
        if (mapping == null)
            return mojangName;

        return mapping.methods().getOrDefault(methodKey(mojangName, desc), mojangName);
    }

    public record ClassMapping(String obfName, String deobfName, Map<String, String> methods) {}
}
