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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * class providing file helper functions
 */
final class FileHelper {

    /**
     * helper function for writing content to file, also available for derived classes
     *
     * @param context application context (for accessing resource strings)
     * @param file    file to write to
     * @param content string content to be written to file
     */
    static void writeContentToFile(@NonNull Context context,
                                   @NonNull File file,
                                   @NonNull String content) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.close();
        } catch (IOException e) {
            throw new ExportException(context.getString(R.string.string_file_operation_failed));
        }
    }

    /**
     * helper function for writing content to file, also available for derived classes
     *
     * @param context application context (for accessing resource strings)
     * @param file    file to write to
     * @param content inputstream content to be written to file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void writeContentToFile(@NonNull Context context,
                                   @NonNull File file,
                                   @NonNull InputStream content) {
        final int bufferSize = 1024;
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[bufferSize];
            int numberOfBytesRead;
            while ((numberOfBytesRead = content.read(buffer, 0, bufferSize)) != -1) {
                out.write(buffer, 0, numberOfBytesRead);
            }
            out.close();
        } catch (IOException e) {
            throw new ExportException(context.getString(R.string.string_file_operation_failed));
        }
    }

    /**
     * helper function for zipping
     *
     * @param context  application context (for accessing resource strings)
     * @param zipFile  resulting zip file to be created
     * @param fileList files to be zipped into zip file
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void zipContentToFile(@NonNull Context context,
                                 @NonNull File zipFile,
                                 @NonNull List<File> fileList) {
        final int bufferSize = 1024;
        try {

            zipFile.createNewFile();
            FileOutputStream destination = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(destination));
            byte data[] = new byte[bufferSize];

            for (File file : fileList) {
                FileInputStream in = new FileInputStream(file);
                BufferedInputStream origin = new BufferedInputStream(in, bufferSize);

                ZipEntry entry = new ZipEntry(
                        file.getPath().substring(file.getPath().lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int numberOfBytesRead;
                while ((numberOfBytesRead = origin.read(data, 0, bufferSize)) != -1) {
                    out.write(data, 0, numberOfBytesRead);
                }
                origin.close();
            }

            out.close();
            destination.close();

        } catch (IOException e) {
            throw new ExportException(context.getString(R.string.string_kmz_export_failed));
        }
    }

}