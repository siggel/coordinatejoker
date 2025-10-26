/*
 * Copyright (c) 2018-2025 by siggel <siggel-apps@gmx.de>
 *
 *     This file is part of Coordinate Joker.
 *
 *     Coordinate Joker is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Coordinate Joker is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Coordinate Joker.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.siggel.coordinatejoker;

import android.content.Context;

import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * A factory for creating various exporter instances
 * <p>
 * you may register any further exporter as long as it derives from this package's Exporter
 */
class ExporterFactory {

    private static final Map<String, Class<?>> registeredExporters = new HashMap<>();

    static {
        register("gpx", GpxExporter.class);
        register("kml", KmlExporter.class);
        register("kmz", KmzExporter.class);
    }

    private static void register(String name, Class<?> exporter) {
        if (Exporter.class.isAssignableFrom(exporter)) {
            registeredExporters.put(name, exporter);
        }
    }

    /**
     * exporter generator method
     *
     * @param context        context required for sending intents, accessing resources etc.
     * @param exportSettings export parameters
     * @return exporter instance of requested type
     */
    @NonNull
    static Exporter getExporter(@NonNull Context context,
                                @NonNull ExportSettings exportSettings) {
        final String format = exportSettings.getFormat().toLowerCase();
        try {
            Class<?> clazz = getRegisteredExporter(format);
            return createExporterInstance(context, exportSettings, clazz);
        } catch (Exception e) {
            throw new ExportException(context.getString(R.string.string_exporter_missing) + " \"" + format + "\".");
        }
    }

    @NonNull
    private static Class<?> getRegisteredExporter(@NonNull String format) {
        Class<?> result = registeredExporters.get(format);
        if (result == null) {
            throw new RuntimeException();
        }
        return result;
    }

    @NonNull
    private static Exporter createExporterInstance(@NonNull Context context, @NonNull ExportSettings exportSettings, @NonNull Class<?> clazz) throws Exception {
        Constructor<?> constructor = clazz.getDeclaredConstructor(Context.class, ExportSettings.class);
        return (Exporter) constructor.newInstance(context, exportSettings);
    }

}
