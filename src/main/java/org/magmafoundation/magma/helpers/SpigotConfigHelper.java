package org.magmafoundation.magma.helpers;

import java.util.ArrayList;
import java.util.List;

public class SpigotConfigHelper {

    private static final List<String> checkedLevels = new ArrayList<>();

    public static boolean isAlreadyRegistered(String levelName) {
        // If the level is already registered, return true. If it is not, add it to the list and return false.
        return checkedLevels.contains(levelName) ? true : !checkedLevels.add(levelName);
    }
}
