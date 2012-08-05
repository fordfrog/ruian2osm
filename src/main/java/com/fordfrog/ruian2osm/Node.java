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
import org.postgis.Point;

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
     * Location of the node.
     */
    private Point point;
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
     * Getter for {@link #point}.
     *
     * @return {@link #point}
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Setter for {@link #point}.
     *
     * @param point {@link #point}
     */
    public void setPoint(final Point point) {
        this.point = point;
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

    /**
     * Returns value of bot:ruian tag.
     *
     * @return value of bot:ruian tag
     */
    public String getBotRuian() {
        return getTags().get("bot:ruian");
    }

    /**
     * Sets value of value of bot:ruian tag.
     *
     * @param botRuian new value
     */
    public void setBotRuian(final String botRuian) {
        if (botRuian == null || botRuian.trim().isEmpty()) {
            getTags().remove("bot:ruian");
        } else {
            getTags().put("bot:ruian", botRuian.trim());
        }
    }
}
