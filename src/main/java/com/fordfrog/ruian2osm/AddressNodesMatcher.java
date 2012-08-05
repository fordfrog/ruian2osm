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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Matches address nodes from OSM to RÚIAN nodes.
 *
 * @author fordfrog
 */
public class AddressNodesMatcher {

    /**
     * Creates new instance of AddressNodesMatcher.
     */
    private AddressNodesMatcher() {
    }

    /**
     * Attempts to match RÚIAN and OSM nodes using various common
     * characteristics.
     *
     * @param ruianNodes       list of RÚIAN nodes
     * @param osmNodes         list of OSM nodes
     * @param matchMaxDistance maximum distance for which to accept address
     *                         nodes match
     * @param logFile          log file writer
     *
     * @return list of address node pairs
     */
    public static List<AddressNodePair> matchNodes(
            final List<AddressNode> ruianNodes,
            final List<AddressNode> osmNodes, final double matchMaxDistance,
            final Writer logFile) {
        Utils.printToLog(logFile, MessageFormat.format("Maximum allowed "
                + "distance for matching two nodes is {0,number,#.#######}",
                matchMaxDistance));

        final List<AddressNode> ruianLeftNodes = new ArrayList<>(ruianNodes);
        final List<AddressNode> osmLeftNodes = new ArrayList<>(osmNodes);
        final List<AddressNodePair> result =
                new ArrayList<>(Math.max(ruianNodes.size(), osmNodes.size()));

        matchByRuianId(result, ruianLeftNodes, osmLeftNodes, logFile);
        matchByFullAddress(result, ruianLeftNodes, osmLeftNodes,
                matchMaxDistance, logFile);
        matchByStreet(result, ruianLeftNodes, osmLeftNodes, matchMaxDistance,
                logFile);
        matchByNumber(result, ruianLeftNodes, osmLeftNodes, matchMaxDistance,
                logFile);

        Utils.printToLog(logFile, MessageFormat.format(
                "Total matched nodes: {0}", result.size()));
        Utils.printToLog(logFile, MessageFormat.format("Total unmatched nodes "
                + "- RÚIAN: {0}, OSM: {1}", ruianLeftNodes.size(),
                osmLeftNodes.size()));

        for (final AddressNode node : ruianLeftNodes) {
            result.add(new AddressNodePair(node, null));
        }

        for (final AddressNode node : osmLeftNodes) {
            result.add(new AddressNodePair(null, node));
        }

        return result;
    }

    /**
     * Matches nodes by RÚIAN id.
     *
     * @param result         list of result pairs
     * @param ruianLeftNodes unresolved RÚIAN nodes
     * @param osmLeftNodes   unresolved OSM nodes
     * @param logFile        log file writer
     */
    private static void matchByRuianId(List<AddressNodePair> result,
            List<AddressNode> ruianLeftNodes,
            List<AddressNode> osmLeftNodes, final Writer logFile) {
        Utils.printToLog(logFile, "Matching nodes by RÚIAN id...");

        final Map<String, AddressNode> ruianIdMap =
                new HashMap<>(ruianLeftNodes.size());

        for (final AddressNode node : ruianLeftNodes) {
            ruianIdMap.put(node.getRefRuian(), node);
        }

        int count = 0;

        for (int i = osmLeftNodes.size() - 1; i >= 0; i--) {
            final AddressNode osmNode = osmLeftNodes.get(i);

            if (osmNode.getRefRuian() == null) {
                continue;
            }

            final AddressNode ruianNode = ruianIdMap.get(osmNode.getRefRuian());

            if (ruianNode == null) {
                continue;
            }

            result.add(new AddressNodePair(ruianNode, osmNode));
            osmLeftNodes.remove(i);
            ruianLeftNodes.remove(ruianNode);
            ruianIdMap.remove(ruianNode.getRefRuian());
            count++;
        }

        Utils.printToLog(
                logFile, MessageFormat.format("{0} nodes matched", count));
    }

