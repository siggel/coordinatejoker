/*
 * Copyright (c) 2018 by bubendorf <markus@bubendorf.ch> and siggel <siggel-apps@gmx.de>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides a List<Integer> based on some input text.<br/>
 * The input text may look like "1,3,5,6", "1-5,7", "2-5,7-12,33-42"
 * or "1-99#2".
 * The resulting list of integers is ordered in ascending order.
 */
class IntegerRange {

    // Regex used to split the input text in parts.
    private static final String SPLIT_REGEX = ",";

    // Character used for from-to ranges
    private static final char FROM_TO_CHAR = '-';

    // Character used to separate the step size
    private static final char STEP_CHAR = '#';

    static List<Integer> getValues(Context context, String text) {
        try {
            text = text.trim(); // avoid string containing just blanks
            Set<Integer> numbers = new HashSet<>();
            if (text.length() > 0) {
                String[] splits = text.split(SPLIT_REGEX);
                for (String split : splits) {
                    String trimmedSplit = split.trim();
                    int dashIndex = trimmedSplit.indexOf(FROM_TO_CHAR);
                    switch (dashIndex) {
                        case 0:
                            throw new ParseException(context != null ?
                                    context.getString(R.string.string_parse_error_negative_number) :
                                    "");
                        case -1:
                            // No dash found. Looks like a single number ==> take it
                            numbers.add(Integer.parseInt(trimmedSplit));
                            break;
                        default:
                            // A from-to range
                            int step = 1;
                            int from = Integer.parseInt(trimmedSplit.substring(0, dashIndex).trim());
                            int to;
                            int atIndex = trimmedSplit.indexOf(STEP_CHAR);
                            if (atIndex == -1) {
                                to = Integer.parseInt(trimmedSplit.substring(dashIndex + 1).trim());
                            } else {
                                to = Integer.parseInt(trimmedSplit.substring(dashIndex + 1, atIndex).trim());
                                step = Integer.parseInt(trimmedSplit.substring(atIndex + 1).trim());
                            }
                            if (from > to) {
                                throw new ParseException(context != null ?
                                        context.getString(R.string.string_parse_error_from_bigger_than_to) :
                                        "");
                            }
                            if (step < 0) {
                                throw new ParseException(context != null ?
                                        context.getString(R.string.string_parse_error_negative_step_size) :
                                        "");
                            }
                            // We now have the from, the to and the step and generate the single numbers.
                            for (int num = from; num <= to; num += step) {
                                numbers.add(num);
                            }
                            break;
                    }
                }
            }

            List<Integer> values = new ArrayList<>(numbers);
            Collections.sort(values);
            return values;
        } catch (NumberFormatException e) {
            throw new ParseException(context != null ?
                    context.getString(R.string.string_parse_error_non_integer) :
                    "");
        }
    }
}
