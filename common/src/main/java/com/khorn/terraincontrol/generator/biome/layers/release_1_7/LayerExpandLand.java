package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerExpandLand extends LayerR17
{

    public LayerExpandLand(long seed, Layer childLayer)
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

                //>>	If the center is OCEAN and at least one of the other checks is NON-OCEAN
                if (centerCheck == 0 && (nwCheck != 0 || neCheck != 0 || swCheck != 0 || seCheck != 0))
                {
                    int chance = 1;
                    int chanceOut = 1;
                    //>>	100% on non-ocean
                    if (nwCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = nwCheck;
                    }
                    //>>	50% on non-ocean
                    if (neCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = neCheck;
                    }
                    //>>	33.3% on non-ocean
                    if (swCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = swCheck;
                    }
                    //>>	25% on non-ocean
                    if (seCheck != 0 && this.nextInt(chance++) == 0)
                    {
                        chanceOut = seCheck;
                    }
                    //>>	33.3% chance to set 
                    if (this.nextInt(3) == 0)
                    {   //>>	set chanceOut (one of the checks or plains)
                        thisInts[xi + zi * xSize] = chanceOut;
                    } else if (chanceOut == 4)
                    {   //>>	set forest, Land Tag?
                        thisInts[xi + zi * xSize] = 4;
                    } else
                    {   //>>	set Ocean
                        thisInts[xi + zi * xSize] = 0;
                    }
                    //>>	Otherwise, if center is NON-OCEAN and at least one other check is OCEAN
                } else if (centerCheck > 0 && (nwCheck == 0 || neCheck == 0 || swCheck == 0 || seCheck == 0))
                {   //>>	20% chance 
                    if (this.nextInt(5) == 0)
                    {   //>>	if center is Forest? Land Tag?
                        if (centerCheck == 4)
                        {   //>>	set forest? Land Tag?
                            thisInts[xi + zi * xSize] = 4;
                        } else
                        {   //>>	set ocean
                            thisInts[xi + zi * xSize] = 0;
                        }
                    } else
                    {   //>>	match centerCheck
                        thisInts[xi + zi * xSize] = centerCheck;
                    }
                } else
                {   //>>	match centerCheck
                    thisInts[xi + zi * xSize] = centerCheck;
                }
            }
        }

        return thisInts;
    }

}
