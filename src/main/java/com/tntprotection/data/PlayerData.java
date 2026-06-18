package com.tntprotection.data;

import org.bukkit.Location;

public class PlayerData {

    private Location pos1;
    private Location pos2;

    public PlayerData() {
        this.pos1 = null;
        this.pos2 = null;
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public void clear() {
        this.pos1 = null;
        this.pos2 = null;
    }

    public boolean hasPos1() {
        return pos1 != null;
    }

    public boolean hasPos2() {
        return pos2 != null;
    }

    public boolean hasBothPositions() {
        return pos1 != null && pos2 != null;
    }
}
