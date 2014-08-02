package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerEmpty extends Layer
{

    public LayerEmpty(long paramLong)
    {
        super(paramLong);
    }

    @Override
    public int[] GetBiomes(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        int[] arrayOfInt = arraysCache.GetArray(x_size * z_size);
        for (int i = 0; i < arrayOfInt.length; i++)
            arrayOfInt[i] = 0;

//        for (int zi = 0; zi < z_size; ++zi)
//        {
//            for (int xi = 0; xi < x_size; ++xi)
//            {
//                this.SetSeed((long) (x + xi), (long) (z + zi));
//                arrayOfInt[xi + zi * x_size] |= this.nextInt(10) == 0 ? LandBit : 0;
//            }
//        }
//
//        if (x > -x_size && x <= 0 && z > -z_size && z <= 0)
//        {
//            arrayOfInt[-x + -z * x_size] |= LandBit;
//        }

        return arrayOfInt;
    }

}
