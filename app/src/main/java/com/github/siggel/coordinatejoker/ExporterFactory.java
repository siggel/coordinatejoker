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
     * @param type                file type of exporter
     * @param useActionViewIntent whether intent shall be sent as ACTION_VIEW or ACTION_SEND
     * @return exporter instance of requested type
     */
    static Exporter getExporter(Context context,
                                String type,
                                Boolean useActionViewIntent) {
        if (type == null)
            return null;
        else if (type.equalsIgnoreCase("gpx"))
            return new GpxExporter(context, useActionViewIntent);
        else if (type.equalsIgnoreCase("kml"))
            return new KmlExporter(context, useActionViewIntent);
        else if (type.equalsIgnoreCase("kmz"))
            return new KmzExporter(context, useActionViewIntent);

        throw new ExportException(context.getString(R.string.string_exporter_missing) + type + ".");
    }
}
