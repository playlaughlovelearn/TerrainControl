package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerEdge extends LayerR17
{

    private final LayerEdge.Mode edgeMode;

    public LayerEdge(long seed, Layer childLayer, LayerEdge.Mode edgeMode)
    {
        super(seed);
        this.child = childLayer;
        this.edgeMode = edgeMode;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        switch (LayerEdge.SwitchMode.modes[this.edgeMode.ordinal()])
        {
            case 1:
            default:
                return this.getCOLD_WARMInts(cache, x, z, xSize, zSize);

            case 2:
                return this.getHEAT_ICEInts(cache, x, z, xSize, zSize);

            case 3:
                return this.getSPECIALInts(cache, x, z, xSize, zSize);
        }
    }

    private int[] getCOLD_WARMInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int x0 = x - 1;
        int z0 = z - 1;
        int xSize0 = 1 + xSize + 1;
        int zSize0 = 1 + zSize + 1;
        int[] childInts = this.child.getInts(cache, x0, z0, xSize0, zSize0);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                this.initChunkSeed((long) (xi + x), (long) (zi + z));
                int selection = childInts[xi + 1 + (zi + 1) * xSize0];

                if (selection == 1)
                {
                    int northCheck = childInts[xi + 1 + (zi + 1 - 1) * xSize0];
                    int southCheck = childInts[xi + 1 + (zi + 1 + 1) * xSize0];
                    int eastCheck = childInts[xi + 1 + 1 + (zi + 1) * xSize0];
                    int westCheck = childInts[xi + 1 - 1 + (zi + 1) * xSize0];
                    boolean threePresent = northCheck == 3 || eastCheck == 3 || westCheck == 3 || southCheck == 3;
                    boolean fourPresent = northCheck == 4 || eastCheck == 4 || westCheck == 4 || southCheck == 4;

                    if (threePresent || fourPresent)
                    {
                        selection = 2;
                    }
                }

                thisInts[xi + zi * xSize] = selection;
            }
        }

        return thisInts;
    }

    private int[] getHEAT_ICEInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int x0 = x - 1;
        int z0 = z - 1;
        int xSise0 = 1 + xSize + 1;
        int zSize0 = 1 + zSize + 1;
        int[] childInts = this.child.getInts(cache, x0, z0, xSise0, zSize0);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                int selection = childInts[xi + 1 + (zi + 1) * xSise0];

                if (selection == 4)
                {
                    int northCheck = childInts[xi + 1 + (zi + 1 - 1) * xSise0];
                    int southCheck = childInts[xi + 1 + (zi + 1 + 1) * xSise0];
                    int eastCheck = childInts[xi + 1 + 1 + (zi + 1) * xSise0];
                    int westCheck = childInts[xi + 1 - 1 + (zi + 1) * xSise0];
                    boolean twoPresent = northCheck == 2 || eastCheck == 2 || westCheck == 2 || southCheck == 2;
                    boolean onePresent = northCheck == 1 || eastCheck == 1 || westCheck == 1 || southCheck == 1;

                    if (onePresent || twoPresent)
                    {
                        selection = 3;
                    }
                }

                thisInts[xi + zi * xSize] = selection;
            }
        }

        return thisInts;
    }

    private int[] getSPECIALInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; ++zi)
        {
            for (int xi = 0; xi < xSize; ++xi)
            {
                this.initChunkSeed((long) (xi + x), (long) (zi + z));
                int selection = childInts[xi + zi * xSize];

                if (selection != 0 && this.nextInt(13) == 0)
                {
                    selection |= 1 + this.nextInt(15) << 8 & 3840;
                }

                thisInts[xi + zi * xSize] = selection;
            }
        }

        return thisInts;
    }

    static final class SwitchMode
    {

        static final int[] modes = new int[LayerEdge.Mode.values().length];

        static
        {
            try
            {
                modes[LayerEdge.Mode.COOL_WARM.ordinal()] = 1;
            } catch (NoSuchFieldError var3)
            {
            }

            try
            {
                modes[LayerEdge.Mode.HEAT_ICE.ordinal()] = 2;
            } catch (NoSuchFieldError var2)
            {
            }

            try
            {
                modes[LayerEdge.Mode.SPECIAL.ordinal()] = 3;
            } catch (NoSuchFieldError var1)
            {
            }
        }

    }

    public static enum Mode
    {

        COOL_WARM("COOL_WARM", 0),
        HEAT_ICE("HEAT_ICE", 1),
        SPECIAL("SPECIAL", 2);

        private static final LayerEdge.Mode[] $VALUES = new LayerEdge.Mode[]
        {
            COOL_WARM, HEAT_ICE, SPECIAL
        };

        private Mode(String name, int id)
        {
        }

    }

}
