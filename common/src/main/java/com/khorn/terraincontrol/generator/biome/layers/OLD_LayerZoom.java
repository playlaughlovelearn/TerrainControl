package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class OLD_LayerZoom extends Layer
{

    public OLD_LayerZoom(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int x0 = x >> 1;
        int z0 = z >> 1;
        int xSize0 = (xSize >> 1) + 3;
        int zSize0 = (zSize >> 1) + 3;

        int[] childInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int[] thisInts = cache.getArray(xSize0 * 2 * (zSize0 * 2));

        int n = xSize0 << 1;
        int i2;
        for (int i1 = 0; i1 < zSize0 - 1; i1++)
        {
            i2 = i1 << 1;
            int i3 = i2 * n;
            int i4 = childInts[((i1) * xSize0)];
            int i5 = childInts[((i1 + 1) * xSize0)];
            for (int i6 = 0; i6 < xSize0 - 1; i6++)
            {
                initChunkSeed((long) (i6 + x0 << 1), (long) (i1 + z0 << 1));
                int i7 = childInts[(i6 + 1 + (i1) * xSize0)];
                int i8 = childInts[(i6 + 1 + (i1 + 1) * xSize0)];

                thisInts[i3] = i4;
                thisInts[i3++ + n] = this.RndParam(i4, i5);
                thisInts[i3] = RndParam(i4, i7);
                thisInts[i3++ + n] = this.getRandomOf4(i4, i7, i5, i8);

                i4 = i7;
                i5 = i8;
            }
        }
        int[] arrayOfInt3 = cache.getArray(xSize * zSize);
        for (i2 = 0; i2 < zSize; i2++)
        {
            System.arraycopy(thisInts, (i2 + (z & 0x1)) * (xSize0 << 1) + (x & 0x1), arrayOfInt3, i2 * xSize, xSize);
        }
        return arrayOfInt3;
    }

    protected int RndParam(int paramInt1, int paramInt2)
    {
        return nextInt(2) == 0 ? paramInt1 : paramInt2;
    }

}
