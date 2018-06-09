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

/**
 * factory for creating various exporters (currently locus and gpx supported)
 */
class ExporterFactory {

    /**
     * exporter generator method
     *
     * @param context             context required for sending intents, accessing resources etc.
     * @param exportSettings      export parameters
     * @return exporter instance of requested type
     */
    static Exporter getExporter(Context context,
                                ExportSettings exportSettings) {

        final String format = exportSettings.getFormat();
        if (format == null)
            throw new ExportException(context.getString(R.string.string_exporter_missing) + "unknown format.");
        else if (format.equalsIgnoreCase("gpx"))
            return new GpxExporter(context, exportSettings);
        else if (format.equalsIgnoreCase("kml"))
            return new KmlExporter(context, exportSettings);
        else if (format.equalsIgnoreCase("kmz"))
            return new KmzExporter(context, exportSettings);

        throw new ExportException(context.getString(R.string.string_exporter_missing) + format + ".");
    }
}
