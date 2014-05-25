package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerZoom extends Layer
{

    public LayerZoom(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int x0 = x >> 1;
        int z0 = z >> 1;
        int xSize0 = (xSize >> 1) + 2;
        int zSize0 = (zSize >> 1) + 2;

        int[] childInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int xci = xSize0 - 1 << 1;
        int zci = zSize0 - 1 << 1;
        int[] thisInts = cache.getArray(xci * zci);
        int cbi; //>>	Cache base index

        for (int zi = 0; zi < zSize0 - 1; ++zi)
        {
            cbi = (zi << 1) * xci;
            int xi0 = 0;
            int childValue = childInts[xi0 + 0 + (zi + 0) * xSize0];

            for (int xi = childInts[xi0 + 0 + (zi + 1) * xSize0]; xi0 < xSize0 - 1; ++xi0)
            {
                this.initChunkSeed((long) (xi0 + x0 << 1), (long) (zi + z0 << 1));
                int var18 = childInts[xi0 + 1 + (zi + 0) * xSize0];
                int var19 = childInts[xi0 + 1 + (zi + 1) * xSize0];
                thisInts[cbi] = childValue;
                thisInts[cbi++ + xci] = this.getRandomInArray(new int[]
                {
                    childValue, xi
                });
                thisInts[cbi] = this.getRandomInArray(new int[]
                {
                    childValue, var18
                });
                thisInts[cbi++ + xci] = this.getRandomOf4(childValue, var18, xi, var19);
                childValue = var18;
                xi = var19;
            }
        }

        int[] ret = cache.getArray(xSize * zSize);

        for (cbi = 0; cbi < zSize; ++cbi)
        {
            System.arraycopy(thisInts, (cbi + (z & 1)) * xci + (x & 1), ret, cbi * xSize, xSize);
        }

        return ret;
    }

//>>	OLD
//    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
//    {
//        int x0 = x >> 1;
//        int z0 = z >> 1;
//        int xSize0 = (xSize >> 1) + 3;
//        int zSize0 = (zSize >> 1) + 3;
//        
//        int[] childInts = this.child.getInts(arraysCache, x0, z0, xSize0, zSize0);
//        int[] thisInts = arraysCache.GetArray( xSize0 * 2 * (zSize0 * 2));
//        
//        int n = xSize0 << 1;
//        int i2;
//        for (int i1 = 0; i1 < zSize0 - 1; i1++)
//        {
//            i2 = i1 << 1;
//            int i3 = i2 * n;
//            int i4 = childInts[((i1) * xSize0)];
//            int i5 = childInts[((i1 + 1) * xSize0)];
//            for (int i6 = 0; i6 < xSize0 - 1; i6++)
//            {
//                initChunkSeed((long) (i6 + x0 << 1), (long) (i1 + z0 << 1));
//                int i7 = childInts[(i6 + 1 + (i1) * xSize0)];
//                int i8 = childInts[(i6 + 1 + (i1 + 1) * xSize0)];
//
//                thisInts[i3] = i4;
//                thisInts[i3++ + n] = RndParam(i4, i5);
//                thisInts[i3] = RndParam(i4, i7);
//                thisInts[i3++ + n] = this.getRandomOf4(i4, i7, i5, i8);
//
//                i4 = i7;
//                i5 = i8;
//            }
//        }
//        int[] arrayOfInt3 = arraysCache.GetArray( xSize * zSize);
//        for (i2 = 0; i2 < zSize; i2++)
//        {
//            System.arraycopy(thisInts, (i2 + (z & 0x1)) * (xSize0 << 1) + (x & 0x1), arrayOfInt3, i2 * xSize, xSize);
//        }
//        return arrayOfInt3;
//    }
    /**
     * Magnify a layer. Parms are seed adjustment, layer, number of times to
     * magnify
     */
    public static Layer magnify(long seedAdjust, Layer layer, int magnification)
    {
        Object baseLayer = layer;
        for (int mi = 0; mi < magnification; ++mi)
            baseLayer = new LayerZoom(seedAdjust + (long) mi, (Layer) baseLayer);
        return (Layer) baseLayer;
    }

}
