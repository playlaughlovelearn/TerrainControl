package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.BiomeConfig;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.layers.Layer;
import com.khorn.terraincontrol.generator.biome.layers.LayerFromImage;
import com.khorn.terraincontrol.generator.biome.layers.LayerSmooth;
import com.khorn.terraincontrol.logging.LogMarker;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

import java.util.ArrayList;

/**
 * This class is kept here for reference during the BiomeGroups feature is
 * implemented
 */
public abstract class LayerR16 extends Layer
{

    protected static int GetBiomeFromLayer(int BiomeAndLand)
    {
        if ((BiomeAndLand & LandBit) != 0)
            return (BiomeAndLand & BiomeBits);
        return 0;
    }

    public static Layer[] Init(long seed, LocalWorld world)
    {

        /*
         * int BigLandSize = 2; //default 0, more - smaller
         * int ChanceToIncreaseLand = 6; //default 4
         * int MaxDepth = 10;
         */
        WorldSettings configs = world.getSettings();
        WorldConfig worldConfig = configs.worldConfig;

        LocalBiome[][] NormalBiomeMap = new LocalBiome[worldConfig.GenerationDepth + 1][];
        LocalBiome[][] IceBiomeMap = new LocalBiome[worldConfig.GenerationDepth + 1][];

        //>>	GENERATION DEPTH, BIOME RARITY
        for (int i = 0; i < worldConfig.GenerationDepth + 1; i++)
        {
            ArrayList<LocalBiome> normalBiomes = new ArrayList<LocalBiome>();
            ArrayList<LocalBiome> iceBiomes = new ArrayList<LocalBiome>();
            for (LocalBiome biome : configs.biomes)
            {
                if (biome == null)
                    continue;

                BiomeConfig biomeConfig = biome.getBiomeConfig();

                if (biomeConfig.biomeSize != i)
                    continue;
                if (worldConfig.NormalBiomes.contains(biomeConfig.name))
                {
                    for (int t = 0; t < biomeConfig.biomeRarity; t++)
                        normalBiomes.add(biome);
                    worldConfig.normalBiomesRarity -= biomeConfig.biomeRarity;
                }

                if (worldConfig.IceBiomes.contains(biomeConfig.name))
                {
                    for (int t = 0; t < biomeConfig.biomeRarity; t++)
                        iceBiomes.add(biome);
                    worldConfig.iceBiomesRarity -= biomeConfig.biomeRarity;
                }

            }

            if (!normalBiomes.isEmpty())
                NormalBiomeMap[i] = normalBiomes.toArray(new LocalBiome[normalBiomes.size() + worldConfig.normalBiomesRarity]);
            else
                NormalBiomeMap[i] = new LocalBiome[0];

            if (!iceBiomes.isEmpty())
                IceBiomeMap[i] = iceBiomes.toArray(new LocalBiome[iceBiomes.size() + worldConfig.iceBiomesRarity]);
            else
                IceBiomeMap[i] = new LocalBiome[0];
        }
        //>>	>>END

        //>>	Start of MCP function similarity GenLayer.initializeAllBomeGenerators on line 40
        Layer MainLayer = new LayerEmpty(1L);

        Layer RiverLayer = new LayerEmpty(1L);
        boolean riversStarted = false;

        for (int depth = 0; depth <= worldConfig.GenerationDepth; depth++)
        {

            MainLayer = new LayerZoom(2001 + depth, MainLayer);

            if (worldConfig.randomRivers && riversStarted)
                RiverLayer = new LayerZoom(2001 + depth, RiverLayer);

            if (worldConfig.LandSize == depth)
            {
                MainLayer = new LayerLand(1L, MainLayer, worldConfig.LandRarity);
                MainLayer = new LayerZoomFuzzy(2000L, MainLayer);
            }

            if (depth < (worldConfig.LandSize + worldConfig.LandFuzzy))
                MainLayer = new LayerLandRandom(depth, MainLayer);

            if (NormalBiomeMap[depth].length != 0 || IceBiomeMap[depth].length != 0)
            {

                LayerBiome layerBiome = new LayerBiome(200, MainLayer);
                layerBiome.biomes = NormalBiomeMap[depth];
                layerBiome.ice_biomes = IceBiomeMap[depth];
                MainLayer = layerBiome;
            }

            if (worldConfig.IceSize == depth)
                MainLayer = new LayerIce(depth, MainLayer, worldConfig.IceRarity);

            if (worldConfig.riverRarity == depth)
                if (worldConfig.randomRivers)
                {
                    RiverLayer = new LayerRiverInit(155, RiverLayer);
                    riversStarted = true;
                } else
                    MainLayer = new LayerRiverInit(155, MainLayer);

            if ((worldConfig.GenerationDepth - worldConfig.riverSize) == depth)
            {
                if (worldConfig.randomRivers)
                    RiverLayer = new LayerRiver(5 + depth, RiverLayer);
                else
                    MainLayer = new LayerRiver(5 + depth, MainLayer);
            }

            LayerBiomeBorder layerBiomeBorder = new LayerBiomeBorder(3000 + depth, world);
            boolean haveBorder = false;
            for (LocalBiome biome : configs.biomes)
            {
                if (biome == null)
                    continue;
                BiomeConfig biomeConfig = biome.getBiomeConfig();
                if (biomeConfig.biomeSize != depth)
                    continue;
                if (worldConfig.IsleBiomes.contains(biomeConfig.name) && biomeConfig.isleInBiome != null)
                {
                    int id = biome.getIds().getGenerationId();

                    LayerBiomeInBiome layerBiome = new LayerBiomeInBiome(4000 + id, MainLayer);
                    layerBiome.biome = biome;
                    for (String islandInName : biomeConfig.isleInBiome)
                    {
                        int islandIn = world.getBiomeByName(islandInName).getIds().getGenerationId();
                        if (islandIn == DefaultBiome.OCEAN.Id)
                            layerBiome.inOcean = true;
                        else
                            layerBiome.BiomeIsles[islandIn] = true;
                    }

                    layerBiome.chance = (worldConfig.BiomeRarityScale + 1) - biomeConfig.biomeRarity;
                    MainLayer = layerBiome;
                }

                if (worldConfig.BorderBiomes.contains(biomeConfig.name) && biomeConfig.biomeIsBorder != null)
                {
                    haveBorder = true;

                    for (String replaceFromName : biomeConfig.biomeIsBorder)
                    {
                        int replaceFrom = world.getBiomeByName(replaceFromName).getIds().getGenerationId();
                        layerBiomeBorder.AddBiome(biome, replaceFrom, world);

                    }

                }
            }

            if (haveBorder)
            {
                layerBiomeBorder.child = MainLayer;
                MainLayer = layerBiomeBorder;
            }

        }
        if (worldConfig.randomRivers)
            MainLayer = new LayerRiverMix(1L, MainLayer, RiverLayer/**/, configs, world/**/);
        else
            MainLayer = new LayerMix(1L, MainLayer, configs, world);

        MainLayer = new LayerSmooth(400L, MainLayer);

        if (worldConfig.biomeMode == TerrainControl.getBiomeModeManager().FROM_IMAGE)
        {

            if (worldConfig.imageMode == WorldConfig.ImageMode.ContinueNormal)
                MainLayer = new LayerFromImage(1L, MainLayer, worldConfig, world);
            else
                MainLayer = new LayerFromImage(1L, null, worldConfig, world);
        }

        Layer ZoomedLayer = new LayerZoomVoronoi(10L, MainLayer);

        //TemperatureLayer = new LayerTemperatureMix(TemperatureLayer, ZoomedLayer, 0, config);
        ZoomedLayer.initWorldGenSeed(seed);

        //MainLayer = new LayerCacheInit(1, MainLayer);
        //ZoomedLayer = new LayerCacheInit(1, ZoomedLayer);
        return new Layer[]
        {
            MainLayer, ZoomedLayer
        };
    }

    public LayerR16(long seed)
    {
        super(seed);
    }

    protected static boolean compareBiomes(final int biome_A_ID, final int biome_B_ID)
    {
        if (biome_A_ID == biome_B_ID)
        {
            return true;
        } else if (biome_A_ID != DefaultBiome.MESA_PLATEAU_FOREST.Id && biome_A_ID != DefaultBiome.MESA_PLATEAU.Id)
        {
            try
            {
                return DefaultBiome.getBiome(biome_A_ID) != null && DefaultBiome.getBiome(biome_B_ID) != null ? DefaultBiome.getBiome(biome_A_ID).equals(DefaultBiome.getBiome(biome_B_ID)) : false;
            } catch (Throwable ex)
            {
                TerrainControl.printStackTrace(LogMarker.FATAL, ex);
            }
        }
        return biome_B_ID == DefaultBiome.MESA_PLATEAU_FOREST.Id || biome_B_ID == DefaultBiome.MESA_PLATEAU.Id;
    }

}
