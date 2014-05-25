package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerIsland extends Layer
{

    public LayerIsland(long seed)
    {
        super(seed);
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                this.initChunkSeed((long) (x + xi), (long) (z + zi));
                thisInts[xi + zi * xSize] = this.nextInt(10) == 0 ? 1 : 0;
            }
        }

        if (x > -xSize && x <= 0 && z > -zSize && z <= 0)
        {
            thisInts[-x + -z * xSize] = 1;
        }

        return thisInts;
    }

}
