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
    public double noiseContributionZ;
    public double noiseContributionY;

    public NoiseGeneratorSimplex()
    {
        this(new Random());
    }

    public NoiseGeneratorSimplex(Random random)
    {
        this.permutationTable = new int[512];
        this.noiseContributionX = random.nextDouble() * 256.0D;
        this.noiseContributionZ = random.nextDouble() * 256.0D;
        this.noiseContributionY = random.nextDouble() * 256.0D;
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

    public double noise(double xOffset, double zOffset)
    {
        double s = (xOffset + zOffset) * skewFactor;
        int i = fastFloor(xOffset + s);
        int j = fastFloor(zOffset + s);
        double t = (double) (i + j) * deskewFactor;
        double X0 = (double) i - t;
        double Z0 = (double) j - t;
        double x0 = xOffset - X0;
        double z0 = zOffset - Z0;
        byte i1;
        byte j1;

        if (x0 > z0)
        {
            i1 = 1;
            j1 = 0;
        } else
        {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - (double) i1 + deskewFactor;
        double z1 = z0 - (double) j1 + deskewFactor;
        double x2 = x0 - 1.0D + 2.0D * deskewFactor;
        double z2 = z0 - 1.0D + 2.0D * deskewFactor;
        int i0 = i & 255;
        int j0 = j & 255;
        int gi0 = this.permutationTable[i0 + this.permutationTable[j0]] % 12;
        int gi1 = this.permutationTable[i0 + i1 + this.permutationTable[j0 + j1]] % 12;
        int gi2 = this.permutationTable[i0 + 1 + this.permutationTable[j0 + 1]] % 12;

        double cc0 = this.calculateCornerContribution(x0, z0, gi0);
        double cc1 = this.calculateCornerContribution(x1, z1, gi1);
        double cc2 = this.calculateCornerContribution(x2, z2, gi2);
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0D * (cc0 + cc1 + cc2);
    }

    private double calculateCornerContribution(double xOffset, double zOffset, int gi)
    {
        double t = 0.5D - xOffset * xOffset - zOffset * zOffset;

        if (t < 0.0D)
        {
            return 0.0D;
        } else
        {
            t *= t;
            return t * t * dot(grad3[gi], xOffset, zOffset);
        }
    }

    /**
     * 
     * @param noiseSpace The X,Y plane of noise values in a 1D array
     * @param xOffset 
     * @param zOffset
     * @param xSize
     * @param zSize
     * @param xScale
     * @param zScale
     * @param octaveAmplitude Noise values generated are in the interval [-1,1] and then multiplied by this value
     */
    public abstract void noise(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude);
    
}