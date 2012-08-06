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

        final List<AddressNodePair> changedCityList = new ArrayList<>();
        final List<AddressNodePair> changedStreetList = new ArrayList<>();
        final List<AddressNode> notMatchedOsm = new ArrayList<>();
        final List<AddressNode> notMatchedRuian = new ArrayList<>();
        final List<AddressNodePair> matchedPairs =
                new ArrayList<>(pairs.size());
        final HistogramContainer histogramContainer = new HistogramContainer();
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

                final double distance = Utils.round(
                        osmNode.getPoint().distance(ruianNode.getPoint()), 7);
                totalDistance += distance;

                if (minDistance == null || distance < minDistance) {
                    minDistance = distance;
                    minDistancePair = pair;
                }

                if (maxDistance == null || distance > maxDistance) {
                    maxDistance = distance;
                    maxDistancePair = pair;
                }

                histogramContainer.addCount((int) Math.ceil(distance * 100000));

                if (osmNode.getCity() != null
                        && !ruianNode.getCity().equals(osmNode.getCity())) {
                    changedCityList.add(pair);
                }

                if (ruianNode.getStreet() == null ? osmNode.getStreet() != null
                        : !ruianNode.getStreet().
                        equalsIgnoreCase(osmNode.getStreet())) {
                    changedStreetList.add(pair);
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
                "Average matched node distance: {0,number,#.#######}",
                totalDistance / matchedCount));
        Utils.printToLog(logFile, MessageFormat.format(
                "Minimum matched node distance: {0,number,#.#######} "
                + "(RÚIAN: {1} OSM: {2})", minDistance,
                minDistancePair.getRuian().getAddressInfo(),
                minDistancePair.getOsm().getAddressInfo()));
        Utils.printToLog(logFile, MessageFormat.format(
                "Maximum matched node distance: {0,number,#.#######} "
                + "(RÚIAN: {1} OSM: {2})", maxDistance,
                maxDistancePair.getRuian().getAddressInfo(),
                maxDistancePair.getOsm().getAddressInfo()));

        Utils.printToLog(logFile,
                "Histogram of distances between matched RÚIAN and OSM nodes:");

        for (int i = 0; i < histogramContainer.getCounts().length; i++) {
            Utils.printToLog(logFile, MessageFormat.format(
                    "{0,number,0.00000} - {1,number,0.00000}: {2}",
                    i * 0.00001, (i + 1) * 0.00001,
                    histogramContainer.getCounts()[i]));
        }

        Collections.sort(notMatchedRuian, new AddressNodeComparator());
        Collections.sort(notMatchedOsm, new AddressNodeComparator());
        Collections.sort(matchedPairs, new AddressNodePairComparator());
        Collections.sort(changedCityList, new AddressNodePairComparator());
        Collections.sort(changedStreetList, new AddressNodePairComparator());

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
            Utils.printToLog(logFile, MessageFormat.format(
                    "RÚIAN: {0} OSM: {1} (distance: {2,number,#.#######})",
                    pair.getRuian().getAddressInfo(),
                    pair.getOsm().getAddressInfo(), pair.getDistance()));
        }

        Utils.printToLog(logFile, "Addresses where city differs "
                + "(excluding cases where OSM node city is not set):");

        for (final AddressNodePair pair : changedCityList) {
            Utils.printToLog(logFile, MessageFormat.format(
                    "RÚIAN: {0} OSM: {1} (distance: {2,number,#.#######})",
                    pair.getRuian().getAddressInfo(),
                    pair.getOsm().getAddressInfo(), pair.getDistance()));
        }

        Utils.printToLog(logFile, "Addresses where street differs (comparison "
                + "is case insensitive):");

        for (final AddressNodePair pair : changedStreetList) {
            Utils.printToLog(logFile, MessageFormat.format(
                    "RÚIAN: {0} OSM: {1} (distance: {2,number,#.#######})",
                    pair.getRuian().getAddressInfo(),
                    pair.getOsm().getAddressInfo(), pair.getDistance()));
        }
    }
}
