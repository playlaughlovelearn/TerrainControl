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
    public int[] getInts(ArraysCache arraysCache, int x, int z, int xSize, int zSize)
    {
        
        x -= 2;
        z -= 2;
        int cx = x >> 2;
        int cz = z >> 2;
        int cxSize = (xSize >> 2) + 2;
        int czSize = (zSize >> 2) + 2;
        int[] childInts = this.child.getInts(arraysCache, cx, cz, cxSize, czSize);
        int var10 = cxSize - 1 << 2;
        int var11 = czSize - 1 << 2;
        int[] thisInts = arraysCache.GetArray(var10 * var11);
        int ci;

        for (int zi = 0; zi < czSize - 1; ++zi) {
            ci = 0;
            int var15 = childInts[ci + 0 + (zi + 0) * cxSize];

            for (int xi = childInts[ci + 0 + (zi + 1) * cxSize]; ci < cxSize - 1; ++ci) {
                double var17 = 3.6D;
                this.initChunkSeed((long) (ci + cx << 2), (long) (zi + cz << 2));
                double var19 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                double var21 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                this.initChunkSeed((long) (ci + cx + 1 << 2), (long) (zi + cz << 2));
                double var23 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                double var25 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                this.initChunkSeed((long) (ci + cx << 2), (long) (zi + cz + 1 << 2));
                double var27 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17;
                double var29 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                this.initChunkSeed((long) (ci + cx + 1 << 2), (long) (zi + cz + 1 << 2));
                double var31 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                double var33 = ((double) this.nextInt(1024) / 1024.0D - 0.5D) * var17 + 4.0D;
                int var35 = childInts[ci + 1 + (zi + 0) * cxSize] & 255;
                int var36 = childInts[ci + 1 + (zi + 1) * cxSize] & 255;

                for (int var37 = 0; var37 < 4; ++var37) {
                    int var38 = ((zi << 2) + var37) * var10 + (ci << 2);

                    for (int var39 = 0; var39 < 4; ++var39) {
                        double var40 = ((double) var37 - var21) * ((double) var37 - var21) + ((double) var39 - var19) * ((double) var39 - var19);
                        double var42 = ((double) var37 - var25) * ((double) var37 - var25) + ((double) var39 - var23) * ((double) var39 - var23);
                        double var44 = ((double) var37 - var29) * ((double) var37 - var29) + ((double) var39 - var27) * ((double) var39 - var27);
                        double var46 = ((double) var37 - var33) * ((double) var37 - var33) + ((double) var39 - var31) * ((double) var39 - var31);

                        if (var40 < var42 && var40 < var44 && var40 < var46) {
                            thisInts[var38++] = var15;
                        } else if (var42 < var40 && var42 < var44 && var42 < var46) {
                            thisInts[var38++] = var35;
                        } else if (var44 < var40 && var44 < var42 && var44 < var46) {
                            thisInts[var38++] = xi;
                        } else {
                            thisInts[var38++] = var36;
                        }
                    }
                }

                var15 = var35;
                xi = var36;
            }
        }

        int[] ret = arraysCache.GetArray(xSize * zSize);

        for (ci = 0; ci < zSize; ++ci) {
            System.arraycopy(thisInts, (ci + (z & 3)) * var10 + (x & 3), ret, ci * xSize, xSize);
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