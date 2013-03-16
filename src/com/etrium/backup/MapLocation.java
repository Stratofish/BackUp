package com.etrium.backup;

import com.badlogic.gdx.math.Vector2;

public class MapLocation
{
    public MapLocation(Vector2 p_location)
    {
        x = (int)p_location.x;
        y = (int)p_location.y;
    }
    
    public MapLocation()
    {
        x = 0;
        y = 0;
    }

    public MapLocation(int p_x, int p_y)
    {
        x = p_x;
        y = p_y;
    }

    public int x;
    public int y;
}
