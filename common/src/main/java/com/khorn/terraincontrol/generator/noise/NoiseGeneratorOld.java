package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorOld extends NoiseGeneratorSimplex
{

    public NoiseGeneratorOld(Random random)
    {
        super(random);
    }

    @Override
    public void noise(double[] noiseSpace, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double octaveAmplitude)
    {
        int i = 0;
        for (int xi = 0; xi < xSize; xi++)
        {
            double z0 = (zOffset + (double) xi) * zScale + this.noiseContributionZ;
            for (int zi = 0; zi < zSize; zi++)
            {
                double x0 = (xOffset + (double) zi) * xScale + this.noiseContributionX;
                noiseSpace[(i++)] += this.noise(x0, z0) * octaveAmplitude;
            }
        }
    }

}
