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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * abstract parent class for various exporters
 * requiring export() to be individually implemented in derived exporters
 * those implementations may use sendFileViaIntent() of this class to perform the intent sending
 * part
 * <p>
 * By convention derived classes must be named "XyzExporter" for format "xyz" if you want to create
 * an instance via ExporterFactory
 */
abstract class Exporter {

    /**
     * base directory for temporary files to be used by this and derived exporters, initialized at
     * class creation
     */
    final File baseDirForTemporaryFiles;

    /**
     * the app's main context required for sending intents, accessing resources etc.
     */
    final Context context;

    /**
     * set of export parameters impacting the kind of export
     */
    private final ExportSettings exportSettings;

    /**
     * constructor providing context and telling whether to send ACTION_VIEW or ACTION_SEND intent
     *
     * @param context        context to be used
     * @param exportSettings export parameters
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    Exporter(Context context, ExportSettings exportSettings) {
        this.context = context;
        this.exportSettings = exportSettings;
        baseDirForTemporaryFiles = new File(context.getFilesDir(), "tmp");
        baseDirForTemporaryFiles.mkdirs();
    }

    /**
     * export interface definition
     *
     * @param waypoints waypoints to be exported
     */
    abstract void export(List<Point> waypoints);

    /**
     * send file via intent to another activity using fileprovider for granting access rights
     * depending on constructor parameter, the intent will be sent as ACTION_VIEW or ACTION_SEND
     *
     * @param file     file to be provided to the other activity
     * @param mimeType mimeType of the file
     */
    void sendFileViaIntent(File file, String mimeType) {
        try {
            // for being able to share a file via intent, shared dir must be contained in
            // project's res/xml/filepaths.xml
            final File sharedDir;
            sharedDir = new File(context.getFilesDir(), "shared");
            //noinspection ResultOfMethodCallIgnored
            sharedDir.mkdirs();

            // copy content to be shared to shared directory
            File out = new File(sharedDir, file.getName());
            FileHelper.writeContentToFile(out, new FileInputStream(file));

            Intent intent = new Intent();

            // for providing read access, provide it via FileProvider
            Uri sharedFileUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    out);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // start activity via intent view or send
            if (!exportSettings.isWantsToShare()) {
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(sharedFileUri, exportSettings.isUseMimeType() ? mimeType : "*/*");
            } else {
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, sharedFileUri);
                intent.setType(exportSettings.isUseMimeType() ? mimeType : "*/*");
            }

            // if app was specified and is served, restrict intent to this app
            final String appName = exportSettings.getAppName();
            final String packageName = findPackageNameForAppName(intent, appName);
            if (!packageName.isEmpty()) {
                intent.setPackage(packageName);
            }
            context.startActivity(intent);
        } catch (IOException e) {
            throw new ExportException(context.getString(R.string.string_file_operation_failed));
        } catch (Exception e) {
            throw new ExportException(context.getString(R.string.string_sending_intent_failed));
        }
    }

    /**
     * method for finding which package containing appName would serve the intent
     *
     * @param intent  intent to be used
     * @param appName app name to be searched in package list serving the intent
     * @return first matching package name
     */
    private String findPackageNameForAppName(Intent intent, String appName) {
        String packageName = "";
        if (!appName.isEmpty()) {
            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> infoList = manager.queryIntentActivities(intent, 0);
            for (ResolveInfo info : infoList) {
                if (info.activityInfo.packageName.contains(appName + ".")) {
                    packageName = info.activityInfo.packageName;
                    break;
                }
            }
        }

        return packageName;
    }

}
