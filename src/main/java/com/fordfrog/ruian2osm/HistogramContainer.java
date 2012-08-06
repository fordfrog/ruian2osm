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
 * Helper class for filling histogram.
 *
 * @author fordfrog
 */
public class HistogramContainer {

    /**
     * Array of counts for each index.
     */
    private int[] counts;

    /**
     * Adds count to specified index.
     *
     * @param index index
     */
    public void addCount(final int index) {
        if (counts == null) {
            counts = new int[index + 1];
        } else if (counts.length <= index) {
            final int[] newCounts = new int[index + 1];
            System.arraycopy(counts, 0, newCounts, 0, counts.length);
            counts = newCounts;
            counts[index] = 1;
        } else {
            counts[index]++;
        }
    }

    /**
     * Getter for {@link #counts}.
     *
     * @return {@link #counts}
     */
    public int[] getCounts() {
        return counts;
    }
}
