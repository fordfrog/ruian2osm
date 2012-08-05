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

/**
 * Pair of RÚIAN and OSM address nodes that match each other.
 *
 * @author fordfrog
 */
public class AddressNodePair {

    /**
     * RÚIAN address node.
     */
    private AddressNode ruian;
    /**
     * OSM address node.
     */
    private AddressNode osm;

    /**
     * Creates new instance of AddressNodePair.
     */
    public AddressNodePair() {
    }

    /**
     * Creates new instance of AddressNodePair.
     *
     * @param ruian {@link #ruian}
     * @param osm   {@link #osm}
     */
    public AddressNodePair(final AddressNode ruian, final AddressNode osm) {
        this.ruian = ruian;
        this.osm = osm;
    }

    /**
     * Getter for {@link #ruian}.
     *
     * @return {@link #ruian}
     */
    public AddressNode getRuian() {
        return ruian;
    }

    /**
     * Setter for {@link #ruian}.
     *
     * @param ruian {@link #ruian}
     */
    public void setRuian(final AddressNode ruian) {
        this.ruian = ruian;
    }

    /**
     * Getter for {@link #osm}.
     *
     * @return {@link #osm}
     */
    public AddressNode getOsm() {
        return osm;
    }

    /**
     * Setter for {@link #osm}.
     *
     * @param osm {@link #osm}
     */
    public void setOsm(final AddressNode osm) {
        this.osm = osm;
    }
}
