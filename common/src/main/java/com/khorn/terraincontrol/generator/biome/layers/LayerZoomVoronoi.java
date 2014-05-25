package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

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

        //>>	OLD
//        x -= 2;
//        z -= 2;
//        int i = 2;
//        int j = 1 << i;
//        int k = x >> i;
//        int m = z >> i;
//        int n = (xSize >> i) + 3;
//        int i1 = (zSize >> i) + 3;
//        int[] arrayOfInt1 = this.child.getInts(arraysCache, k, m, n, i1);
//
//        int i2 = n << i;
//        int i3 = i1 << i;
//        int[] arrayOfInt2 = arraysCache.GetArray( i2 * i3);
//        for (int i4 = 0; i4 < i1 - 1; i4++)
//        {
//            int i5 = arrayOfInt1[((i4) * n)];
//            int i6 = arrayOfInt1[((i4 + 1) * n)];
//            for (int i7 = 0; i7 < n - 1; i7++)
//            {
//                double d1 = j * 0.9D;
//                initChunkSeed(i7 + k << i, i4 + m << i);
//                double d2 = (nextInt(1024) / 1024.0D - 0.5D) * d1;
//                double d3 = (nextInt(1024) / 1024.0D - 0.5D) * d1;
//                initChunkSeed(i7 + k + 1 << i, i4 + m << i);
//                double d4 = (nextInt(1024) / 1024.0D - 0.5D) * d1 + j;
//                double d5 = (nextInt(1024) / 1024.0D - 0.5D) * d1;
//                initChunkSeed(i7 + k << i, i4 + m + 1 << i);
//                double d6 = (nextInt(1024) / 1024.0D - 0.5D) * d1;
//                double d7 = (nextInt(1024) / 1024.0D - 0.5D) * d1 + j;
//                initChunkSeed(i7 + k + 1 << i, i4 + m + 1 << i);
//                double d8 = (nextInt(1024) / 1024.0D - 0.5D) * d1 + j;
//                double d9 = (nextInt(1024) / 1024.0D - 0.5D) * d1 + j;
//
//                int i8 = arrayOfInt1[(i7 + 1 + (i4) * n)];
//                int i9 = arrayOfInt1[(i7 + 1 + (i4 + 1) * n)];
//
//                for (int i10 = 0; i10 < j; i10++)
//                {
//                    int i11 = ((i4 << i) + i10) * i2 + (i7 << i);
//                    for (int i12 = 0; i12 < j; i12++)
//                    {
//                        double d10 = (i10 - d3) * (i10 - d3) + (i12 - d2) * (i12 - d2);
//                        double d11 = (i10 - d5) * (i10 - d5) + (i12 - d4) * (i12 - d4);
//                        double d12 = (i10 - d7) * (i10 - d7) + (i12 - d6) * (i12 - d6);
//                        double d13 = (i10 - d9) * (i10 - d9) + (i12 - d8) * (i12 - d8);
//
//                        if ((d10 < d11) && (d10 < d12) && (d10 < d13))
//                            arrayOfInt2[(i11++)] = i5;
//                        else if ((d11 < d10) && (d11 < d12) && (d11 < d13))
//                            arrayOfInt2[(i11++)] = i8;
//                        else if ((d12 < d10) && (d12 < d11) && (d12 < d13))
//                            arrayOfInt2[(i11++)] = i6;
//                        else
//                        {
//                            arrayOfInt2[(i11++)] = i9;
//                        }
//                    }
//                }
//
//                i5 = i8;
//                i6 = i9;
//            }
//        }
//        int[] arrayOfInt3 = arraysCache.GetArray( xSize * zSize);
//        for (int i5 = 0; i5 < zSize; i5++)
//        {
//            System.arraycopy(arrayOfInt2, (i5 + (z & j - 1)) * (n << i) + (x & j - 1), arrayOfInt3, i5 * xSize, xSize);
//        }
//        return arrayOfInt3;
    }

}
