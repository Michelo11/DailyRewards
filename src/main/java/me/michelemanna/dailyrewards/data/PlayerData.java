package me.michelemanna.dailyrewards.data;

import java.sql.Date;

public class PlayerData {
    private Date lastClaim = new Date(0);
    private int claimedDays = 0;

    public Date getLastClaim() {
        return lastClaim;
    }

    public void setLastClaim(Date lastClaim) {
        this.lastClaim = lastClaim;
    }

    public int getClaimedDays() {
        return claimedDays;
    }

    public void setClaimedDays(int claimedDays) {
        this.claimedDays = claimedDays;
    }
}
