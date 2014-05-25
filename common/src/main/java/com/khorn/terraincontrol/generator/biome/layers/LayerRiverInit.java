package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerRiverInit extends Layer
{

    public LayerRiverInit(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] parentInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                this.initChunkSeed((long) (xi + x), (long) (zi + z));
                thisInts[xi + zi * xSize] = parentInts[xi + zi * xSize] > 0 ? this.nextInt(299999) + 2 : 0;
            }
        }
        return thisInts;
    }

}
