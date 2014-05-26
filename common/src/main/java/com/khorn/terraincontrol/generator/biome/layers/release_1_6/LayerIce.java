package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.IceBit;

public class LayerIce extends Layer
{

    public int rarity = 5;

    public LayerIce(long seed, Layer childLayer, int _rarity)
    {
        super(seed);
        this.child = childLayer;
        this.rarity = 101 - _rarity;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);

        int[] thisInts = cache.getArray(xSize * zSize);
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                initChunkSeed(z + zi, x + xi);      // reversed, again why? ... >.>
                thisInts[(xi + zi * xSize)] = (nextInt(rarity) == 0 ? (childInts[(xi + zi * xSize)] | IceBit) : childInts[(xi + zi * xSize)]);
            }
        }

        return thisInts;
    }

}
