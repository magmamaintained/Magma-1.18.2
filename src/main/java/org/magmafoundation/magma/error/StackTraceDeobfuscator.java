package org.magmafoundation.magma.error;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.magmafoundation.magma.configuration.MagmaConfig;
import org.magmafoundation.magma.deobf.IStackTraceDeobfuscator;
import org.magmafoundation.magma.remapping.MagmaRemapper;
import org.magmafoundation.magma.util.IntermediaryMappings;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;

// Some of the code here is from https://github.com/PaperMC/Paper/blob/ver/1.18.2/patches/server/0413-Deobfuscate-stacktraces-in-log-messages-crash-report.patch
public class StackTraceDeobfuscator implements IStackTraceDeobfuscator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackTraceDeobfuscator.class);

    private final Map<Class<?>, Map<String, IntList>> lineMapCache = Collections.synchronizedMap(new LinkedHashMap<>(128, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Class<?>, Map<String, IntList>> eldest) {
            return this.size() > 127;
        }
    });

    private final Map<String, List<Mapping>> mappings = new HashMap<>();

    public StackTraceDeobfuscator() {
        LOGGER.info("Loading mappings...");
        IntermediaryMappings.tryLoad();
        final Map<String, String> methods = MagmaRemapper.getResourceMapper().jarMapping.methods;
        methods.forEach(this::getMappings);
        LOGGER.info("Loaded {} mappings", methods.size());
    }

    private void getMappings(String obf, String mojang) {
        String raw = obf.substring(0, obf.indexOf('(') - 1);
        int lastSlash = raw.lastIndexOf('/');
        String classPath = raw.substring(0, lastSlash);
        String methodName = raw.substring(lastSlash + 1);
        String desc = obf.substring(obf.indexOf(methodName) + methodName.length() + 1).trim();
        mappings.computeIfAbsent(classPath.replace('/', '.'), k -> new ArrayList<>()).add(Mapping.from(methodName, desc, mojang));
    }

    public void deobf(Throwable throwable) {
        if (!MagmaConfig.instance.debugDeobfuscateStacktraces.getValues() || mappings == null || mappings.isEmpty())
            return;

        throwable.setStackTrace(deobf(throwable.getStackTrace()));
        final Throwable cause = throwable.getCause();
        if (cause != null) deobf(cause);

        final Throwable[] suppressed = throwable.getSuppressed();
        for (final Throwable s : suppressed) {
            deobf(s);
        }
    }

    public StackTraceElement[] deobf(StackTraceElement[] stacktrace) {
        if (!MagmaConfig.instance.debugDeobfuscateStacktraces.getValues() || stacktrace.length == 0 || mappings.isEmpty())
            return stacktrace;

        final StackTraceElement[] deobf = new StackTraceElement[stacktrace.length];
        for (int i = 0; i < stacktrace.length; i++) {
            final StackTraceElement element = stacktrace[i];

            final String className = element.getClassName();

            final List<Mapping> mapping = mappings.get(className);
            if (mapping == null) {
                deobf[i] = element;
                continue;
            }

            final Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to find class for {}", className, e);
                deobf[i] = element;
                continue;
            }

            final String methodKey = this.determineMethodForLine(clazz, element.getLineNumber());

            if (methodKey == null) {
                deobf[i] = element;
                continue;
            }

            final Mapping map = mapping.stream().filter(m -> (m.obfMethod + m.obfDesc).equals(methodKey)).findFirst().orElse(null);

            if (map == null) {
                deobf[i] = element;
                continue;
            }

            final String methodName = IntermediaryMappings.getFromMojang(className, map.mojang(), map.obfDesc());

            deobf[i] = new StackTraceElement(
                    element.getClassLoaderName(),
                    element.getModuleName(),
                    element.getModuleVersion(),
                    className,
                    Objects.equals(methodName, map.mojang()) ? element.getMethodName() : methodName,
                    sourceFileName(className),
                    element.getLineNumber()
            );
        }

        return deobf;
    }

    private String determineMethodForLine(final Class<?> clazz, final int lineNumber) {
        final Map<String, IntList> lineMap = this.lineMapCache.computeIfAbsent(clazz, StackTraceDeobfuscator::buildLineMap);
        for (final var entry : lineMap.entrySet()) {
            final String methodKey = entry.getKey();
            final IntList lines = entry.getValue();
            for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
                final int num = lines.getInt(i);
                if (num == lineNumber) {
                    return methodKey;
                }
            }
        }
        return null;
    }

    private static String sourceFileName(final String fullClassName) {
        final int dot = fullClassName.lastIndexOf('.');
        final String className = dot == -1
                ? fullClassName
                : fullClassName.substring(dot + 1);
        final String rootClassName = className.split("\\$")[0];
        return rootClassName + ".java";
    }

    private static Map<String, IntList> buildLineMap(final Class<?> key) {
        final Map<String, IntList> lineMap = new HashMap<>();
        final class LineCollectingMethodVisitor extends MethodVisitor {
            private final IntList lines = new IntArrayList();
            private final String name;
            private final String descriptor;

            LineCollectingMethodVisitor(String name, String descriptor) {
                super(Opcodes.ASM9);
                this.name = name;
                this.descriptor = descriptor;
            }

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                this.lines.add(line);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                lineMap.put(methodKey(this.name, this.descriptor), this.lines);
            }
        }
        final ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new LineCollectingMethodVisitor(name, descriptor);
            }
        };
        try {
            final InputStream inputStream = StackTraceDeobfuscator.class.getClassLoader()
                    .getResourceAsStream(key.getName().replace('.', '/') + ".class");
            if (inputStream == null) {
                throw new IllegalStateException("Could not find class file: " + key.getName());
            }
            final byte[] classData;
            try (inputStream) {
                classData = inputStream.readAllBytes();
            }
            final ClassReader reader = new ClassReader(classData);
            reader.accept(classVisitor, 0);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return lineMap;
    }

    public static String methodKey(final String obfName, final String obfDescriptor) {
        return obfName + obfDescriptor;
    }

    public static final class StringPool {
        private final Map<String, String> pool = new HashMap<>();

        public String string(final String string) {
            return this.pool.computeIfAbsent(string, Function.identity());
        }
    }

    public record Mapping(String obfMethod, String obfDesc, String mojang) {

        static Mapping from(String obfMethod, String obfDesc, String mojang) {
            return new Mapping(obfMethod, obfDesc, mojang);
        }
    }
}
