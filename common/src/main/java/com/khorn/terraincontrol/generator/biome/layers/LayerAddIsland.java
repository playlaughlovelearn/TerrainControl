package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerAddIsland extends Layer
{

    public LayerAddIsland(long seed, Layer childLayer)
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
                int nwCheck = childInts[xi + 0 + (zi + 0) * xSize0];
                int neCheck = childInts[xi + 2 + (zi + 0) * xSize0];
                int swCheck = childInts[xi + 0 + (zi + 2) * xSize0];
                int seCheck = childInts[xi + 2 + (zi + 2) * xSize0];
                int centerCheck = childInts[xi + 1 + (zi + 1) * xSize0];
                this.initChunkSeed((long) (xi + x), (long) (zi + z));

                if (centerCheck == 0 && (nwCheck != 0 || neCheck != 0 || swCheck != 0 || seCheck != 0))
                {
                    int chance = 1;
                    int chanceOut = 1;

                    if (nwCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = nwCheck;
                    }

                    if (neCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = neCheck;
                    }

                    if (swCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = swCheck;
                    }

                    if (seCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = seCheck;
                    }

                    if (this.nextInt(3) == 0)
                    {
                        thisInts[xi + zi * xSize] = chanceOut;
                    } else if (chanceOut == 4)
                    {
                        thisInts[xi + zi * xSize] = 4;
                    } else
                    {
                        thisInts[xi + zi * xSize] = 0;
                    }
                } else if (centerCheck > 0 && (nwCheck == 0 || neCheck == 0 || swCheck == 0 || seCheck == 0))
                {
                    if (this.nextInt(5) == 0)
                    {
                        if (centerCheck == 4)
                        {
                            thisInts[xi + zi * xSize] = 4;
                        } else
                        {
                            thisInts[xi + zi * xSize] = 0;
                        }
                    } else
                    {
                        thisInts[xi + zi * xSize] = centerCheck;
                    }
                } else
                {
                    thisInts[xi + zi * xSize] = centerCheck;
                }
            }
        }

        return thisInts;
    }

}
