
package com.me.foursquareexplorer.models.beans;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Specials {

    @Expose
    private int count;
    @Expose
    private List<Object> items = new ArrayList<Object>();

    /**
     * 
     * @return
     *     The count
     */
    public int getCount() {
        return count;
    }

    /**
     * 
     * @param count
     *     The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 
     * @return
     *     The items
     */
    public List<Object> getItems() {
        return items;
    }

    /**
     * 
     * @param items
     *     The items
     */
    public void setItems(List<Object> items) {
        this.items = items;
    }

}