    /**
     * Matches nodes by full address (city, street, conscription
     * number/provisional number).
     *
     * @param result           list of result pairs
     * @param ruianLeftNodes   unresolved RÚIAN nodes
     * @param osmLeftNodes     unresolved OSM nodes
     * @param matchMaxDistance maximum distance for which to accept address
     *                         nodes match
     * @param logFile          log file writer
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private static void matchByFullAddress(List<AddressNodePair> result,
            List<AddressNode> ruianLeftNodes,
            List<AddressNode> osmLeftNodes, final double matchMaxDistance,
            final Writer logFile) {
        Utils.printToLog(logFile, "Matching nodes by full address...");

        // maps city, street, address nodes
        final Map<String, Map<String, List<AddressNode>>> ruianMap =
                new HashMap<>();

        for (final AddressNode node : ruianLeftNodes) {
            Map<String, List<AddressNode>> streets =
                    ruianMap.get(node.getCity());

            if (streets == null) {
                streets = new HashMap<>();
                ruianMap.put(node.getCity(), streets);
            }

            List<AddressNode> nodes = streets.get(node.getStreet());

            if (nodes == null) {
                nodes = new ArrayList<>();
                streets.put(node.getStreet(), nodes);
            }

            nodes.add(node);
        }

        int count = 0;

        for (int i = osmLeftNodes.size() - 1; i >= 0; i--) {
            final AddressNode osmNode = osmLeftNodes.get(i);

            final Map<String, List<AddressNode>> streets =
                    ruianMap.get(osmNode.getCity());

            if (streets == null) {
                continue;
            }

            final List<AddressNode> nodes = streets.get(osmNode.getStreet());

            if (nodes == null) {
                continue;
            }

            if (osmNode.getRefRuian() == null) {
                continue;
            }

            final AddressNode matchedRuianNode = getClosestNodeByNumber(
                    osmNode, nodes, matchMaxDistance, logFile);

            if (matchedRuianNode == null) {
                continue;
            }

            result.add(new AddressNodePair(matchedRuianNode, osmNode));
            osmLeftNodes.remove(i);
            ruianLeftNodes.remove(matchedRuianNode);
            nodes.remove(matchedRuianNode);
            count++;
        }

        Utils.printToLog(
                logFile, MessageFormat.format("{0} nodes matched", count));
    }

    /**
     * Matches nodes by street and conscription/provisional number.
     *
     * @param result           list of result pairs
     * @param ruianLeftNodes   unresolved RÚIAN nodes
     * @param osmLeftNodes     unresolved OSM nodes
     * @param matchMaxDistance maximum distance for which to accept address
     *                         nodes match
     * @param logFile          log file writer
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private static void matchByStreet(final List<AddressNodePair> result,
            final List<AddressNode> ruianLeftNodes,
            final List<AddressNode> osmLeftNodes, final double matchMaxDistance,
            final Writer logFile) {
        Utils.printToLog(logFile, "Matching nodes by street...");

        final Map<String, List<AddressNode>> ruianMap = new HashMap<>();

        for (final AddressNode node : ruianLeftNodes) {
            List<AddressNode> nodes = ruianMap.get(node.getStreet());

            if (nodes == null) {
                nodes = new ArrayList<>();
                ruianMap.put(node.getStreet(), nodes);
            }

            nodes.add(node);
        }

        int count = 0;

        for (int i = osmLeftNodes.size() - 1; i >= 0; i--) {
            final AddressNode osmNode = osmLeftNodes.get(i);

            final List<AddressNode> nodes = ruianMap.get(osmNode.getStreet());

            if (nodes == null) {
                continue;
            }

            final AddressNode matchedRuianNode = getClosestNodeByNumber(
                    osmNode, nodes, matchMaxDistance, logFile);

            if (matchedRuianNode == null) {
                continue;
            }

            result.add(new AddressNodePair(matchedRuianNode, osmNode));
            osmLeftNodes.remove(i);
            ruianLeftNodes.remove(matchedRuianNode);
            nodes.remove(matchedRuianNode);
            count++;
        }

        Utils.printToLog(
                logFile, MessageFormat.format("{0} nodes matched", count));
    }

    /**
     * Matches nodes by conscription/provisional number.
     *
     * @param result           list of result pairs
     * @param ruianLeftNodes   unresolved RÚIAN nodes
     * @param osmLeftNodes     unresolved OSM nodes
     * @param matchMaxDistance maximum distance for which to accept address
     *                         nodes match
     * @param logFile          log file writer
     */
    private static void matchByNumber(final List<AddressNodePair> result,
            final List<AddressNode> ruianLeftNodes,
            final List<AddressNode> osmLeftNodes, final double matchMaxDistance,
            final Writer logFile) {
        Utils.printToLog(logFile,
                "Matching nodes by conscription/provisional number...");

        int count = 0;

        for (int i = osmLeftNodes.size() - 1; i >= 0; i--) {
            final AddressNode osmNode = osmLeftNodes.get(i);

            final AddressNode matchedRuianNode = getClosestNodeByNumber(
                    osmNode, ruianLeftNodes, matchMaxDistance, logFile);

            if (matchedRuianNode == null) {
                continue;
            }

            result.add(new AddressNodePair(matchedRuianNode, osmNode));
            osmLeftNodes.remove(i);
            ruianLeftNodes.remove(matchedRuianNode);
            count++;
        }

        Utils.printToLog(
                logFile, MessageFormat.format("{0} nodes matched", count));
    }

