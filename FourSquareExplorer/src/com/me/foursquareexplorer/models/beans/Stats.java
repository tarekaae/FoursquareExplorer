
package com.me.foursquareexplorer.models.beans;

import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
public class Stats {

    @Expose
    private int checkinsCount;
    @Expose
    private int usersCount;
    @Expose
    private int tipCount;

    /**
     * 
     * @return
     *     The checkinsCount
     */
    public int getCheckinsCount() {
        return checkinsCount;
    }

    /**
     * 
     * @param checkinsCount
     *     The checkinsCount
     */
    public void setCheckinsCount(int checkinsCount) {
        this.checkinsCount = checkinsCount;
    }

    /**
     * 
     * @return
     *     The usersCount
     */
    public int getUsersCount() {
        return usersCount;
    }

    /**
     * 
     * @param usersCount
     *     The usersCount
     */
    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    /**
     * 
     * @return
     *     The tipCount
     */
    public int getTipCount() {
        return tipCount;
    }

    /**
     * 
     * @param tipCount
     *     The tipCount
     */
    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

}
