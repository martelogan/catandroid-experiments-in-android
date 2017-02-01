package com.catandroid.app.common.components.hexGridUtils;

import java.util.ArrayList;

/**
 * Created by logan on 2017-01-31.
 */

public class AxialHexLocation
{
    public AxialHexLocation(int q, int r)
    {
        this.q = q;
        this.r = r;
    }

    public final int q;
    public final int r;

    static public AxialHexLocation addAxial(AxialHexLocation a, AxialHexLocation b)
    {
        int q = a.q + b.q;
        int r = a.r + b.r;
        return new AxialHexLocation(q, r);
    }

    // for pointy-oriented hex, directions are clockwise from 0->5 with 0 at top-right edge
    static public ArrayList<AxialHexLocation> axialDirections = new ArrayList<AxialHexLocation>(){{
        add(new AxialHexLocation(1, -1)); add(new AxialHexLocation(1, 0));
        add(new AxialHexLocation(0, 1)); add(new AxialHexLocation(-1, 1));
        add(new AxialHexLocation(-1, 0)); add(new AxialHexLocation(0, -1));}};

    static public AxialHexLocation axialDirection(int direction)
    {
        return AxialHexLocation.axialDirections.get(direction);
    }

    static public int complementAxialDirection(int direction)
    {
        return (direction + 3) % 6;
    }

    static public AxialHexLocation axialNeighbor(AxialHexLocation hexLocation, int direction)
    {
        return AxialHexLocation.addAxial(hexLocation, AxialHexLocation.axialDirection(direction));
    }

}