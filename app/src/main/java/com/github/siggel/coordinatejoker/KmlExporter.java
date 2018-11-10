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

import java.io.File;
import java.util.List;

/**
 * Exporter for sending waypoints via kml file
 * Sending will be done via parent's functionality, so only the file creation needs to be done here.
 */
class KmlExporter extends Exporter {

    /**
     * string buffer for creating kml content
     */
    private StringBuffer kmlData = null;

    /**
     * constructor
     * <p>
     * looks unused as constructor is used in exporter factory via reflection
     *
     * @param context        the app's main context required for sending intents, accessing resources etc.
     * @param exportSettings export parameters
     */
    @SuppressWarnings("unused")
    KmlExporter(Context context, ExportSettings exportSettings) {
        super(context, exportSettings);
    }

    /**
     * method for exporting waypoints to kml file and sending it as intent to other apps
     *
     * @param waypoints waypoints to be exported
     */
    @Override
    public void export(List<Point> waypoints) {

        File file;

        try {

            // create kml content
            init();
            addHeader();
            addWaypoints(waypoints);
            addFooter();

            file = new File(baseDirForTemporaryFiles, "coordinatejoker.kml");
            FileHelper.writeContentToFile(file, kmlData.toString());

        } catch (Exception e) {
            throw new ExportException(context.getString(R.string.string_kml_export_failed));
        }

        // use parent's general functionality for sending intent
        final String mimeType = "application/vnd.google-earth.kml+xml";
        sendFileViaIntent(file, mimeType);
    }

    /**
     * clear kmlData to begin a new kml file
     */
    private void init() {
        this.kmlData = new StringBuffer();
    }

    /**
     * add kml header to kmlData
     */
    private void addHeader() {
        kmlData.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");
        kmlData.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        kmlData.append("  <Document>\n");
        kmlData.append("    <atom:author><atom:name>Coordinate Joker</atom:name></atom:author>\n");
    }

    /**
     * add kml footer to kmlData
     */
    private void addFooter() {
        kmlData.append("  </Document>\n");
        kmlData.append("</kml>\n");
    }

    /**
     * add waypoints to kmlData
     *
     * @param waypoints list of waypoints to be added
     */
    private void addWaypoints(List<Point> waypoints) {
        for (Point waypoint : waypoints) {
            kmlData.append("    <Placemark>\n");
            //noinspection StringConcatenationInsideStringBufferAppend
            kmlData.append("      <name>" + waypoint.getName() + "</name>\n");
            kmlData.append("      <Point>\n");
            kmlData.append(String.format("        <coordinates>%s,%s</coordinates>\n",
                    waypoint.getLongitude(),
                    waypoint.getLatitude()));
            kmlData.append("      </Point>\n");
            kmlData.append("    </Placemark>\n");
        }
    }
}
