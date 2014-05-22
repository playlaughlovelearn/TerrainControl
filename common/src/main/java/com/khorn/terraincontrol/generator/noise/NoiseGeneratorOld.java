package com.khorn.terraincontrol.generator.noise;

import java.util.Random;

public class NoiseGeneratorOld extends NoiseGeneratorSimplex
{

    public NoiseGeneratorOld(Random random)
    {
        super(random);
    }

    @Override
    public void noise(double[] noisePlane, double x, double y, int xmax, int ymax, double xscale, double yscale, double noiseRange)
    {
        int i = 0;
        for (int xi = 0; xi < xmax; xi++)
        {
            double y0 = (y + (double) xi) * yscale + this.noiseContributionY;
            for (int yi = 0; yi < ymax; yi++)
            {
                double x0 = (x + (double) yi) * xscale + this.noiseContributionX;
                noisePlane[(i++)] += this.noise(x0, y0) * noiseRange;
            }
        }
    }

}
