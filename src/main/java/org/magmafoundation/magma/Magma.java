/*
 * Magma Server
 * Copyright (C) 2019-2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.io.LoggerBufferedInputStream;
import org.slf4j.LoggerFactory;

/**
 * Magma
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 28/06/2020 - 12:32 am
 */
public class Magma {

    private static final String NAME = "Magma";
    private static final String VERSION = (Magma.class.getPackage().getImplementationVersion() != null) ? Magma.class.getPackage().getImplementationVersion() : "dev-env";
    private static final String BUKKIT_VERSION = "v1_18_R2";
    private static final String NMS_PREFIX = "net/minecraft/server/";
    private static Magma INSTANCE = new Magma();
    public static Logger LOGGER = LogManager.getLogger(Magma.class);

    public Magma() {
        INSTANCE = this;
    }

    public static Magma getInstance() {
        return INSTANCE;
    }

    public static String getName() {
        return NAME;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getBukkitVersion() {
        return BUKKIT_VERSION;
    }

    public static String getNmsPrefix() {
        return NMS_PREFIX;
    }

}
