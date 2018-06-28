/*
 * hexagonal-experiments-in-android
 * Copyright (C) 2017, Logan Martel, Frederick Parsons
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.catandroid.app.common.components.utilities.hex_grid_utils;

import com.catandroid.app.common.components.utilities.hex_grid_utils.unsigned_data.ULong;

import java.util.ArrayList;

/**
 * Created by logan on 2017-01-31.
 */

public class HexGridUtils
{
    public HexGridUtils()
    {
    }

    public static Long perfectHash(int a, int b)
    {
        ULong A = ULong.valueOf(a >= 0 ? 2 * (long)a : -2 * (long)a - 1);
        ULong B = ULong.valueOf (b >= 0 ? 2 * (long)b : -2 * (long)b - 1);
        ULong C = ULong.valueOf(0);
        ULong i = ULong.valueOf(0);
        if(A.compareTo(B) >= 0) {
            while(i.compareTo(A) == -1) {
                C = C.add(A);
                i = i.add(1);
            }
            C = C.add(A);
            C = C.add(B);
        }
        else {
            while(i.compareTo(B) == -1) {
                C = C.add(B);
                i = i.add(1);
            }
            C = C.add(A);
        }
        return a < 0 && b < 0 || a >= 0 && b >= 0 ? C.longValue() : -C.longValue() - 1;
    }

    static public Long perfectHash(AxialHexLocation location)
    {
        return perfectHash(location.q, location.r);
    }

    static public Double cantorHash(double q, double r)
    {
        return 0.5 * ((q + r) * (q + r + 1)) + r;
    }

    static public Double cantorHash(AxialHexLocation location)
    {
        return cantorHash(location.q, location.r);
    }

    static public void equalHex(String name, CubicHexLocation a, CubicHexLocation b)
    {
        if (!(a.q == b.q && a.s == b.s && a.r == b.r))
        {
            complain(name);
        }
    }


    static public void equalOffsetcoord(String name, HexGridOffsetCoord a, HexGridOffsetCoord b)
    {
        if (!(a.col == b.col && a.row == b.row))
        {
            complain(name);
        }
    }


    static public void equalInt(String name, int a, int b)
    {
        if (!(a == b))
        {
            complain(name);
        }
    }


    static public void equalHexArray(String name, ArrayList<CubicHexLocation> a, ArrayList<CubicHexLocation> b)
    {
        equalInt(name, a.size(), b.size());
        for (int i = 0; i < a.size(); i++)
        {
            equalHex(name, a.get(i), b.get(i));
        }
    }


    static public void complain(String name)
    {
        System.out.println("FAIL " + name);
    }

}
