package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerRiverMix extends Layer
{

    private Layer biomePatternGeneratorChain;
    private Layer riverPatternGeneratorChain;

    public LayerRiverMix(long seed, Layer childLayer, Layer riverLayer)
    {
        super(seed);
        this.biomePatternGeneratorChain = childLayer;
        this.riverPatternGeneratorChain = riverLayer;
    }

    @Override
    public void initWorldGenSeed(long seed)
    {
        //>>	This is how MCP code shows the river mix as opposed to using the child layer
        biomePatternGeneratorChain.initWorldGenSeed(seed);
        riverPatternGeneratorChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize) {
        int[] biomeInts = this.biomePatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] riverInts = this.riverPatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int cdi = 0; cdi < xSize * zSize; ++cdi) {
            if (biomeInts[cdi] != DefaultBiome.OCEAN.Id && biomeInts[cdi] != DefaultBiome.DEEP_OCEAN.Id) {
                if (riverInts[cdi] == DefaultBiome.RIVER.Id) {
                    if (biomeInts[cdi] == DefaultBiome.ICE_PLAINS.Id) {
                        thisInts[cdi] = DefaultBiome.FROZEN_RIVER.Id;
                    } else if (biomeInts[cdi] != DefaultBiome.MUSHROOM_ISLAND.Id && biomeInts[cdi] != DefaultBiome.MUSHROOM_ISLAND_SHORE.Id) {
                        thisInts[cdi] = riverInts[cdi] & 255;
                    } else {
                        thisInts[cdi] = DefaultBiome.MUSHROOM_ISLAND_SHORE.Id;
                    }
                } else {
                    thisInts[cdi] = biomeInts[cdi];
                }
            } else {
                thisInts[cdi] = biomeInts[cdi];
            }
        }

        return thisInts;
    }

}
