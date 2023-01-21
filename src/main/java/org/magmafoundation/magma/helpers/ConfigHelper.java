package org.magmafoundation.magma.helpers;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

public class ConfigHelper {

    private static final List<String> checkedLevelsSpigot = new ArrayList<>();
    private static final List<String> checkedLevelsPaper = new ArrayList<>();

    public static boolean isSpigotConfigAlreadyRegistered(String levelName) {
        if (levelName.equals(Strings.EMPTY))
            return true;

        // If the level is already registered, return true. If it is not, add it to the list and return false.
        return checkedLevelsSpigot.contains(levelName) ? true : !checkedLevelsSpigot.add(levelName);
    }

    public static boolean isPaperConfigAlreadyRegistered(String levelName) {
        if (levelName.equals(Strings.EMPTY))
            return true;

        // If the level is already registered, return true. If it is not, add it to the list and return false.
        return checkedLevelsPaper.contains(levelName) ? true : !checkedLevelsPaper.add(levelName);
    }
}
