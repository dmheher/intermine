package org.intermine.util;

/*
 * Copyright (C) 2002-2004 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.OutputStream;
import java.io.PrintStream;

import java.util.List;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Utility methods for dealing with text files.
 *
 * @author Kim Rutherford
 */

public abstract class TextFileUtil
{
    /**
     * Write a list of lists using tab characters to delimit the fields.
     * @param os the OutputStream to write to
     * @param listOfLists the table to write
     * @param columnOrder the real order of the column in the output - a map from the column index
     * in the output to the column index in the listOfLists
     * @param columnVisible an array mapping from columns in listOfLists to their visibility
     */
    public static void writeTabDelimitedTable(OutputStream os, List listOfLists,
                                              int [] columnOrder, boolean [] columnVisible) {
        writeDelimitedTable(os, listOfLists, columnOrder, columnVisible, '\t');
    }

    /**
     * Write a list of lists using comma characters to delimit the fields.
     * @param os the OutputStream to write to
     * @param listOfLists the table to write
     * @param columnOrder the real order of the column in the output - a map from the column index
     * in the output to the column index in the listOfLists
     * @param columnVisible an array mapping from columns in listOfLists to their visibility
     */
    public static void writeCSVTable(OutputStream os, List listOfLists,
                                     int [] columnOrder, boolean [] columnVisible) {
        writeDelimitedTable(os, listOfLists, columnOrder, columnVisible, ',');
    }

    /**
     * Write a list of lists using the given delimiter character to delimit the fields.
     * @param os the OutputStream to write to
     * @param listOfLists the table to write
     * @param columnOrder the real order of the column in the output - a map from the column index
     * in the output to the column index in the listOfLists
     * @param columnVisible an array mapping from columns in listOfLists to their visibility
     * @param delimiter the character to use to separate the fields in the output
     */
    public static void writeDelimitedTable(OutputStream os, List listOfLists,
                                           int [] columnOrder, boolean [] columnVisible,
                                           char delimiter) {
        PrintStream printStream = new PrintStream(os);

        String delimiters = "" + delimiter;

        // a count of the columns that are invisble - used to get the correct columnIndex
        int invisibleColumns = 0;

        for (int columnIndex = 0; columnIndex < columnVisible.length; columnIndex++) {
            if (!columnVisible[columnIndex]) {
                invisibleColumns++;
            }
        }

        Iterator rowIterator = listOfLists.iterator();
        while (rowIterator.hasNext()) {
            List row = (List) rowIterator.next();

            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                int realColumnIndex = columnOrder[columnIndex];

                Object o = row.get(realColumnIndex);

                if (!columnVisible[columnIndex]) {
                    continue;
                }

                if (o instanceof Number) {
                    writeUnQuoted(printStream, o);
                } else {
                    writeQuoted(printStream, o);
                }

                if (columnIndex < row.size () - 1 - invisibleColumns) {
                    printStream.print(delimiter);
                }
                
            }

            printStream.println();
        }

        printStream.flush();
    }

    private static final String QUOTE = "\"";

    /**
     * Write an Object as a String to an OutputStream with quoting.  Any double-quote characters
     * are quoted doubling them.  The output will be surrounded by double quotes.
     * ie.  fred"eric -> "fred""eric"
     * @param printStream the PrintStream to write to
     * @param o the Object to write
     */
    public static void writeQuoted(PrintStream printStream, Object o) {
        // don't use toString() in case o is null
        String objectString = "" + o;

        StringBuffer buffer = new StringBuffer();

        final StringTokenizer tokeniser =
            new StringTokenizer (objectString, QUOTE, true);

        buffer.append(QUOTE);

        while (tokeniser.hasMoreTokens ()) {
            final String tokenValue = tokeniser.nextToken ();

            if (tokenValue.equals(QUOTE)) {
                // quotes are quoted by doubling
                buffer.append(tokenValue);
                buffer.append(tokenValue);
            } else {
                buffer.append(tokenValue);
            }
        }

        buffer.append('"');

        printStream.print(buffer);
    }

    /**
     * Write an Object as a String to an OutputStream with quoting special characters
     * @param printStream the PrintStream to write to
     * @param o the Object to write
     */
    public static void writeUnQuoted(PrintStream printStream, Object o) {
        printStream.print(o.toString());
    }
}
