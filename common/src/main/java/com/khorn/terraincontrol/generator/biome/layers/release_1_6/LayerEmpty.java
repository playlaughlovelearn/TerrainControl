package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerEmpty extends Layer
{

    public LayerEmpty(long seed)
    {
        super(seed);
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] thisInts = cache.getArray(xSize * zSize);
        for (int i = 0; i < thisInts.length; i++)
            thisInts[i] = 0;
        return thisInts;
    }

}
