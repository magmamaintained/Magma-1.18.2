package org.magmafoundation.magma.helpers;

import com.google.common.collect.Lists;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class EnumJ17Helper {

    private static MethodHandles.Lookup lookup = null;
    private static boolean setup = false;
    private static Unsafe unsafe;

    public static void setup() {
        if (!setup) {
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Unsafe unsafe = (Unsafe) unsafeField.get(null);
                Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                lookupField = (Field) unsafe.getObject(unsafe.staticFieldBase(lookupField), unsafe.staticFieldOffset(lookupField));
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            setup = true;
        }
    }

    /*
    * Found at: http://niceideas.ch/roller2/badtrash/entry/java_create_enum_instances_dynamically
    *
    * Coded with Github Copilot
    */

    private static MethodHandle getConstructorAccessor(Class<?> enumClass, Class<?>[] parameterTypes) throws NoSuchMethodException, IllegalAccessException {
        Class<?>[] parameterTypesWithEnum = new Class[parameterTypes.length + 2];
        parameterTypesWithEnum[0] = String.class;
        parameterTypesWithEnum[1] = int.class;
        System.arraycopy(parameterTypes, 0, parameterTypesWithEnum, 2, parameterTypes.length);
        return lookup.findConstructor(enumClass, MethodType.methodType(void.class, parameterTypesWithEnum));
    }

    private static <T extends Enum<?>> T makeEnum(Class<T> enumClass, String name, int ordinal, Class<?>[] parameterTypes, Object[] parameterValues) throws Throwable {
        int additionalParameterCount = parameterTypes.length;
        Object[] parameterValuesWithEnum = new Object[additionalParameterCount + 2];
        parameterValuesWithEnum[0] = name;
        parameterValuesWithEnum[1] = ordinal;
        if(parameterValues != null) {
            System.arraycopy(parameterValues, 0, parameterValuesWithEnum, 2, additionalParameterCount);
        }
        return enumClass.cast(getConstructorAccessor(enumClass, parameterTypes).invokeWithArguments(parameterValuesWithEnum));
    }

    public static void setFailsafeFieldValue(Field field, Object instance, Object value) throws Throwable {
        if(instance == null) {
            lookup.findSetter(field.getDeclaringClass(), field.getName(), field.getType()).invoke(instance, value);
        } else {
            lookup.findStaticSetter(field.getDeclaringClass(), field.getName(), field.getType()).invoke(instance, value);
        }
    }

    private static void blankField(Class<?> clazz, String fieldName) throws Throwable {
        for(Field field : clazz.getDeclaredFields()) {
            if(field.getName().equals(fieldName)) {
                setFailsafeFieldValue(field, clazz, null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class<?> clazz) throws Throwable {
        blankField(clazz, "enumConstantDirectory");
        blankField(clazz, "enumConstants");
        blankField(clazz, "enumVars");
    }

    public static <T extends Enum<?>> T addEnum0(Class<T> enumClass, String name, Class<?>[] parameterTypes, Object... parameterValues) throws Throwable {
        return addEnum(enumClass, name, parameterTypes, parameterValues);
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumClass, String name, Class<?>[] parameterTypes, Object... parameterValues) throws Throwable {
        if(!setup) {
            setup();
        }

        Field valuesField = null;
        Field[] fields = enumClass.getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equals("$VALUES") || fieldName.equals("ENUM$VALUES")) {
                valuesField = field;
                break;
            }
        }
        int flags = (Modifier.PUBLIC) | Modifier.STATIC | Modifier.FINAL | 0x1000;
        if(valuesField == null) {
            String type = String.format("[L%s;", enumClass.getName().replace('.', '/'));

            for(Field field : fields) {
                if((field.getModifiers() & flags) == flags & field.getType().getName().replace('.', '/').equals(type)) {
                    valuesField = field;
                    break;
                }
            }
        }

        if(valuesField == null) {
            final List<String> lines = Lists.newCopyOnWriteArrayList();
            lines.add(String.format("Could not find $VALUES field for %s", enumClass.getName()));
            lines.add(String.format("Flags: %s", String.format("%16s", Integer.toBinaryString(flags)).replace(' ', '0')));
            lines.add("Fields:");
            for (Field field : fields) {
                String mods = String.format("%16s", Integer.toBinaryString(field.getModifiers()).replace(' ', '0'));
                lines.add(String.format("        %s %s %s", mods, field.getName(), field.getType().getName()));
            }

            return null;
        }

        valuesField.setAccessible(true);

        try {
            T[] previousValues = (T[]) valuesField.get(enumClass);
            List<T> values = Lists.newCopyOnWriteArrayList(Arrays.asList(previousValues));
            T newValue = (T) makeEnum(enumClass, name, values.size(), parameterTypes, parameterValues);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumClass, 0)));
            cleanEnumCache(enumClass);

            return newValue;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t.getMessage(), t);
        }
    }
    static {
        if(!setup) {
            setup();
        }
    }

    public static void setField(Object obj, Object value, Field field) throws Throwable {
        if(obj == null) {
            setStaticField(field, value);
        } else {
            try {
                unsafe.putObject(obj, unsafe.objectFieldOffset(field), value);
            } catch (Exception e) {
                throw new ReflectiveOperationException(e);
            }
        }
    }

    public static void setStaticField(Field field, Object value) throws Throwable {
        try {
            lookup.ensureInitialized(field.getDeclaringClass());
            unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), value);
        } catch (Exception e) {
            throw new ReflectiveOperationException(e);
        }
    }

    public static <T> T getField(Object obj, Field field) throws Throwable {
        if(obj == null) {
            return getStaticField(field);
        } else {
            try {
                return (T) unsafe.getObject(obj, unsafe.objectFieldOffset(field));
            } catch (Exception e) {
                throw new ReflectiveOperationException(e);
            }
        }
    }

    public static <T> T getStaticField(Field field) throws Throwable {
        try {
            lookup.ensureInitialized(field.getDeclaringClass());
            return (T) unsafe.getObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
        } catch (Exception e) {
            throw new ReflectiveOperationException(e);
        }
    }
}
