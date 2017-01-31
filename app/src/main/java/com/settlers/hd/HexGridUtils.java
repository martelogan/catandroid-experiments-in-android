package com.settlers.hd;

import java.util.ArrayList;

class Point
{
    public Point(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    public final double x;
    public final double y;
}

class CubicHexLocation
{
    public CubicHexLocation(int q, int r, int s)
    {
        this.q = q;
        this.r = r;
        this.s = s;
    }
    public final int q;
    public final int r;
    public final int s;

    static public CubicHexLocation addCubic(CubicHexLocation a, CubicHexLocation b)
    {
        return new CubicHexLocation(a.q + b.q, a.r + b.r, a.s + b.s);
    }


    static public CubicHexLocation subtractCubic(CubicHexLocation a, CubicHexLocation b)
    {
        return new CubicHexLocation(a.q - b.q, a.r - b.r, a.s - b.s);
    }


    static public CubicHexLocation scale(CubicHexLocation a, int k)
    {
        return new CubicHexLocation(a.q * k, a.r * k, a.s * k);
    }

    static public ArrayList<CubicHexLocation> cubicDirections = new ArrayList<CubicHexLocation>(){{add(new CubicHexLocation(1, 0, -1)); add(new CubicHexLocation(1, -1, 0)); add(new CubicHexLocation(0, -1, 1)); add(new CubicHexLocation(-1, 0, 1)); add(new CubicHexLocation(-1, 1, 0)); add(new CubicHexLocation(0, 1, -1));}};

    static public CubicHexLocation cubicDirection(int direction)
    {
        return CubicHexLocation.cubicDirections.get(direction);
    }

    static public int complementCubicDirection(int direction)
    {
        return (direction + 3) % 6;
    }

    static public CubicHexLocation cubicNeighbor(CubicHexLocation hexLocation, int direction)
    {
        return CubicHexLocation.addCubic(hexLocation, CubicHexLocation.cubicDirection(direction));
    }

    static public ArrayList<CubicHexLocation> diagonals = new ArrayList<CubicHexLocation>(){{add(new CubicHexLocation(2, -1, -1)); add(new CubicHexLocation(1, -2, 1)); add(new CubicHexLocation(-1, -1, 2)); add(new CubicHexLocation(-2, 1, 1)); add(new CubicHexLocation(-1, 2, -1)); add(new CubicHexLocation(1, 1, -2));}};

    static public CubicHexLocation diagonalNeighbor(CubicHexLocation hexLocation, int direction)
    {
        return CubicHexLocation.addCubic(hexLocation, CubicHexLocation.diagonals.get(direction));
    }


    static public int length(CubicHexLocation hexLocation)
    {
        return (int)((Math.abs(hexLocation.q) + Math.abs(hexLocation.r) + Math.abs(hexLocation.s)) / 2);
    }


    static public int distance(CubicHexLocation a, CubicHexLocation b)
    {
        return CubicHexLocation.length(CubicHexLocation.subtractCubic(a, b));
    }

}

class AxialHexLocation
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

class FractionalHexLocation
{
    public FractionalHexLocation(double q, double r, double s)
    {
        this.q = q;
        this.r = r;
        this.s = s;
    }
    public final double q;
    public final double r;
    public final double s;

    static public CubicHexLocation hexRound(FractionalHexLocation h)
    {
        int q = (int)(Math.round(h.q));
        int r = (int)(Math.round(h.r));
        int s = (int)(Math.round(h.s));
        double q_diff = Math.abs(q - h.q);
        double r_diff = Math.abs(r - h.r);
        double s_diff = Math.abs(s - h.s);
        if (q_diff > r_diff && q_diff > s_diff)
        {
            q = -r - s;
        }
        else
        if (r_diff > s_diff)
        {
            r = -q - s;
        }
        else
        {
            s = -q - r;
        }
        return new CubicHexLocation(q, r, s);
    }


    static public FractionalHexLocation hexLerp(FractionalHexLocation a, FractionalHexLocation b, double t)
    {
        return new FractionalHexLocation(a.q * (1 - t) + b.q * t, a.r * (1 - t) + b.r * t, a.s * (1 - t) + b.s * t);
    }


