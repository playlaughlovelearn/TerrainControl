package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorNew extends NoiseGeneratorSimplex
{

    public NoiseGeneratorNew()
    {
        super();
    }

    public NoiseGeneratorNew(Random random)
    {
        super(random);
    }

    @Override
    public void noise(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude)
    {
        int i = 0;
        for (int zi = 0; zi < zSize; ++zi)
        {
            double z0 = (zOffset + (double) zi) * zScale + this.noiseContributionZ;
            for (int xi = 0; xi < xSize; ++xi)
            {
                double x0 = (xOffset + (double) xi) * xScale + this.noiseContributionX;
                noiseSpace[(i++)] += this.noise(x0, z0) * octaveAmplitude;
            }
        }
    }

}
