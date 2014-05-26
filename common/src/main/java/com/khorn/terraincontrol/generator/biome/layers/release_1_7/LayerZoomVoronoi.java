package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerZoomVoronoi extends Layer
{

    public LayerZoomVoronoi(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        x -= 2;
        z -= 2;
        int x0 = x >> 2;
        int z0 = z >> 2;
        int xSize0 = (xSize >> 2) + 2;
        int zSize0 = (zSize >> 2) + 2;
        int[] childInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int xSize1 = xSize0 - 1 << 2;
        int zSize1 = zSize0 - 1 << 2;
        int[] thisInts = cache.getArray(xSize1 * zSize1);
        int ci;

        for (int zi = 0; zi < zSize0 - 1; ++zi)
        {
            ci = 0;
            int var15 = childInts[ci + 0 + (zi + 0) * xSize0];

            for (int xi = childInts[ci + 0 + (zi + 1) * xSize0]; ci < xSize0 - 1; ++ci)
            {
                double var17 = 3.6D;
                //>>	Corner 1
                this.initChunkSeed((long) (ci + x0 << 2), (long) (zi + z0 << 2));
                double c1x = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                double c1z = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                //>>	Corner 2
                this.initChunkSeed((long) (ci + x0 + 1 << 2), (long) (zi + z0 << 2));
                double c2x = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                double c2z = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                //>>	Corner 3
                this.initChunkSeed((long) (ci + x0 << 2), (long) (zi + z0 + 1 << 2));
                double c3x = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                double c3z = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                //>>	Corner 4
                this.initChunkSeed((long) (ci + x0 + 1 << 2), (long) (zi + z0 + 1 << 2));
                double c4x = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                double c4z = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;

                int var35 = childInts[ci + 1 + (zi + 0) * xSize0] & 255;
                int var36 = childInts[ci + 1 + (zi + 1) * xSize0] & 255;

                for (int var37 = 0; var37 < 4; ++var37)
                {
                    int var38 = ((zi << 2) + var37) * xSize1 + (ci << 2);

                    for (int var39 = 0; var39 < 4; ++var39)
                    {
                        double var40 = ((double) var37 - c1z) * ((double) var37 - c1z) + ((double) var39 - c1x) * ((double) var39 - c1x);
                        double var42 = ((double) var37 - c2z) * ((double) var37 - c2z) + ((double) var39 - c2x) * ((double) var39 - c2x);
                        double var44 = ((double) var37 - c3z) * ((double) var37 - c3z) + ((double) var39 - c3x) * ((double) var39 - c3x);
                        double var46 = ((double) var37 - c4z) * ((double) var37 - c4z) + ((double) var39 - c4x) * ((double) var39 - c4x);

                        if (var40 < var42 && var40 < var44 && var40 < var46)
                        {
                            thisInts[var38++] = var15;
                        } else if (var42 < var40 && var42 < var44 && var42 < var46)
                        {
                            thisInts[var38++] = var35;
                        } else if (var44 < var40 && var44 < var42 && var44 < var46)
                        {
                            thisInts[var38++] = xi;
                        } else
                        {
                            thisInts[var38++] = var36;
                        }
                    }
                }

                var15 = var35;
                xi = var36;
            }
        }

        int[] ret = cache.getArray(xSize * zSize);

        for (ci = 0; ci < zSize; ++ci)
        {
            System.arraycopy(thisInts, (ci + (z & 3)) * xSize1 + (x & 3), ret, ci * xSize, xSize);
        }

        return ret;
    }

}
