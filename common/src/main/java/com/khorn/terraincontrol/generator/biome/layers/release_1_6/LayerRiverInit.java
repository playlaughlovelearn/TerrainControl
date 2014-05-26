package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.RiverBitOne;
import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.RiverBitTwo;

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
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);
        
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                initChunkSeed(zi + z, xi + x);           // reversed, but why?
                int currentPiece = childInts[(xi + zi * xSize)];
                if (nextInt(2) == 0)
                    currentPiece |= RiverBitOne;
                else
                    currentPiece |= RiverBitTwo;

                thisInts[(xi + zi * xSize)] = currentPiece;
            }
        }

        return thisInts;
    }

}
