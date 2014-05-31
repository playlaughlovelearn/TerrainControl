package com.khorn.terraincontrol.generator.biome.layers.release_1_7;

import com.khorn.terraincontrol.generator.biome.layers.LayerSmooth;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.generator.biome.layers.Layer;
import com.khorn.terraincontrol.logging.LogMarker;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

public abstract class LayerR17 extends Layer
{

    public static Layer[] Init(long seed, LocalWorld world)
    {
        boolean flag = false;
        //>>	Start off by adding BiomeGroup1 to 1/10th of the map
        LayerIsland ML = new LayerIsland(1L);
        //>>	Zoom in an unknown amount and be a little messy about it
        LayerZoomFuzzy ML2 = new LayerZoomFuzzy(2000L, ML);
        //>>	Add BiomeGroup 1/2 in island fashion
        LayerExpandLand ML3 = new LayerExpandLand(1L, ML2);
        //>>	Zoom in with a preference towards the majority
        LayerZoom ML3a1 = new LayerZoom(2001L, ML3);
        //>>	More adding BG 1/2 fashion
        ML3 = new LayerExpandLand(2L, ML3a1);
        ML3 = new LayerExpandLand(50L, ML3);
        ML3 = new LayerExpandLand(70L, ML3);
        //>>	Check for BG 0 (Ocean) and set BG 1 if too much BG 0 is found
        LayerRemoveTooMuchOcean ML3b1 = new LayerRemoveTooMuchOcean(2L, ML3);
        
        LayerAddColdBiomeGroups ML4 = new LayerAddColdBiomeGroups(2L, ML3b1);
        ML3 = new LayerExpandLand(3L, ML4);
        LayerEdge ML3c1 = LayerEdge.allEdges(2L, ML3);
        ML3a1 = (LayerZoom) LayerZoom.magnify(2002L, ML3c1, 2);
        ML3 = new LayerExpandLand(4L, ML3a1);
        LayerAddMushroomIsland ML3d1 = new LayerAddMushroomIsland(5L, ML3);
        LayerDeepOcean ML3d2 = new LayerDeepOcean(4L, ML3d1);
        Layer ML3d3 = LayerZoom.magnify(1000L, ML3d2, 0);
        byte zoomAmount = 4;

//        if (worldType == WorldType.LARGE_BIOMES) {
//            zoomAmount = 6;
//        }
        if (flag)
        {
            zoomAmount = 4;
        }

        Layer RL1 = LayerZoom.magnify(1000L, ML3d3, 0);
        LayerRiverInit RL1a = new LayerRiverInit(100L, RL1);
        Object ML3d4 = new LayerBiome(200L, ML3d3);

        if (!flag)
        {
            Layer ML3d5 = LayerZoom.magnify(1000L, (Layer) ML3d4, 2);
            ML3d4 = new LayerBiomeEdge(1000L, ML3d5);
        }

        Layer RL1a1 = LayerZoom.magnify(1000L, RL1a, 2);
        LayerAddSubBiomes ML3d5 = new LayerAddSubBiomes(1000L, (Layer) ML3d4, RL1a1);
        RL1 = LayerZoom.magnify(1000L, RL1a, 2);
        RL1 = LayerZoom.magnify(1000L, RL1, zoomAmount);
        LayerRiver RL2b = new LayerRiver(1L, RL1);
        LayerSmooth RL2b1 = new LayerSmooth(1000L, RL2b);
        ML3d4 = new LayerSunflowerPlains(1001L, ML3d5);

        for (int zAi = 0; zAi < zoomAmount; ++zAi)
        {
            ML3d4 = new LayerZoom((long) (1000 + zAi), (Layer) ML3d4);

            if (zAi == 0)
            {
                ML3d4 = new LayerExpandLand(3L, (Layer) ML3d4);
            }

            if (zAi == 1)
            {
                ML3d4 = new LayerShore(1000L, (Layer) ML3d4);
            }
        }

        LayerSmooth ML3d6 = new LayerSmooth(1000L, (Layer) ML3d4);
        LayerRiverMix unZoomL1 = new LayerRiverMix(100L, ML3d6, RL2b1);
        LayerZoomVoronoi BioL1 = new LayerZoomVoronoi(10L, unZoomL1);
        unZoomL1.initWorldGenSeed(seed);
        BioL1.initWorldGenSeed(seed);
        return new Layer[]
        {
            unZoomL1, BioL1
        };
    }

    public LayerR17(long seed)
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

    protected static boolean isOcean(int biomeID)
    {
        return biomeID == DefaultBiome.OCEAN.Id || biomeID == DefaultBiome.DEEP_OCEAN.Id || biomeID == DefaultBiome.FROZEN_OCEAN.Id;
    }

}
