package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public abstract class NoiseGeneratorSimplex {

    protected static final int[][] grad3 = new int[][]
    { 
        { 1, 1, 0}, { -1, 1, 0}, { 1, -1, 0}, { -1, -1, 0},
        { 1, 0, 1}, { -1, 0, 1}, { 1, 0, -1}, { -1, 0, -1},
        { 0, 1, 1}, { 0, -1, 1}, { 0, 1, -1}, { 0, -1, -1}
    };
    protected static final double square3 = Math.sqrt(3.0D);
    protected static final double skewFactor = 0.5D * (square3 - 1.0D);
    protected static final double deskewFactor = (3.0D - square3) / 6.0D;

    protected int[] permutationTable;
    public double noiseContributionX;
    public double noiseContributionY;
    public double noiseContributionZ;

    public NoiseGeneratorSimplex()
    {
        this(new Random());
    }

    public NoiseGeneratorSimplex(Random random)
    {
        this.permutationTable = new int[512];
        this.noiseContributionX = random.nextDouble() * 256.0D;
        this.noiseContributionY = random.nextDouble() * 256.0D;
        this.noiseContributionZ = random.nextDouble() * 256.0D;
        int i;

        for (i = 0; i < 256; this.permutationTable[i] = i++)
        {
        }

        for (i = 0; i < 256; ++i)
        {
            int swapPos = random.nextInt(256 - i) + i;    //>>    Get a point < 256
            int swapSave = this.permutationTable[i];    //>>    Save the point at i
            this.permutationTable[i] = this.permutationTable[swapPos];  //>>    Swap i and swapPos
            this.permutationTable[swapPos] = swapSave;
            this.permutationTable[i + 256] = this.permutationTable[i];  //>>    Doubles the table to remove the need for index wrapping
        }
    }

    private static int fastFloor(double x)
    {
        int i = (int) x;
        return x > 0.0D ? i : i - 1;
    }

    private static double dot(int[] g, double a, double b)
    {
        return (double) g[0] * a + (double) g[1] * b;
    }

    public double noise(double x, double y)
    {
        double s = (x + y) * skewFactor;
        int i = fastFloor(x + s);
        int j = fastFloor(y + s);
        double t = (double) (i + j) * deskewFactor;
        double X0 = (double) i - t;
        double Y0 = (double) j - t;
        double x0 = x - X0;
        double y0 = y - Y0;
        byte i1;
        byte j1;

        if (x0 > y0)
        {
            i1 = 1;
            j1 = 0;
        } else
        {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - (double) i1 + deskewFactor;
        double y1 = y0 - (double) j1 + deskewFactor;
        double x2 = x0 - 1.0D + 2.0D * deskewFactor;
        double y2 = y0 - 1.0D + 2.0D * deskewFactor;
        int i0 = i & 255;
        int j0 = j & 255;
        int gi0 = this.permutationTable[i0 + this.permutationTable[j0]] % 12;
        int gi1 = this.permutationTable[i0 + i1 + this.permutationTable[j0 + j1]] % 12;
        int gi2 = this.permutationTable[i0 + 1 + this.permutationTable[j0 + 1]] % 12;

        double cc0 = this.calculateCornerContribution(x0, y0, gi0);
        double cc1 = this.calculateCornerContribution(x1, y1, gi1);
        double cc2 = this.calculateCornerContribution(x2, y2, gi2);
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0D * (cc0 + cc1 + cc2);
    }

    private double calculateCornerContribution(double x, double y, int gi)
    {
        double t = 0.5D - x * x - y * y;

        if (t < 0.0D)
        {
            return 0.0D;
        } else
        {
            t *= t;
            return t * t * dot(grad3[gi], x, y);
        }
    }

    /**
     * 
     * @param noisePlane The X,Y plane of noise values in a 1D array
     * @param x 
     * @param y
     * @param xmax
     * @param ymax
     * @param xscale
     * @param yscale
     * @param noiseRange Noise values generated are in the interval [-1,1] and then multiplied by this value
     */
    public abstract void noise(double[] noisePlane, double x, double y, int xmax, int ymax, double xscale, double yscale, double noiseRange);
    
}