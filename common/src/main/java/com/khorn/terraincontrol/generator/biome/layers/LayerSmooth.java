package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerSmooth extends Layer
{

    public LayerSmooth(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache arraysCache, int x, int z, int xSize, int zSize)
    {

        int x0 = x - 1;
        int z0 = z - 1;
        int xSize0 = xSize + 2;
        int zSize0 = zSize + 2;

        int[] childInts = this.child.getInts(arraysCache, x0, z0, xSize0, zSize0);
        int[] thisInts = arraysCache.GetArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                int a = childInts[xi + 0 + (zi + 1) * xSize0];
                int b = childInts[xi + 2 + (zi + 1) * xSize0];
                int c = childInts[xi + 1 + (zi + 0) * xSize0];
                int d = childInts[xi + 1 + (zi + 2) * xSize0];
                int abcd = childInts[xi + 1 + (zi + 1) * xSize0];

                if (a == b && c == d)
                {
                    this.initChunkSeed((long) (xi + x), (long) (zi + z));

                    if (this.nextInt(2) == 0)
                    {
                        abcd = a;
                    } else
                    {
                        abcd = c;
                    }
                } else
                {
                    if (a == b)
                    {
                        abcd = a;
                    }

                    if (c == d)
                    {
                        abcd = c;
                    }
                }

                thisInts[xi + zi * xSize] = abcd;
            }
        }

        return thisInts;
    }

}