    /**
     * Compares OSM node conscription/provisional number to the RÚIAN nodes and
     * returns the closest one that matches by any of the numbers, unless it is
     * more distant than maximum allowed distance for matching.
     *
     * @param osmNode          OSM node
     * @param ruianNodes       list of RÚIAN nodes
     * @param matchMaxDistance maximum allowed distance for matching
     * @param logFile          log file writer
     *
     * @return matched RÚIAN node or null
     */
    private static AddressNode getClosestNodeByNumber(final AddressNode osmNode,
            final List<AddressNode> ruianNodes, final double matchMaxDistance,
            final Writer logFile) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<AddressNode> matchedRuianNodes = new ArrayList<>();

        for (final AddressNode ruianNode : ruianNodes) {
            if (ruianNode.getHouseNumber().equals(osmNode.getHouseNumber())
                    || ruianNode.getConscriptionNumber() != null
                    && ruianNode.getConscriptionNumber().
                    equals(osmNode.getConscriptionNumber())
                    || ruianNode.getProvisionalNumber() != null
                    && ruianNode.getProvisionalNumber().
                    equals(osmNode.getProvisionalNumber())) {
                matchedRuianNodes.add(ruianNode);
            }
        }

        if (matchedRuianNodes.isEmpty()) {
            return null;
        }

        AddressNode matchedRuianNode = matchedRuianNodes.get(0);
        double minDistance = Utils.round(
                matchedRuianNode.getPoint().distance(osmNode.getPoint()), 7);

        for (int j = 1; j < matchedRuianNodes.size(); j++) {
            final AddressNode ruianNode = matchedRuianNodes.get(j);
            final double curDistance = Utils.round(
                    ruianNode.getPoint().distance(osmNode.getPoint()), 7);

            if (curDistance < minDistance) {
                matchedRuianNode = ruianNode;
                minDistance = curDistance;
            }
        }

        if (minDistance > matchMaxDistance) {
            Utils.printToLog(logFile, MessageFormat.format("Matched RÚIAN node "
                    + "{0} and OSM node {1} but their distance "
                    + "{2,number,#.#######} is over the limit "
                    + "{3,number,#.#######}", matchedRuianNode.getAddressInfo(),
                    osmNode.getAddressInfo(), minDistance, matchMaxDistance));

            return null;
        }

        return matchedRuianNode;
    }
}
