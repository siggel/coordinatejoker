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
