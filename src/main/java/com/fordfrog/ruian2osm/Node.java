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

import java.util.HashMap;
import java.util.Map;

/**
 * OSM node.
 *
 * @author fordfrog
 */
public class Node {

    /**
     * Map of tag names and corresponding values.
     */
    private final Map<String, String> tags = new HashMap<>(10);
    /**
     * Id of the node.
     */
    private int id;
    /**
     * Latitude of the node.
     */
    private double lat;
    /**
     * Longitude of the node.
     */
    private double lon;
    /**
     * Version of the node.
     */
    private int version;
    /**
     * Name of user that last modified the node.
     */
    private String user;

    /**
     * Getter for {@link #id}.
     *
     * @return {@link #id}
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for {@link #id}.
     *
     * @param id {@link #id}
     */
    public void setId(final int id) {
        this.id = id;
    }

    /**
     * Getter for {@link #lat}.
     *
     * @return {@link #lat}
     */
    public double getLat() {
        return lat;
    }

    /**
     * Setter for {@link #lat}.
     *
     * @param lat {@link #lat}
     */
    public void setLat(final double lat) {
        this.lat = lat;
    }

    /**
     * Getter for {@link #lon}.
     *
     * @return {@link #lon}
     */
    public double getLon() {
        return lon;
    }

    /**
     * Setter for {@link #lon}.
     *
     * @param lon {@link #lon}
     */
    public void setLon(final double lon) {
        this.lon = lon;
    }

    /**
     * Getter for {@link #version}.
     *
     * @return {@link #version}
     */
    public int getVersion() {
        return version;
    }

    /**
     * Setter for {@link #version}.
     *
     * @param version {@link #version}
     */
    public void setVersion(final int version) {
        this.version = version;
    }

    /**
     * Getter for {@link #user}.
     *
     * @return {@link #user}
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter for {@link #user}.
     *
     * @param user {@link #user}
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * Getter for {@link #tags}.
     *
     * @return {@link #tags}
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public Map<String, String> getTags() {
        return tags;
    }
}
