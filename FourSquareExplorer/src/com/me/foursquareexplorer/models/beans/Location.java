
package com.me.foursquareexplorer.models.beans;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Location {

    @Expose
    private String address;
    @Expose
    private double lat;
    @Expose
    private double lng;
    @Expose
    private int distance;
    @Expose
    private String cc;
    @Expose
    private String city;
    @Expose
    private String state;
    @Expose
    private String country;
    @Expose
    private List<String> formattedAddress = new ArrayList<String>();

    /**
     * 
     * @return
     *     The address
     */
    public String getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * 
     * @return
     *     The lng
     */
    public double getLng() {
        return lng;
    }

    /**
     * 
     * @param lng
     *     The lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * 
     * @return
     *     The distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * 
     * @return
     *     The cc
     */
    public String getCc() {
        return cc;
    }

    /**
     * 
     * @param cc
     *     The cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * 
     * @param country
     *     The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * 
     * @return
     *     The formattedAddress
     */
    public List<String> getFormattedAddress() {
        return formattedAddress;
    }

    /**
     * 
     * @param formattedAddress
     *     The formattedAddress
     */
    public void setFormattedAddress(List<String> formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

}
