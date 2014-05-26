package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

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
