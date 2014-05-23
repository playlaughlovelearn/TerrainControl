package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorNewOctaves
{

    private NoiseGeneratorNew[] generatorCollection;
    private int octaves;

    public NoiseGeneratorNewOctaves(Random random, int numOctaves)
    {
        this.octaves = numOctaves;
        this.generatorCollection = new NoiseGeneratorNew[numOctaves];
        for (int i = 0; i < numOctaves; ++i)
            this.generatorCollection[i] = new NoiseGeneratorNew(random);
    }

    public double generate(double xOffset, double zOffset)
    {
        double noise = 0.0D;
        double octaveAmplitude = 1.0D;

        for (int i = 0; i < this.octaves; ++i)
        {
            noise += this.generatorCollection[i].noise(xOffset * octaveAmplitude, zOffset * octaveAmplitude) / octaveAmplitude;
            octaveAmplitude /= 2.0D;
        }

        return noise;
    }

    public double[] generate(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude)
    {
        return this.generate(noiseSpace, xOffset, zOffset, xSize, zSize, xScale, zScale, octaveAmplitude, 0.5D);
    }

    public double[] generate(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude, double octaveOtherScaleValue)
    {
        if (noiseSpace != null && noiseSpace.length >= xSize * zSize)
        {
            for (int k = 0; k < noiseSpace.length; ++k)
            {
                noiseSpace[k] = 0.0D;
            }
        } else
        {
            noiseSpace = new double[xSize * zSize];
        }

        double d6 = 1.0D;
        double d7 = 1.0D;

        for (int l = 0; l < this.octaves; ++l)
        {
            this.generatorCollection[l].noise(noiseSpace, xOffset, zOffset, xSize, zSize, xScale * d7 * d6, zScale * d7 * d6, 0.55D / d6);
            d7 *= octaveAmplitude;
            d6 *= octaveOtherScaleValue;
        }

        return noiseSpace;
    }

}
