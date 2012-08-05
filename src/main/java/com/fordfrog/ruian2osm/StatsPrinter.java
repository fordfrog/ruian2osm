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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.postgis.Point;

/**
 * Prints statistics about matching nodes.
 *
 * @author fordfrog
 */
public class StatsPrinter {

    /**
     * Creates new instance of StatsPrinter.
     */
    private StatsPrinter() {
    }

    /**
     * Prints statistics about matching nodes.
     *
     * @param pairs   match pairs
     * @param logFile log file writer
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    public static void printStats(final List<AddressNodePair> pairs,
            final Writer logFile) {
        Utils.printToLog(logFile, "Generating statistics...");

        final List<AddressNode> notMatchedOsm = new ArrayList<>();
        final List<AddressNode> notMatchedRuian = new ArrayList<>();
        final List<AddressNodePair> matchedPairs =
                new ArrayList<>(pairs.size());
        int matchedCount = 0;
        int matchedRuianDeleted = 0;
        int notMatchedRuianCount = 0;
        int notMatchedRuianDeletedCount = 0;
        int notMatchedOsmCount = 0;
        double totalDistance = 0;
        Double minDistance = null;
        Double maxDistance = null;
        AddressNodePair minDistancePair = null;
        AddressNodePair maxDistancePair = null;

        for (final AddressNodePair pair : pairs) {
            final AddressNode osmNode = pair.getOsm();
            final AddressNode ruianNode = pair.getRuian();

            if (osmNode != null && ruianNode != null) {
                matchedCount++;
                matchedPairs.add(pair);

                if (ruianNode.isDeleted()) {
                    matchedRuianDeleted++;
                }

                final double distance =
                        osmNode.getPoint().distance(ruianNode.getPoint());
                totalDistance += distance;

                if (minDistance == null || distance < minDistance) {
                    minDistance = distance;
                    minDistancePair = pair;
                }

                if (maxDistance == null || distance > maxDistance) {
                    maxDistance = distance;
                    maxDistancePair = pair;
                }
            } else if (osmNode != null) {
                notMatchedOsmCount++;
                notMatchedOsm.add(osmNode);
            } else if (ruianNode != null) {
                notMatchedRuianCount++;
                notMatchedRuian.add(ruianNode);

                if (ruianNode.isDeleted()) {
                    notMatchedRuianDeletedCount++;
                }
            }
        }

        Utils.printToLog(logFile,
                MessageFormat.format("Total nodes: {0}", pairs.size()));
        Utils.printToLog(logFile, MessageFormat.format("Total matched nodes: "
                + "{0} ({1} of RÚIAN nodes are marked as deleted)",
                matchedCount, matchedRuianDeleted));
        Utils.printToLog(logFile, MessageFormat.format(
                "Total unmatched OSM nodes: {0}", notMatchedOsmCount));
        Utils.printToLog(logFile, MessageFormat.format("Total unmatched RÚIAN "
                + "nodes: {0} ({1} of these are marked as deleted)",
                notMatchedRuianCount, notMatchedRuianDeletedCount));
        Utils.printToLog(logFile, MessageFormat.format(
                "Total unmatched RÚIAN nodes: {0}", notMatchedRuianCount));
        Utils.printToLog(logFile, MessageFormat.format(
                "Average matched node distance: {0,number,#.##########}",
                totalDistance / matchedCount));
        Utils.printToLog(logFile, MessageFormat.format(
                "Minimum matched node distance: {0,number,#.##########} "
                + "(RÚIAN: {1} OSM: {2})", minDistance,
                minDistancePair.getRuian().getAddressInfo(),
                minDistancePair.getOsm().getAddressInfo()));
        Utils.printToLog(logFile, MessageFormat.format(
                "Maximum matched node distance: {0,number,#.##########} "
                + "(RÚIAN: {1} OSM: {2})", maxDistance,
                maxDistancePair.getRuian().getAddressInfo(),
                maxDistancePair.getOsm().getAddressInfo()));

        Collections.sort(notMatchedRuian, new AddressNodeComparator());
        Collections.sort(notMatchedOsm, new AddressNodeComparator());
        Collections.sort(matchedPairs, new AddressNodePairComparator());

        Utils.printToLog(logFile, "Not matched RÚIAN addresses:");

        for (final AddressNode node : notMatchedRuian) {
            Utils.printToLog(logFile, node.getAddressInfo());
        }

        Utils.printToLog(logFile, "Not matched OSM addresses:");

        for (final AddressNode node : notMatchedOsm) {
            Utils.printToLog(logFile, node.getAddressInfo());
        }

        Utils.printToLog(logFile, "Matched addresses:");

        for (final AddressNodePair pair : matchedPairs) {
            final Point ruianPoint = pair.getRuian().getPoint();
            final Point osmPoint = pair.getOsm().getPoint();
            final Double distance;

            if (ruianPoint == null) {
                distance = null;
            } else {
                distance = ruianPoint.distance(osmPoint);
            }

            Utils.printToLog(logFile, MessageFormat.format(
                    "RÚIAN: {0} OSM: {1} (distance: {2,number,#.#########})",
                    pair.getRuian().getAddressInfo(),
                    pair.getOsm().getAddressInfo(), distance));
        }
    }
}
