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
        for (int i = 0; i < numOctaves; i++)
            this.generatorCollection[i] = new NoiseGeneratorOld(random);
    }

    public double[] generateOctaves(double[] octaves, double Xin, double Yin, int Xmax, int Ymax, double Xscale, double Yscale, double someScaleValue)
    {
        return generateOctaves(octaves, Xin, Yin, Xmax, Ymax, Xscale, Yscale, someScaleValue, 0.5D);
    }

    public double[] generateOctaves(double[] octaves, double Xin, double Yin, int Xmax, int Ymax, double Xscale, double Yscale, double someScaleValue, double someOtherScaleValue)
    {
        Xscale /= 1.5D;
        Yscale /= 1.5D;

        if ((octaves == null) || (octaves.length < Xmax * Ymax))
            octaves = new double[Xmax * Ymax];
        else
        {
            for (int i = 0; i < octaves.length; i++)
            {
                octaves[i] = 0.0D;
            }
        }
        double d1 = 1.0D;
        double d2 = 1.0D;
        for (int j = 0; j < this.octaves; j++)
        {
            this.generatorCollection[j].noise(octaves, Xin, Yin, Xmax, Ymax, Xscale * d2, Yscale * d2, 0.55D / d1);
            d2 *= someScaleValue;
            d1 *= someOtherScaleValue;
        }

        return octaves;
    }

}
