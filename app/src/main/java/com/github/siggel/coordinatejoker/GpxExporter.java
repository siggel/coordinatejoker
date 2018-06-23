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
 * Exporter for sending waypoints via gpx file
 * Sending will be done via parent's functionality, so only the file creation needs to be done here.
 */
class GpxExporter extends Exporter {

    /**
     * string buffer for creating gpx content
     */
    private StringBuffer gpxData = null;

    /**
     * constructor
     *
     * @param context        the app's main context required for sending intents, accessing resources etc.
     * @param exportSettings export parameters
     */
    GpxExporter(Context context, ExportSettings exportSettings) {
        super(context, exportSettings);
    }

    /**
     * method for exporting waypoints to gpx file and sending it as intent to other activities
     *
     * @param waypoints waypoints to be exported
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void export(List<Point> waypoints) {

        File file;

        try {

            // create gpx content
            init();
            addHeader();
            addWaypoints(waypoints);
            addFooter();

            file = new File(baseDirForTemporaryFiles, "coordinatejoker.gpx");
            FileHelper.writeContentToFile(file, gpxData.toString());

        } catch (Exception e) {
            throw new ExportException(context.getString(R.string.string_gpx_export_failed));
        }

        // use parent's general functionality for sending intent
        final String mimeType = "application/gpx+xml";
        sendFileViaIntent(file, mimeType);
    }

    /**
     * clear gpxData to begin a new gpx file
     */
    private void init() {
        this.gpxData = new StringBuffer("");
    }

    /**
     * add gpx header to gpxData
     */
    private void addHeader() {
        gpxData.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");
        gpxData.append("<gpx version=\"1.1\" creator=\"Coordinate Joker, Android\"\n");
        gpxData.append("  xmlns=\"http://www.topografix.com/GPX/1/1\"\n");
        gpxData.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        //noinspection StringConcatenationInsideStringBufferAppend
        gpxData.append("  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                "http://www.topografix.com/GPX/1/1/gpx.xsd\"\n");
        gpxData.append("  xmlns:gpx_style=\"http://www.topografix.com/GPX/gpx_style/0/2\">\n");
        gpxData.append("  <metadata>\n");
        gpxData.append("    <desc>File with points from Coordinate Joker</desc>\n");
        gpxData.append("  </metadata>\n");
    }

    /**
     * add gpx footer to gpxData
     */
    private void addFooter() {
        gpxData.append("</gpx>\n");
    }

    /**
     * add waypoints to gpxData
     *
     * @param waypoints list of waypoints to be added
     */
    private void addWaypoints(List<Point> waypoints) {
        for (Point waypoint : waypoints) {
            gpxData.append(String.format("  <wpt lat=\"%s\" lon=\"%s\">\n",
                    waypoint.getLatitude(),
                    waypoint.getLongitude()));
            //noinspection StringConcatenationInsideStringBufferAppend
            gpxData.append("    <name>" + waypoint.getName() + "</name>\n");
            gpxData.append("  </wpt>\n");
        }
    }
}
