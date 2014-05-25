package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public class LayerMixWithRiver extends Layer
{

    private WorldSettings configs;
    private int[] riverBiomes;
    private Layer biomePatternGeneratorChain;
    private Layer riverPatternGeneratorChain;

    public LayerMixWithRiver(long seed, Layer childLayer, Layer riverLayer, WorldSettings configs, LocalWorld world)
    {
        super(seed);
        //>>	This is how MCP code shows the river mix as opposed to using the child layer
        this.child = childLayer;
        this.biomePatternGeneratorChain = childLayer;
        this.riverPatternGeneratorChain = riverLayer;
        //>>	TC Specific Things
        this.configs = configs;
        this.riverBiomes = new int[world.getMaxBiomesCount()];

        for (int id = 0; id < this.riverBiomes.length; id++)
        {
            LocalBiome biome = configs.biomes[id];
            if (biome == null || biome.getBiomeConfig().riverBiome.isEmpty())
            {
                this.riverBiomes[id] = -1;
            } else
            {
                this.riverBiomes[id] = world.getBiomeByName(biome.getBiomeConfig().riverBiome).getIds().getGenerationId();
            }
        }
    }

    @Override
    public void initWorldGenSeed(long seed)
    {
        //>>	This is how MCP code shows the river mix as opposed to using the child layer
        biomePatternGeneratorChain.initWorldGenSeed(seed/** / + 31337/**/);
        riverPatternGeneratorChain.initWorldGenSeed(seed/** / + 31337/**/);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        switch (cache.outputType)
        {
            case FULL:
                return this.GetFull(cache, x, z, xSize, zSize);
            case WITHOUT_RIVERS:
                return this.GetWithoutRivers(cache, x, z, xSize, zSize);
            case ONLY_RIVERS:
                return this.GetOnlyRivers(cache, x, z, xSize, zSize);
            default:
                throw new UnsupportedOperationException("Unknown/invalid output type: " + cache.outputType);
        }
    }

    private int[] GetFull(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] biomeInts = this.biomePatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] riverInts = this.riverPatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);
        WorldConfig worldConfig = this.configs.worldConfig;

        int selectionBiome;
        int selectionRiver;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selectionBiome = biomeInts[(xi + zi * xSize)];
                selectionRiver = riverInts[(xi + zi * xSize)];

                if ((selectionBiome & LandBit) != 0)
                    preFinalBiome = selectionBiome & BiomeBits;
                else if (worldConfig.FrozenOcean && (selectionBiome & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (selectionRiver & RiverBits) != 0 && !this.configs.biomes[preFinalBiome].getBiomeConfig().riverBiome.isEmpty())
                    selectionBiome = this.riverBiomes[preFinalBiome];
                else
                    selectionBiome = preFinalBiome;

                thisInts[(xi + zi * xSize)] = selectionBiome;
            }
        }

        return thisInts;

    }

    private int[] GetWithoutRivers(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] biomeInts = this.biomePatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        // int[] riverInts = this.riverPatternGeneratorChain.getInts(arraysCache, x, z, x_size, z_size);
        int[] thisInts = cache.getArray(xSize * zSize);
        WorldConfig worldConfig = this.configs.worldConfig;

        int selectionBiome;
//        int selectionRiver;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selectionBiome = biomeInts[(xi + zi * xSize)];
//                 selectionRiver = riverInts[(j + i * x_size)];

                if ((selectionBiome & LandBit) != 0)
                    preFinalBiome = selectionBiome & BiomeBits;
                else if (worldConfig.FrozenOcean && (selectionBiome & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                /** /if (worldConfig.riversEnabled && (selectionRiver & RiverBits) != 0 && !worldConfig.biomeConfigs[cachedId].riverBiome.isEmpty())
                   selectionBiome = this.riverBiomes[cachedId];
                 else/* */
                selectionBiome = preFinalBiome;

                thisInts[(xi + zi * xSize)] = selectionBiome;
            }
        }

        return thisInts;
    }

    private int[] GetOnlyRivers(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] biomeInts = this.biomePatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] riverInts = this.riverPatternGeneratorChain.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        WorldConfig worldConfig = this.configs.worldConfig;

        int selectionBiome;
        int selectionRiver;
        int preFinalBiome;
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                selectionBiome = biomeInts[(xi + zi * xSize)];
                selectionRiver = riverInts[(xi + zi * xSize)];

                if ((selectionBiome & LandBit) != 0)
                    preFinalBiome = selectionBiome & BiomeBits;
                else if (worldConfig.FrozenOcean && (selectionBiome & IceBit) != 0)
                    preFinalBiome = DefaultBiome.FROZEN_OCEAN.Id;
                else
                    preFinalBiome = DefaultBiome.OCEAN.Id;

                if (worldConfig.riversEnabled && (selectionRiver & RiverBits) != 0 && !this.configs.biomes[preFinalBiome].getBiomeConfig().riverBiome.isEmpty())
                    selectionBiome = 1;
                else
                    selectionBiome = 0;

                thisInts[(xi + zi * xSize)] = selectionBiome;
            }
        }

        return thisInts;
    }

}