    static public ArrayList<CubicHexLocation> hexLinedraw(CubicHexLocation a, CubicHexLocation b)
    {
        int N = CubicHexLocation.distance(a, b);
        FractionalHexLocation a_nudge = new FractionalHexLocation(a.q + 0.000001, a.r + 0.000001, a.s - 0.000002);
        FractionalHexLocation b_nudge = new FractionalHexLocation(b.q + 0.000001, b.r + 0.000001, b.s - 0.000002);
        ArrayList<CubicHexLocation> results = new ArrayList<CubicHexLocation>(){{}};
        double step = 1.0 / Math.max(N, 1);
        for (int i = 0; i <= N; i++)
        {
            results.add(FractionalHexLocation.hexRound(FractionalHexLocation.hexLerp(a_nudge, b_nudge, step * i)));
        }
        return results;
    }

}

class OffsetCoord
{
    public OffsetCoord(int col, int row)
    {
        this.col = col;
        this.row = row;
    }
    public final int col;
    public final int row;
    static public int EVEN = 1;
    static public int ODD = -1;

    static public OffsetCoord qoffsetFromCube(int offset, CubicHexLocation h)
    {
        int col = h.q;
        int row = h.r + (int)((h.q + offset * (h.q & 1)) / 2);
        return new OffsetCoord(col, row);
    }


    static public CubicHexLocation qoffsetToCube(int offset, OffsetCoord h)
    {
        int q = h.col;
        int r = h.row - (int)((h.col + offset * (h.col & 1)) / 2);
        int s = -q - r;
        return new CubicHexLocation(q, r, s);
    }


    static public OffsetCoord roffsetFromCube(int offset, CubicHexLocation h)
    {
        int col = h.q + (int)((h.r + offset * (h.r & 1)) / 2);
        int row = h.r;
        return new OffsetCoord(col, row);
    }


    static public CubicHexLocation roffsetToCube(int offset, OffsetCoord h)
    {
        int q = h.col - (int)((h.row + offset * (h.row & 1)) / 2);
        int r = h.row;
        int s = -q - r;
        return new CubicHexLocation(q, r, s);
    }

}

class Orientation
{
    public Orientation(double f0, double f1, double f2, double f3, double b0, double b1, double b2, double b3, double start_angle)
    {
        this.f0 = f0;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.start_angle = start_angle;
    }
    public final double f0;
    public final double f1;
    public final double f2;
    public final double f3;
    public final double b0;
    public final double b1;
    public final double b2;
    public final double b3;
    public final double start_angle;
}

class Layout
{
    public Layout(Orientation orientation, Point size, Point origin)
    {
        this.orientation = orientation;
        this.size = size;
        this.origin = origin;
    }
    public final Orientation orientation;
    public final Point size;
    public final Point origin;
    static public Orientation pointy = new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0, Math.sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5);
    static public Orientation flat = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

    static public Point hexToPixel(Layout layout, CubicHexLocation h)
    {
        Orientation M = layout.orientation;
        Point size = layout.size;
        Point origin = layout.origin;
        double x = (M.f0 * h.q + M.f1 * h.r) * size.x;
        double y = (M.f2 * h.q + M.f3 * h.r) * size.y;
        return new Point(x + origin.x, y + origin.y);
    }


    static public FractionalHexLocation pixelToHex(Layout layout, Point p)
    {
        Orientation M = layout.orientation;
        Point size = layout.size;
        Point origin = layout.origin;
        Point pt = new Point((p.x - origin.x) / size.x, (p.y - origin.y) / size.y);
        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;
        return new FractionalHexLocation(q, r, -q - r);
    }


    static public Point hexCornerOffset(Layout layout, int corner)
    {
        Orientation M = layout.orientation;
        Point size = layout.size;
        double angle = 2.0 * Math.PI * (M.start_angle - corner) / 6;
        return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }


    static public ArrayList<Point> polygonCorners(Layout layout, CubicHexLocation h)
    {
        ArrayList<Point> corners = new ArrayList<Point>(){{}};
        Point center = Layout.hexToPixel(layout, h);
        for (int i = 0; i < 6; i++)
        {
            Point offset = Layout.hexCornerOffset(layout, i);
            corners.add(new Point(center.x + offset.x, center.y + offset.y));
        }
        return corners;
    }

}



