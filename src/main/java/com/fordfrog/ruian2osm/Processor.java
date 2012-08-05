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

import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import org.postgis.PGbox2d;
import org.postgresql.PGConnection;

/**
 * Processes OSM and RÚIAN data.
 *
 * @author fordfrog
 */
public class Processor {

    /**
     * Creates new instance of Processor.
     */
    private Processor() {
    }

    /**
     * Processes the input parameters.
     *
     * @param dbConnectionUrl database connection URL
     * @param bbox            bounding box (can be null)
     * @param printStats      whether statistics should be printed
     * @param update          whether OSM data update should be performed
     * @param logFile         log file
     */
    public static void process(final String dbConnectionUrl, final PGbox2d bbox,
            final boolean printStats, final boolean update,
            final Writer logFile) {
        try (final Connection con =
                        createConnection(dbConnectionUrl, logFile)) {
            final PGbox2d useBBox = bbox == null ? getBBox(con) : bbox;
            Utils.printToLog(logFile,
                    MessageFormat.format("Using bounding box {0}", useBBox));
            Loader.loadNodes(useBBox, logFile);
        } catch (final SQLException ex) {
            throw new RuntimeException(
                    "Problem occurred while communicating with database", ex);
        }
    }

    /**
     * Creates database connection and initializes Postgis objects.
     *
     * @param dbConnectionUrl database connection URL
     *
     * @return database connection
     */
    private static Connection createConnection(final String dbConnectionUrl,
            final Writer logFile) {
        Utils.printToLog(logFile, "Initializing database connection...");

        final Connection con;

        try {
            con = DriverManager.getConnection(dbConnectionUrl);
        } catch (final SQLException ex) {
            throw new RuntimeException("Cannot create database connection. Is "
                    + "the connection URL valid?", ex);
        }

        final PGConnection pGConnection = (PGConnection) con;

        try {
            pGConnection.addDataType(
                    "geometry", Class.forName("org.postgis.PGgeometry"));
            pGConnection.addDataType(
                    "box2d", Class.forName("org.postgis.PGbox2d"));
            pGConnection.addDataType(
                    "box3d", Class.forName("org.postgis.PGbox3d"));
        } catch (final ClassNotFoundException | SQLException ex) {
            throw new RuntimeException(
                    "Failed to initialize Postgis objects", ex);
        }

        return con;
    }

    /**
     * Returns bounding box using country borders.
     *
     * @param con database connection
     *
     * @return bounding box
     */
    private static PGbox2d getBBox(final Connection con) {
        try (final Statement stm = con.createStatement();
                final ResultSet rs = stm.executeQuery(
                        "SELECT st_extent(st_transform(hranice, 4326)) "
                        + "FROM rn_stat WHERE nuts_lau = 'CZ'")) {
            if (!rs.next()) {
                throw new RuntimeException("Table rn_stat does not contain "
                        + "borders for CZ. Cannot determine bounding box.");
            }

            return (PGbox2d) rs.getObject(1);
        } catch (final SQLException ex) {
            throw new RuntimeException(
                    "Failed to get country bounding box", ex);
        }
    }
}
