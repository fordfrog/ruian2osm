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
 * Address node.
 *
 * @author fordfrog
 */
public class AddressNode extends Node {

    /**
     * Returns country.
     *
     * @return country code or null
     */
    public String getCountry() {
        return getTags().get("addr:country");
    }

    /**
     * Sets country.
     *
     * @param country country code
     */
    public void setCountry(final String country) {
        if (country == null || country.trim().isEmpty()) {
            getTags().remove("addr:country");
        } else {
            getTags().put("addr:country", country.trim());
        }
    }

    /**
     * Returns city.
     *
     * @return city or null
     */
    public String getCity() {
        return getTags().get("addr:city");
    }

    /**
     * Sets city.
     *
     * @param city city
     */
    public void setCity(final String city) {
        if (city == null || city.trim().isEmpty()) {
            getTags().remove("addr:city");
        } else {
            getTags().put("addr:city", city.trim());
        }
    }

    /**
     * Returns post code.
     *
     * @return post code or null
     */
    public String getPostCode() {
        return getTags().get("addr:postcode");
    }

    /**
     * Sets post code
     *
     * @param postCode post code
     */
    public void setPostCode(final String postCode) {
        if (postCode == null || postCode.trim().isEmpty()) {
            getTags().remove("addr:postcode");
        } else {
            getTags().put("addr:postcode", postCode.trim());
        }
    }

    /**
     * Returns street.
     *
     * @return street or null
     */
    public String getStreet() {
        return getTags().get("addr:street");
    }

    /**
     * Sets street.
     *
     * @param street street
     */
    public void setStreet(final String street) {
        if (street == null || street.trim().isEmpty()) {
            getTags().remove("addr:street");
        } else {
            getTags().put("addr:street", street);
        }
    }

    /**
     * Returns conscription number.
     *
     * @return conscription number or null
     */
    public String getConscriptionNumber() {
        return getTags().get("addr:conscriptionnumber");
    }

    /**
     * Sets concscription number.
     *
     * @param conscriptionNumber conscription number
     */
    public void setConscriptionNumber(final String conscriptionNumber) {
        if (conscriptionNumber == null || conscriptionNumber.trim().isEmpty()) {
            getTags().remove("addr:conscriptionnumber");
        } else {
            getTags().put("addr:conscriptionnumber", conscriptionNumber);
        }
    }

    /**
     * Returns provisional number.
     *
     * @return provisional number or null
     */
    public String getProvisionalNumber() {
        return getTags().get("addr:provisional");
    }

    /**
     * Sets provisional number.
     *
     * @param provisionalNumber provisional number
     */
    public void setProvisionalNumber(final String provisionalNumber) {
        if (provisionalNumber == null || provisionalNumber.trim().isEmpty()) {
            getTags().remove("addr:provisionalnumber");
        } else {
            getTags().put("addr:provisionalnumber", provisionalNumber);
        }
    }

    /**
     * Returns house number.
     *
     * @return house number or null
     */
    public String getHouseNumber() {
        return getTags().get("addr:housenumber");
    }

    /**
     * Sets house number
     *
     * @param houseNumber house number
     */
    public void setHouseNumber(final String houseNumber) {
        if (houseNumber == null || houseNumber.trim().isEmpty()) {
            getTags().remove("addr:housenumber");
        } else {
            getTags().put("addr:housenumber", houseNumber);
        }
    }

    /**
     * Returns street number.
     *
     * @return street number or null
     */
    public String getStreetNumber() {
        return getTags().get("addr:streetnumber");
    }

    /**
     * Sets street number.
     *
     * @param streetNumber street number
     */
    public void setStreetNumber(final String streetNumber) {
        if (streetNumber == null || streetNumber.trim().isEmpty()) {
            getTags().remove("addr:streetnumber");
        } else {
            getTags().put("addr:streetnumber", streetNumber);
        }
    }

    /**
     * Returns is in.
     *
     * @return is in or null
     */
    public String getIsIn() {
        return getTags().get("is_in");
    }

    /**
     * Sets is in.
     *
     * @param isIn is in
     */
    public void setIsIn(final String isIn) {
        if (isIn == null || isIn.trim().isEmpty()) {
            getTags().remove("is_in");
        } else {
            getTags().put("is_in", isIn);
        }
    }

    /**
     * Returns source of address.
     *
     * @return source of address or null
     */
    public String getSourceAddr() {
        return getTags().get("source:addr");
    }

    /**
     * Sets source of address.
     *
     * @param sourceAddr source of address
     */
    public void setSourceAddr(final String sourceAddr) {
        if (sourceAddr == null || sourceAddr.trim().isEmpty()) {
            getTags().remove("source:addr");
        } else {
            getTags().put("source:addr", sourceAddr);
        }
    }

    /**
     * Returns source of location.
     *
     * @return source of location or null
     */
    public String getSourceLoc() {
        return getTags().get("source:loc");
    }

    /**
     * Sets source of location.
     *
     * @param sourceLoc source of location
     */
    public void setSourceLoc(final String sourceLoc) {
        if (sourceLoc == null || sourceLoc.trim().isEmpty()) {
            getTags().remove("source:loc");
        } else {
            getTags().put("source:loc", sourceLoc);
        }
    }

    /**
     * Returns RÚIAN reference id.
     *
     * @return reference id or null
     */
    public String getRefRuian() {
        return getTags().get("ref:ruian");
    }

    /**
     * Sets RÚIAN reference id.
     *
     * @param refRuian reference id
     */
    public void setRefRuian(final String refRuian) {
        if (refRuian == null || refRuian.trim().isEmpty()) {
            getTags().remove("ref:ruian");
        } else {
            getTags().put("ref:ruian", refRuian);
        }
    }
}