// HexGridUtils


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
            HexGridUtils.complain(name);
        }
    }


    static public void equalOffsetcoord(String name, OffsetCoord a, OffsetCoord b)
    {
        if (!(a.col == b.col && a.row == b.row))
        {
            HexGridUtils.complain(name);
        }
    }


    static public void equalInt(String name, int a, int b)
    {
        if (!(a == b))
        {
            HexGridUtils.complain(name);
        }
    }


    static public void equalHexArray(String name, ArrayList<CubicHexLocation> a, ArrayList<CubicHexLocation> b)
    {
        HexGridUtils.equalInt(name, a.size(), b.size());
        for (int i = 0; i < a.size(); i++)
        {
            HexGridUtils.equalHex(name, a.get(i), b.get(i));
        }
    }


    static public void testHexArithmetic()
    {
        HexGridUtils.equalHex("hex_add", new CubicHexLocation(4, -10, 6), CubicHexLocation.addCubic(new CubicHexLocation(1, -3, 2), new CubicHexLocation(3, -7, 4)));
        HexGridUtils.equalHex("hex_subtract", new CubicHexLocation(-2, 4, -2), CubicHexLocation.subtractCubic(new CubicHexLocation(1, -3, 2), new CubicHexLocation(3, -7, 4)));
    }


    static public void testHexDirection()
    {
        HexGridUtils.equalHex("hex_direction", new CubicHexLocation(0, -1, 1), CubicHexLocation.cubicDirection(2));
    }


    static public void testHexNeighbor()
    {
        HexGridUtils.equalHex("hex_neighbor", new CubicHexLocation(1, -3, 2), CubicHexLocation.cubicNeighbor(new CubicHexLocation(1, -2, 1), 2));
    }


    static public void testHexDiagonal()
    {
        HexGridUtils.equalHex("hex_diagonal", new CubicHexLocation(-1, -1, 2), CubicHexLocation.diagonalNeighbor(new CubicHexLocation(1, -2, 1), 3));
    }


    static public void testHexDistance()
    {
        HexGridUtils.equalInt("hex_distance", 7, CubicHexLocation.distance(new CubicHexLocation(3, -7, 4), new CubicHexLocation(0, 0, 0)));
    }


    static public void testHexRound()
    {
        FractionalHexLocation a = new FractionalHexLocation(0, 0, 0);
        FractionalHexLocation b = new FractionalHexLocation(1, -1, 0);
        FractionalHexLocation c = new FractionalHexLocation(0, -1, 1);
        HexGridUtils.equalHex("hex_round 1", new CubicHexLocation(5, -10, 5), FractionalHexLocation.hexRound(FractionalHexLocation.hexLerp(new FractionalHexLocation(0, 0, 0), new FractionalHexLocation(10, -20, 10), 0.5)));
        HexGridUtils.equalHex("hex_round 2", FractionalHexLocation.hexRound(a), FractionalHexLocation.hexRound(FractionalHexLocation.hexLerp(a, b, 0.499)));
        HexGridUtils.equalHex("hex_round 3", FractionalHexLocation.hexRound(b), FractionalHexLocation.hexRound(FractionalHexLocation.hexLerp(a, b, 0.501)));
        HexGridUtils.equalHex("hex_round 4", FractionalHexLocation.hexRound(a), FractionalHexLocation.hexRound(new FractionalHexLocation(a.q * 0.4 + b.q * 0.3 + c.q * 0.3, a.r * 0.4 + b.r * 0.3 + c.r * 0.3, a.s * 0.4 + b.s * 0.3 + c.s * 0.3)));
        HexGridUtils.equalHex("hex_round 5", FractionalHexLocation.hexRound(c), FractionalHexLocation.hexRound(new FractionalHexLocation(a.q * 0.3 + b.q * 0.3 + c.q * 0.4, a.r * 0.3 + b.r * 0.3 + c.r * 0.4, a.s * 0.3 + b.s * 0.3 + c.s * 0.4)));
    }


    static public void testHexLinedraw()
    {
        HexGridUtils.equalHexArray("hex_linedraw", new ArrayList<CubicHexLocation>(){{add(new CubicHexLocation(0, 0, 0)); add(new CubicHexLocation(0, -1, 1)); add(new CubicHexLocation(0, -2, 2)); add(new CubicHexLocation(1, -3, 2)); add(new CubicHexLocation(1, -4, 3)); add(new CubicHexLocation(1, -5, 4));}}, FractionalHexLocation.hexLinedraw(new CubicHexLocation(0, 0, 0), new CubicHexLocation(1, -5, 4)));
    }


    static public void testLayout()
    {
        CubicHexLocation h = new CubicHexLocation(3, 4, -7);
        Layout flat = new Layout(Layout.flat, new Point(10, 15), new Point(35, 71));
        HexGridUtils.equalHex("layout", h, FractionalHexLocation.hexRound(Layout.pixelToHex(flat, Layout.hexToPixel(flat, h))));
        Layout pointy = new Layout(Layout.pointy, new Point(10, 15), new Point(35, 71));
        HexGridUtils.equalHex("layout", h, FractionalHexLocation.hexRound(Layout.pixelToHex(pointy, Layout.hexToPixel(pointy, h))));
    }


    static public void testConversionRoundtrip()
    {
        CubicHexLocation a = new CubicHexLocation(3, 4, -7);
        OffsetCoord b = new OffsetCoord(1, -3);
        HexGridUtils.equalHex("conversion_roundtrip even-q", a, OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, a)));
        HexGridUtils.equalOffsetcoord("conversion_roundtrip even-q", b, OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, b)));
        HexGridUtils.equalHex("conversion_roundtrip odd-q", a, OffsetCoord.qoffsetToCube(OffsetCoord.ODD, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, a)));
        HexGridUtils.equalOffsetcoord("conversion_roundtrip odd-q", b, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, OffsetCoord.qoffsetToCube(OffsetCoord.ODD, b)));
        HexGridUtils.equalHex("conversion_roundtrip even-r", a, OffsetCoord.roffsetToCube(OffsetCoord.EVEN, OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, a)));
        HexGridUtils.equalOffsetcoord("conversion_roundtrip even-r", b, OffsetCoord.roffsetFromCube(OffsetCoord.EVEN, OffsetCoord.roffsetToCube(OffsetCoord.EVEN, b)));
        HexGridUtils.equalHex("conversion_roundtrip odd-r", a, OffsetCoord.roffsetToCube(OffsetCoord.ODD, OffsetCoord.roffsetFromCube(OffsetCoord.ODD, a)));
        HexGridUtils.equalOffsetcoord("conversion_roundtrip odd-r", b, OffsetCoord.roffsetFromCube(OffsetCoord.ODD, OffsetCoord.roffsetToCube(OffsetCoord.ODD, b)));
    }


    static public void testOffsetFromCube()
    {
        HexGridUtils.equalOffsetcoord("offset_from_cube even-q", new OffsetCoord(1, 3), OffsetCoord.qoffsetFromCube(OffsetCoord.EVEN, new CubicHexLocation(1, 2, -3)));
        HexGridUtils.equalOffsetcoord("offset_from_cube odd-q", new OffsetCoord(1, 2), OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, new CubicHexLocation(1, 2, -3)));
    }


    static public void testOffsetToCube()
    {
        HexGridUtils.equalHex("offset_to_cube even-", new CubicHexLocation(1, 2, -3), OffsetCoord.qoffsetToCube(OffsetCoord.EVEN, new OffsetCoord(1, 3)));
        HexGridUtils.equalHex("offset_to_cube odd-q", new CubicHexLocation(1, 2, -3), OffsetCoord.qoffsetToCube(OffsetCoord.ODD, new OffsetCoord(1, 2)));
    }


    static public void testAll()
    {
        HexGridUtils.testHexArithmetic();
        HexGridUtils.testHexDirection();
        HexGridUtils.testHexNeighbor();
        HexGridUtils.testHexDiagonal();
        HexGridUtils.testHexDistance();
        HexGridUtils.testHexRound();
        HexGridUtils.testHexLinedraw();
        HexGridUtils.testLayout();
        HexGridUtils.testConversionRoundtrip();
        HexGridUtils.testOffsetFromCube();
        HexGridUtils.testOffsetToCube();
    }


    static public void main(String[] args)
    {
        HexGridUtils.testAll();
    }


    static public void complain(String name)
    {
        System.out.println("FAIL " + name);
    }

}
