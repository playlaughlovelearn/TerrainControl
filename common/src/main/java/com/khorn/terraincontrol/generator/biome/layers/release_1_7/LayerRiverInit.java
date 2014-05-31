package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

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
                //>>	If not Ocean, set to a random value from 2 - 300,000
                thisInts[xi + zi * xSize] = parentInts[xi + zi * xSize] > 0 ? this.nextInt(299999) + 2 : 0;
            }
        }
        return thisInts;
    }

}
