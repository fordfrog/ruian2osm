/**
 * Copyright 2012 Miroslav Šulc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fordfrog.ruian2osm;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.postgis.PGbox2d;

/**
 * Various utility methods.
 *
 * @author fordfrog
 */
public class Utils {

    /**
     * Creates new instance of Utils.
     */
    private Utils() {
    }

    /**
     * Prints message to log.
     *
     * @param logFile log file
     * @param message message
     */
    public static void printToLog(final Writer logFile, final String message) {
        try {
            logFile.write(message);
            logFile.write('\n');
            logFile.flush();
        } catch (final IOException ex) {
            throw new RuntimeException("Failed to write to log", ex);
        }
    }

    /**
     * Creates new PGbox2d.
     *
     * @param llbx LLB x
     * @param llby LLB y
     * @param urtx URT x
     * @param urty URT y
     *
     * @return created box
     */
    public static PGbox2d createPGbox2d(final double llbx, final double llby,
            final double urtx, final double urty) {
        try {
            return new PGbox2d(MessageFormat.format(
                    "SRID=4326;BOX({0} {1},{2} {3})", Double.toString(llbx),
                    Double.toString(llby), Double.toString(urtx),
                    Double.toString(urty)));
        } catch (final SQLException ex) {
            throw new RuntimeException("Failed to create PGbox2d", ex);
        }
    }
}
