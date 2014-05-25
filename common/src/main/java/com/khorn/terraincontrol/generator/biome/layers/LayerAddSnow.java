package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerAddSnow extends Layer
{

    public LayerAddSnow(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int x0 = x - 1;
        int z0 = z - 1;
        int xSize0 = xSize + 2;
        int zSize0 = zSize + 2;
        int[] childInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                int selection = childInts[xi + 1 + (zi + 1) * xSize0];
                this.initChunkSeed((long) (xi + x), (long) (zi + z));

                if (selection == 0)
                {
                    thisInts[xi + zi * xSize] = 0;
                } else
                {
                    int chance = this.nextInt(6);
                    byte snowValue;

                    if (chance == 0)
                    {
                        snowValue = 4;
                    } else if (chance <= 1)
                    {
                        snowValue = 3;
                    } else
                    {
                        snowValue = 1;
                    }

                    thisInts[xi + zi * xSize] = snowValue;
                }
            }
        }
        return thisInts;
    }

}
