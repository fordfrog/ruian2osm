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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.postgis.PGbox2d;
import org.postgis.PGgeometry;
import org.postgis.Point;

/**
 * Loads nodes from RÚIAN database.
 *
 * @author fordfrog
 */
public class RuianLoader {

    /**
     * Creates new instance of RuianLoader.
     */
    private RuianLoader() {
    }

    /**
     * Loads nodes from RÚIAN database.
     *
     * @param bbox    bounding box
     * @param con     database connection
     * @param logFile log file writer
     *
     * @return
     */
    public static List<AddressNode> loadNodes(final PGbox2d bbox,
            final Connection con, final Writer logFile) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<AddressNode> nodes = new ArrayList<>();

        Utils.printToLog(logFile, MessageFormat.format(
                "Loading RÚIAN nodes from bounding box {0}", bbox));

        try (final PreparedStatement pstm = con.prepareStatement(
                        "SELECT am.kod, am.adrp_psc, am.cislo_domovni, "
                        + "am.cislo_orientacni_hodnota, "
                        + "am.cislo_orientacni_pismeno, "
                        + "st_transform(am.definicni_bod, 4326) point, "
                        + "am.item_timestamp, am.deleted, u.nazev street, "
                        + "ob.nazev city, so.typ_kod, vusc.nazev vusc_name "
                        + "FROM rn_adresni_misto am "
                        + "LEFT JOIN rn_ulice u "
                        + "ON am.ulice_kod = u.kod "
                        + "LEFT JOIN rn_stavebni_objekt so "
                        + "ON am.stavobj_kod = so.kod "
                        + "LEFT JOIN rn_cast_obce co "
                        + "ON so.cobce_kod = co.kod "
                        + "LEFT JOIN rn_obec ob "
                        + "ON co.obec_kod = ob.kod "
                        + "LEFT JOIN rn_okres ok "
                        + "ON ob.okres_kod = ok.kod "
                        + "LEFT JOIN rn_vusc vusc "
                        + "ON ok.vusc_kod = vusc.kod "
                        + "WHERE am.definicni_bod IS NOT NULL "
                        + "AND so.typ_kod IN (1, 2) "
                        + "AND st_contains(st_setsrid(?, 4326), "
                        + "st_transform(am.definicni_bod, 4326))")) {
            pstm.setFetchSize(100);
            pstm.setObject(1, bbox);

            try (final ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    final AddressNode node = new AddressNode();
                    nodes.add(node);

                    final PGgeometry geom = (PGgeometry) rs.getObject("point");
                    final Point point =
                            (Point) (geom == null ? null : geom.getGeometry());
                    final String conscriptionNumber =
                            rs.getString("cislo_domovni");
                    final String streetNumberValue =
                            rs.getString("cislo_orientacni_hodnota");
                    final String streetNumberLetter =
                            rs.getString("cislo_orientacni_pismeno");
                    final String streetNumber = streetNumberValue == null
                            && streetNumberLetter == null ? null
                            : (streetNumberValue == null ? ""
                            : streetNumberValue) + (streetNumberLetter == null
                            ? "" : streetNumberLetter);

                    node.setRefRuian(rs.getString("kod"));
                    node.setSourceAddr("ruian");
                    node.setSourceLoc("ruian");
                    node.setPoint(point);
                    node.setCountry("CZ");
                    node.setCity(rs.getString("city"));
                    node.setPostCode(rs.getString("adrp_psc"));
                    node.setStreet(rs.getString("street"));
                    node.setStreetNumber(streetNumber);
                    node.setIsIn(buildIsIn(node.getCity(), rs.getString(
                            "vusc_name"), node.getCountry()));
                    node.setDeleted(rs.getBoolean("deleted"));

                    switch (rs.getInt("typ_kod")) {
                        case 1:
                            node.setProvisionalNumber(conscriptionNumber);
                            node.setHouseNumber(conscriptionNumber
                                    + (streetNumber == null ? "" : '/'
                                    + streetNumber));
                            break;
                        case 2:
                            node.setConscriptionNumber(conscriptionNumber);
                            node.setHouseNumber("ev." + conscriptionNumber);
                            break;
                    }
                }
            }
        } catch (final SQLException ex) {
            throw new RuntimeException(
                    "Failed to load RÚIAN data from database", ex);
        }

        Utils.printToLog(logFile, MessageFormat.format(
                "Loaded {0} RÚIAN nodes", nodes.size()));

        return nodes;
    }

    /**
     * Builds "is in" string.
     *
     * @param city    city name
     * @param vusc    VÚSC name
     * @param country country code
     *
     * @return "is in" string
     */
    private static String buildIsIn(final String city, final String vusc,
            final String country) {
        return city + ", " + vusc + ", " + country;
    }
}
