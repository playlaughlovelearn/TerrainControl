package com.khorn.terraincontrol.generator.noise;

import com.khorn.terraincontrol.util.helpers.MathHelper;

import java.util.Random;

public class NoiseGeneratorPerlinOctaves
{

    private NoiseGeneratorPerlin[] generatorCollection;
    private int octaves;

    public NoiseGeneratorPerlinOctaves(Random random, int numOctaves)
    {
        this.octaves = numOctaves;
        this.generatorCollection = new NoiseGeneratorPerlin[numOctaves];

        for (int i = 0; i < numOctaves; ++i)
        {
            this.generatorCollection[i] = new NoiseGeneratorPerlin(random);
        }
    }

    public double[] generate3D(double[] noiseSpace, int xOffset, int yOffset, int zOffset, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale)
    {
        if (noiseSpace == null)
        {
            noiseSpace = new double[xSize * ySize * zSize];
        } else
        {
            for (int i = 0; i < noiseSpace.length; ++i)
            {
                noiseSpace[i] = 0.0D;
            }
        }

        double octaveFactor = 1.0D;

        for (int i = 0; i < this.octaves; ++i)
        {
            double xF = (double) xOffset * octaveFactor * xScale;
            double yF = (double) yOffset * octaveFactor * yScale;
            double zF = (double) zOffset * octaveFactor * zScale;
            long xFfloor = MathHelper.floor_double_long(xF);
            long zFfloor = MathHelper.floor_double_long(zF);

            xF -= (double) xFfloor;
            zF -= (double) zFfloor;
            xFfloor %= 16777216L;
            zFfloor %= 16777216L;
            xF += (double) xFfloor;
            zF += (double) zFfloor;
            this.generatorCollection[i].noise3D(noiseSpace, xF, yF, zF, xSize, ySize, zSize, xScale * octaveFactor, yScale * octaveFactor, zScale * octaveFactor, octaveFactor);
            octaveFactor /= 2.0D;
        }

        return noiseSpace;
    }


    public double[] generate2D(double[] noiseSpace, int xOffset, int zOffset, int xSize, int zSize, double xScale, double zScale)
    {
       // return this.Noise3D(doubleArray, xOffset, 10, zOffset, xSize, 1, zSize, xScale, 1.0D, zScale);

        if (noiseSpace == null)
        {
            noiseSpace = new double[xSize * zSize];
        } else
        {
            for (int i = 0; i < noiseSpace.length; ++i)
            {
                noiseSpace[i] = 0.0D;
            }
        }

        double octaveAmplitude = 1.0D;

        for (int i = 0; i < this.octaves; ++i)
        {
            double xF = (double) xOffset * octaveAmplitude * xScale;
            double zF = (double) zOffset * octaveAmplitude * zScale;
            long xFfloor = MathHelper.floor_double_long(xF);
            long zFfloor = MathHelper.floor_double_long(zF);

            xF -= (double) xFfloor;
            zF -= (double) zFfloor;
            xFfloor %= 16777216L;
            zFfloor %= 16777216L;
            xF += (double) xFfloor;
            zF += (double) zFfloor;
            this.generatorCollection[i].noise2D(noiseSpace, xF, zF, xSize, zSize, xScale * octaveAmplitude, zScale * octaveAmplitude, octaveAmplitude);
            octaveAmplitude /= 2.0D;
        }

        return noiseSpace;
    }
}
