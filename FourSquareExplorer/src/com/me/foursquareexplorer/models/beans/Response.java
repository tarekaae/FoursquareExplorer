
package com.me.foursquareexplorer.models.beans;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Response {

    @Expose
    private List<Venue> venues = new ArrayList<Venue>();
    @Expose
    private boolean confident;

    /**
     * 
     * @return
     *     The venues
     */
    public List<Venue> getVenues() {
        return venues;
    }

    /**
     * 
     * @param venues
     *     The venues
     */
    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    /**
     * 
     * @return
     *     The confident
     */
    public boolean isConfident() {
        return confident;
    }

    /**
     * 
     * @param confident
     *     The confident
     */
    public void setConfident(boolean confident) {
        this.confident = confident;
    }

}
