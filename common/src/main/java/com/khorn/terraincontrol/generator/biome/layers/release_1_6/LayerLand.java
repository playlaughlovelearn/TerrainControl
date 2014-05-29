package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerLand extends Layer
{

    public LayerLand(long seed, Layer childLayer, int chance)
    {
        super(seed);
        this.child = childLayer;
        this.chance = 101 - chance;
    }

    public int chance = 5;

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                initChunkSeed(x + xi, z + zi);
                if (nextInt(chance) == 0)
                    thisInts[(xi + zi * xSize)] = childInts[(xi + zi * xSize)] | LandBit;
                else
                    thisInts[(xi + zi * xSize)] = childInts[(xi + zi * xSize)];
            }
        }

        return thisInts;
    }

}
