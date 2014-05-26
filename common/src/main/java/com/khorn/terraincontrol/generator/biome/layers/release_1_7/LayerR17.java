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
        LayerIsland MainLayer = new LayerIsland(1L);
        LayerZoomFuzzy mlA = new LayerZoomFuzzy(2000L, MainLayer);
        LayerAddIsland mlB = new LayerAddIsland(1L, mlA);
        LayerZoom mlC = new LayerZoom(2001L, mlB);
        mlB = new LayerAddIsland(2L, mlC);
        mlB = new LayerAddIsland(50L, mlB);
        mlB = new LayerAddIsland(70L, mlB);
        LayerRemoveTooMuchOcean mlD = new LayerRemoveTooMuchOcean(2L, mlB);
        LayerAddSnow mlE = new LayerAddSnow(2L, mlD);
        mlB = new LayerAddIsland(3L, mlE);
        LayerEdge mlF = new LayerEdge(2L, mlB, LayerEdge.Mode.COOL_WARM);
        mlF = new LayerEdge(2L, mlF, LayerEdge.Mode.HEAT_ICE);
        mlF = new LayerEdge(3L, mlF, LayerEdge.Mode.SPECIAL);
        mlC = new LayerZoom(2002L, mlF);
        mlC = new LayerZoom(2003L, mlC);
        mlB = new LayerAddIsland(4L, mlC);
        LayerAddMushroomIsland mlG = new LayerAddMushroomIsland(5L, mlB);
        LayerDeepOcean mlH = new LayerDeepOcean(4L, mlG);
        Layer mlJ = LayerZoom.magnify(1000L, mlH, 0);
        byte var5 = 4;

//        if (worldType == WorldType.LARGE_BIOMES) {
//            var5 = 6;
//        }
        if (flag)
        {
            var5 = 4;
        }

        Layer mlK = LayerZoom.magnify(1000L, mlJ, 0);
        LayerRiverInit mlL = new LayerRiverInit(100L, mlK);
        Object mlM = new LayerBiome(200L, mlJ);

        if (!flag)
        {
            Layer mlN = LayerZoom.magnify(1000L, (Layer) mlM, 2);
            mlM = new LayerBiomeEdge(1000L, mlN);
        }

        Layer MlO = LayerZoom.magnify(1000L, mlL, 2);
        LayerHills mlP = new LayerHills(1000L, (Layer) mlM, MlO);
        mlK = LayerZoom.magnify(1000L, mlL, 2);
        mlK = LayerZoom.magnify(1000L, mlK, var5);
        LayerRiver mlQ = new LayerRiver(1L, mlK);
        LayerSmooth mlR = new LayerSmooth(1000L, mlQ);
        mlM = new LayerRareBiome(1001L, mlP);

        for (int var9 = 0; var9 < var5; ++var9)
        {
            mlM = new LayerZoom((long) (1000 + var9), (Layer) mlM);

            if (var9 == 0)
            {
                mlM = new LayerAddIsland(3L, (Layer) mlM);
            }

            if (var9 == 1)
            {
                mlM = new LayerShore(1000L, (Layer) mlM);
            }
        }

        LayerSmooth mlS = new LayerSmooth(1000L, (Layer) mlM);
        LayerRiverMix mlT = new LayerRiverMix(100L, mlS, mlR);
        LayerZoomVoronoi mlU = new LayerZoomVoronoi(10L, mlT);
        mlT.initWorldGenSeed(seed);
        mlU.initWorldGenSeed(seed);
        return new Layer[]
        {
            mlT, mlU, mlT
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
