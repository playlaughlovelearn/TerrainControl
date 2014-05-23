package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorPerlin
{
    private int permutations[];
    public double xCoord;
    public double yCoord;
    public double zCoord;


    public NoiseGeneratorPerlin(Random random)
    {
        permutations = new int[512];
        xCoord = random.nextDouble() * 256D;
        yCoord = random.nextDouble() * 256D;
        zCoord = random.nextDouble() * 256D;
        for (int i = 0; i < 256; i++)
        {
            permutations[i] = i;
        }

        for (int j = 0; j < 256; j++)
        {
            int k = random.nextInt(256 - j) + j;
            int l = permutations[j];
            permutations[j] = permutations[k];
            permutations[k] = l;
            permutations[j + 256] = permutations[j];
        }
    }

    public final double lerp(double t, double a, double b)
    {
        return a + t * (b - a);
    }

    public final double grad2(int hash, double x, double z)
    {
        int masked = hash & 0xf;
        double x0 = (double) (1 - ((masked & 8) >> 3)) * x;
        double z0 = masked >= 4 ? masked != 12 && masked != 14 ? z : x : 0.0D;
        return ((masked & 1) != 0 ? -x0 : x0) + ((masked & 2) != 0 ? -z0 : z0);
    }

    public final double grad3(int hash, double x, double y, double z)
    {
        int masked = hash & 0xf;
        double xy0 = masked >= 8 ? y : x;
        double d4 = masked >= 4 ? masked != 12 && masked != 14 ? z : x : y;
        return ((masked & 1) != 0 ? -xy0 : xy0) + ((masked & 2) != 0 ? -d4 : d4);
    }

    public void noise3D(double[] noiseSpace, double xOffset, double yOffset, double zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double inversePersistance)
    {
        int i1 = 0;
        double d7 = 1.0D / inversePersistance;
        int i2 = -1;
        double d13 = 0.0D;
        double d15 = 0.0D;
        double d16 = 0.0D;
        double d18 = 0.0D;
        for (int xi = 0; xi < xSize; xi++)
        {
            double d20 = xOffset + (double) xi * xScale + xCoord;
            int k5 = (int) d20;
            if (d20 < (double) k5)
            {
                k5--;
            }
            int i6 = k5 & 0xff;
            d20 -= k5;
            double d22 = d20 * d20 * d20 * (d20 * (d20 * 6D - 15D) + 10D);
            for (int zi = 0; zi < zSize; zi++)
            {
                double d24 = zOffset + (double) zi * zScale + zCoord;
                int k6 = (int) d24;
                if (d24 < (double) k6)
                {
                    k6--;
                }
                int l6 = k6 & 0xff;
                d24 -= k6;
                double d25 = d24 * d24 * d24 * (d24 * (d24 * 6D - 15D) + 10D);
                for (int yi = 0; yi < ySize; yi++)
                {
                    double d26 = yOffset + (double) yi * yScale + yCoord;
                    int j7 = (int) d26;
                    if (d26 < (double) j7)
                    {
                        j7--;
                    }
                    int k7 = j7 & 0xff;
                    d26 -= j7;
                    double d27 = d26 * d26 * d26 * (d26 * (d26 * 6D - 15D) + 10D);
                    if (yi == 0 || k7 != i2)
                    {
                        i2 = k7;
                        int j2 = permutations[i6] + k7;
                        int k2 = permutations[j2] + l6;
                        int l2 = permutations[j2 + 1] + l6;
                        int i3 = permutations[i6 + 1] + k7;
                        int k3 = permutations[i3] + l6;
                        int l3 = permutations[i3 + 1] + l6;
                        d13 = lerp(d22, grad3(permutations[k2], d20, d26, d24), grad3(permutations[k3], d20 - 1.0D, d26, d24));
                        d15 = lerp(d22, grad3(permutations[l2], d20, d26 - 1.0D, d24), grad3(permutations[l3], d20 - 1.0D, d26 - 1.0D, d24));
                        d16 = lerp(d22, grad3(permutations[k2 + 1], d20, d26, d24 - 1.0D), grad3(permutations[k3 + 1], d20 - 1.0D, d26, d24 - 1.0D));
                        d18 = lerp(d22, grad3(permutations[l2 + 1], d20, d26 - 1.0D, d24 - 1.0D), grad3(permutations[l3 + 1], d20 - 1.0D, d26 - 1.0D, d24 - 1.0D));
                    }
                    double d28 = lerp(d27, d13, d15);
                    double d29 = lerp(d27, d16, d18);
                    double d30 = lerp(d25, d28, d29);
                    noiseSpace[i1++] += d30 * d7;
                }
            }
        }
    }

    public void noise2D(double NoiseArray[], double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double inversePersistence)
    {
        int j3 = 0;
        double d12 = 1.0D / inversePersistence;
        for (int xi = 0; xi < xSize; xi++)
        {
            double d14 = xOffset + (double) xi * xScale + xCoord;
            int j4 = (int) d14;
            if (d14 < (double) j4)
            {
                j4--;
            }
            int k4 = j4 & 0xff;
            d14 -= j4;
            double d17 = d14 * d14 * d14 * (d14 * (d14 * 6D - 15D) + 10D);
            for (int zi = 0; zi < zSize; zi++)
            {
                double d19 = zOffset + (double) zi * zScale + zCoord;
                int j5 = (int) d19;
                if (d19 < (double) j5)
                {
                    j5--;
                }
                int l5 = j5 & 0xff;
                d19 -= j5;
                double d21 = d19 * d19 * d19 * (d19 * (d19 * 6D - 15D) + 10D);
                int l = permutations[k4];
                int j1 = permutations[l] + l5;
                int k1 = permutations[k4 + 1];
                int l1 = permutations[k1] + l5;
                double d9 = lerp(d17, grad2(permutations[j1], d14, d19), grad3(permutations[l1], d14 - 1.0D, 0.0D, d19));
                double d11 = lerp(d17, grad3(permutations[j1 + 1], d14, 0.0D, d19 - 1.0D), grad3(permutations[l1 + 1], d14 - 1.0D, 0.0D, d19 - 1.0D));
                double d23 = lerp(d21, d9, d11);
                NoiseArray[j3++] += d23 * d12;
            }
        }

    }
}
