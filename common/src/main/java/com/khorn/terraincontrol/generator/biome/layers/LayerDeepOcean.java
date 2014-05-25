package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerDeepOcean extends Layer
{

    public LayerDeepOcean(long seed, Layer childLayer)
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
                int northCheck = childInts[xi + 1 + (zi + 1 - 1) * (xSize + 2)];
                int southCheck = childInts[xi + 1 + (zi + 1 + 1) * (xSize + 2)];
                int eastCheck = childInts[xi + 1 + 1 + (zi + 1) * (xSize + 2)];
                int westCheck = childInts[xi + 1 - 1 + (zi + 1) * (xSize + 2)];
                int centerCheck = childInts[xi + 1 + (zi + 1) * xSize0];

                int count = 0;
                if (northCheck == 0)
                {
                    ++count;
                }

                if (eastCheck == 0)
                {
                    ++count;
                }

                if (westCheck == 0)
                {
                    ++count;
                }

                if (southCheck == 0)
                {
                    ++count;
                }

                if (centerCheck == 0 && count > 3)
                {
                    thisInts[xi + zi * xSize] = DefaultBiome.DEEP_OCEAN.Id;
                } else
                {
                    thisInts[xi + zi * xSize] = centerCheck;
                }
            }
        }

        return thisInts;
    }

}
