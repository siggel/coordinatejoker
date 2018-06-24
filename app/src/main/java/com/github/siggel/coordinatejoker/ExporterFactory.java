/*
 * Copyright (c) 2018 by siggel <siggel-apps@gmx.de>
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
import android.support.annotation.NonNull;

import java.lang.reflect.Constructor;

/**
 * A factory for creating various exporters
 * <p>
 * By convention derived Exporter classes must be named "XyzExporter" for exportSettings.format
 * "xyz" if you want to use this factory for getting an instance
 */
class ExporterFactory {

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

        final String format = exportSettings.getFormat();

        if (format == null)
            throw new ExportException(context.getString(R.string.string_exporter_missing) + "unknown format.");

        // format to class name convention: xyz > fullPackageName.XyzExporter
        final String className = Exporter.class.getPackage().getName() + "." +
                format.substring(0, 1).toUpperCase() + format.substring(1) + "Exporter";
        try {
            // find class by class name and return new instance
            Class<?> aClass = Class.forName(className);
            Constructor<?> constructor = aClass.getDeclaredConstructor(Context.class,
                    ExportSettings.class);
            return (Exporter) constructor.newInstance(context, exportSettings);
        } catch (Exception e) {
            throw new ExportException(context.getString(R.string.string_exporter_missing) + " " + format + ".");
        }
    }
}
