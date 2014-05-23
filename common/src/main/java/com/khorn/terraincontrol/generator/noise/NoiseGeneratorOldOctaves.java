package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorOldOctaves
{

    private NoiseGeneratorOld[] generatorCollection;
    private int octaves;

    public NoiseGeneratorOldOctaves(Random random, int numOctaves)
    {
        this.octaves = numOctaves;
        this.generatorCollection = new NoiseGeneratorOld[numOctaves];
        for (int i = 0; i < numOctaves; ++i)
            this.generatorCollection[i] = new NoiseGeneratorOld(random);
    }

    public double[] generate(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude)
    {
        return generate(noiseSpace, xOffset, zOffset, xSize, zSize, xScale, zScale, octaveAmplitude, 0.5D);
    }

    public double[] generate(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude, double octaveOtherScaleValue)
    {
        xScale /= 1.5D;
        zScale /= 1.5D;

        if ((noiseSpace == null) || (noiseSpace.length < xSize * zSize))
            noiseSpace = new double[xSize * zSize];
        else
        {
            for (int i = 0; i < noiseSpace.length; i++)
            {
                noiseSpace[i] = 0.0D;
            }
        }
        double d1 = 1.0D;
        double d2 = 1.0D;
        for (int j = 0; j < this.octaves; j++)
        {
            this.generatorCollection[j].noise(noiseSpace, xOffset, zOffset, xSize, zSize, xScale * d2, zScale * d2, 0.55D / d1);
            d2 *= octaveAmplitude;
            d1 *= octaveOtherScaleValue;
        }

        return noiseSpace;
    }

}
