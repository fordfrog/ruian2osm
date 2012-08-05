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
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.postgis.PGbox2d;

/**
 * Loads nodes from OSM overpass API.
 *
 * @author fordfrog
 */
public class Loader {

    /**
     * Creates new instance of Loader.
     */
    private Loader() {
    }

    /**
     * Loads nodes from specified bounding box.
     *
     * @param bbox    bounding box
     * @param logFile log file writer
     *
     * @return loaded nodes
     */
    public static List<Node> loadNodes(final PGbox2d bbox,
            final Writer logFile) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<Node> nodes = new ArrayList<>();
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<Integer> loadedIds = new ArrayList<>();

        final int xTiles =
                (int) Math.ceil((bbox.getURT().x - bbox.getLLB().x) / 1);
        final int yTiles =
                (int) Math.ceil((bbox.getURT().y - bbox.getLLB().y) / 1);

        for (int xIndex = 0; xIndex < xTiles; xIndex++) {
            for (int yIndex = 0; yIndex < yTiles; yIndex++) {
                final PGbox2d curBBox = Utils.createPGbox2d(
                        bbox.getLLB().x + xIndex,
                        bbox.getLLB().y + yIndex,
                        Math.min(bbox.getLLB().x + xIndex + 1, bbox.getURT().x),
                        Math.min(bbox.getLLB().y + yIndex + 1, bbox.getURT().y));

                final URL url;

                try {
                    url = new URL("http://www.overpass-api.de/api/"
                            + "xapi_meta?node"
                            + "[addr:housenumber=*]"
                            + "[addr:country=CZ]"
                            + "[bbox=" + curBBox.getLLB().x + ','
                            + curBBox.getLLB().y + ',' + curBBox.getURT().x
                            + ',' + curBBox.getURT().y + ']');
                } catch (final MalformedURLException ex) {
                    throw new RuntimeException("Request URL is not valid", ex);
                }

                Utils.printToLog(logFile, MessageFormat.format("Loading nodes "
                        + "from boudning box {0} (tile {1},{2})", curBBox,
                        xIndex, yIndex));
                loadNodes(nodes, loadedIds, url);
                Utils.printToLog(logFile,
                        MessageFormat.format("Loaded {0} nodes", nodes.size()));
            }
        }

        return nodes;
    }

    /**
     * Loads nodes from specified URL.
     *
     * @param nodes     list for storing nodes
     * @param loadedIds list of node ids that were already loaded
     * @param url       download URL
     */
    private static void loadNodes(final List<Node> nodes,
            final List<Integer> loadedIds, final URL url) {
        try (final InputStream inputStream = url.openStream()) {
            final XMLInputFactory xMLInputFactory =
                    XMLInputFactory.newInstance();
            final XMLStreamReader reader =
                    xMLInputFactory.createXMLStreamReader(inputStream);

            while (reader.hasNext()) {
                final int event = reader.next();

                if (event == XMLStreamReader.START_ELEMENT) {
                    if ("osm".equals(reader.getLocalName())) {
                        loadNodesOsm(nodes, loadedIds, reader);
                    } else {
                        throw new RuntimeException(MessageFormat.format(
                                "Unsupported element ''{0}''",
                                reader.getLocalName()));
                    }
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(MessageFormat.format(
                    "Failed to download data from URL {0}", url), ex);
        } catch (final XMLStreamException ex) {
            throw new RuntimeException("Failed to read XML stream", ex);
        }
    }

    /**
     * Processes osm element.
     *
     * @param nodes     list for storing nodes
     * @param loadedIds list of node ids that were already loaded
     * @param reader    XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void loadNodesOsm(final List<Node> nodes,
            final List<Integer> loadedIds, final XMLStreamReader reader)
            throws XMLStreamException {
        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "note":
                        case "meta":
                            // just consume the element
                            break;
                        case "node":
                            loadNode(nodes, loadedIds, reader);
                            break;
                        default:
                            throw new RuntimeException(MessageFormat.format(
                                    "Unsupported element ''{0}''",
                                    reader.getLocalName()));
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "note":
                        case "meta":
                        case "node":
                            // just consume the end element
                            break;
                        case "osm":
                            return;
                        default:
                            throw new RuntimeException(MessageFormat.format(
                                    "Unsupported element ''{0}''",
                                    reader.getLocalName()));
                    }

                    break;
            }
        }
    }

    /**
     * Loads node information.
     *
     * @param nodes     list for storing nodes
     * @param loadedIds list of node ids that were already loaded
     * @param reader    XML stream reader
     *
     * @throws XMLStreamException Thrown if problem occurred while reading XML
     *                            stream.
     */
    private static void loadNode(final List<Node> nodes,
            final List<Integer> loadedIds, final XMLStreamReader reader)
            throws XMLStreamException {
        final Node node = new Node();

        node.setId(Integer.parseInt(reader.getAttributeValue(null, "id")));
        node.setLat(Double.parseDouble(reader.getAttributeValue(null, "lat")));
        node.setLon(Double.parseDouble(reader.getAttributeValue(null, "lon")));
        node.setVersion(
                Integer.parseInt(reader.getAttributeValue(null, "version")));
        node.setUser(reader.getAttributeValue(null, "user"));

        while (reader.hasNext()) {
            switch (reader.next()) {
                case XMLStreamReader.START_ELEMENT:
                    if ("tag".equals(reader.getLocalName())) {
                        node.getTags().put(reader.getAttributeValue(null, "k"),
                                reader.getAttributeValue(null, "v"));
                    } else {
                        throw new RuntimeException(MessageFormat.format(
                                "Unsupported element ''{0}''",
                                reader.getLocalName()));
                    }

                    break;
                case XMLStreamReader.END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "tag":
                            // just consume the end element
                            break;
                        case "node":
                            // this prevents adding the node to the list twice
                            if (!loadedIds.contains(node.getId())) {
                                nodes.add(node);
                                loadedIds.add(node.getId());
                            }

                            return;
                        default:
                            throw new RuntimeException(MessageFormat.format(
                                    "Unsupported element ''{0}''",
                                    reader.getLocalName()));
                    }
            }
        }
    }
}
