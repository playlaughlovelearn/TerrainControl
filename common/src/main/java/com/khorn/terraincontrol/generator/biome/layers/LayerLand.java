package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerLand extends Layer
{

    public LayerLand(long paramLong, Layer paramGenLayer, int _chance)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.chance = 101 - _chance;
    }

    public int chance = 5;

    @Override
    public int[] GetBiomes(ArraysCache arraysCache, int x, int z, int xSize, int zSize)
    {

        int[] arrayOfInt1 = this.child.GetBiomes(arraysCache, x, z, xSize, zSize);

        int[] arrayOfInt2 = arraysCache.GetArray(xSize * zSize);
        for (int i = 0; i < zSize; i++)
        {
            for (int j = 0; j < xSize; j++)
            {
                SetSeed(x + j, z + i);
                if (nextInt(chance) == 0)
                    arrayOfInt2[(j + i * xSize)] = arrayOfInt1[(j + i * xSize)] | LandBit;
                else
                    arrayOfInt2[(j + i * xSize)] = arrayOfInt1[(j + i * xSize)];
            }
        }
        
        return arrayOfInt2;
    }

}
