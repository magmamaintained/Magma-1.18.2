package org.magmafoundation.magma.helpers;

public class EnumHelper {

    public static <T extends Enum<?>> T addEnum0(Class<T> enumClass, String enumName, Class<?>[] paramTypes, Object... params) throws Throwable {
        return addEnum(enumClass, enumName, paramTypes, params);
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumClass, String enumName, Class<?>[] paramTypes, Object[] params) throws Throwable {
        return EnumJ17Helper.addEnum(enumClass, enumName, paramTypes, params);
    }
}
