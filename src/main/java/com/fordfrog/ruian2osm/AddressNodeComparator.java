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

import java.util.Comparator;
import java.util.Objects;

/**
 * Compares two address nodes.
 *
 * @author fordfrog
 */
public class AddressNodeComparator implements Comparator<AddressNode> {

    @Override
    public int compare(final AddressNode o1, final AddressNode o2) {
        if (!Objects.equals(o1.getCity(), o2.getCity())) {
            return compare(o1.getCity(), o2.getCity());
        } else if (!Objects.equals(o1.getStreet(), o2.getStreet())) {
            return compare(o1.getStreet(), o2.getStreet());
        } else {
            return compare(o1.getHouseNumber(), o2.getHouseNumber());
        }
    }

    /**
     * Compares two strings no matter if they are null or not.
     *
     * @param string1 string
     * @param string2 string
     *
     * @return comparison result
     */
    private int compare(final String string1, final String string2) {
        if (string1 == null) {
            return -1;
        } else if (string2 == null) {
            return 1;
        } else {
            return string1.compareToIgnoreCase(string2);
        }
    }
}
