package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.BiomeConfig;
import com.khorn.terraincontrol.configuration.BiomeGroup;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.logging.LogMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Layer
{

    protected long b;
    protected Layer child;
    private long c;
    protected long d;

    /*
     * LayerIsland - chance to big land
     * LayerLandRandom - a(3) - chance to increase big land
     * GenLayerIcePlains - chance to ice
     * GenLayerMushroomIsland - chance to mushroom island
     *
     * biome:
     * 1) is island
     * 2) size
     * 3) chance
     * 4) is shore
     * 5) color
     * 6) temperature
     * 7) downfall
     * 8) is snow biome
     * 9) Have rivers
     *
     * world
     * 1) chance to lands
     * 2) size of big lands
     * 3) chance to increase lands
     * 4) Chance for ice area
     * 5) Ice area size
     * 6) Rivers
     * 7) Rivers size
     */
    
    // [ Biome Data ]
    protected static final int BiomeBits = 1023;            //>>	1st-10th Bits           // 255 63
    
    // [ Flags ]
    protected static final int LandBit = (1 << 10);         //>>	11th Bit, 1024          // 256 64
    protected static final int IslandBit = (1 << 11);       //>>	12th Bit, 2048          // 4096 1024
    protected static final int IceBit = (1 << 12);          //>>	13th Bit, 4096
    
    // [ Biome Group Data ]
    protected static final int BiomeGroupShift = 13;        //>>	Shift amount for biome group data
    protected static final int BiomeGroupBits = (127 << BiomeGroupShift);   //>>	14th-20th Bits, 1040384
    
    // [ River Data ]
    protected static final int RiverShift = 20;
    protected static final int RiverBits = (3 << RiverShift);               //>>	21st-22nd Bits, 3145728  //3072 768
    protected static final int RiverBitOne = (1 << RiverShift);             //>>	21st Bit, 1048576
    protected static final int RiverBitTwo = (1 << (RiverShift + 1));       //>>	22nd Bit, 2097152

    protected static int GetBiomeFromLayer(int BiomeAndLand)
    {
        if ((BiomeAndLand & LandBit) != 0)
            return (BiomeAndLand & BiomeBits);
        return 0;
    }

    public static Layer[] Init(long paramLong, LocalWorld world)
    {

        /*
         * int BigLandSize = 2; //default 0, more - smaller
         * int ChanceToIncreaseLand = 6; //default 4
         * int MaxDepth = 10;
         */
        WorldSettings configs = world.getSettings();
        WorldConfig worldConfig = configs.worldConfig;

        Map<String, LocalBiome[][]> GroupBiomeMap = new HashMap<String, LocalBiome[][]>(4);
        Map<String, Integer> BiomeGroupRarity = new HashMap<String, Integer>(4);
        
        //>>	Init GroupBiomeMap
        for (BiomeGroup group : worldConfig.biomeGroupManager.getGroups())
        {
            GroupBiomeMap.put(group.getName(), new LocalBiome[worldConfig.GenerationDepth + 1][]);
            BiomeGroupRarity.put(group.getName(), group.getRarity());
        }

        for (int i = 0; i < worldConfig.GenerationDepth + 1; i++)
        {
            Map<String, ArrayList<LocalBiome>> BiomeGroups = new HashMap<String, ArrayList<LocalBiome>>(4);
            //>>	Init BiomeGroups
            for (BiomeGroup group : worldConfig.biomeGroupManager.getGroups())
            {
                BiomeGroups.put(group.getName(), new ArrayList<LocalBiome>(8));
            }
            //>>	Place biomes in BiomeGroups
            for (LocalBiome biome : configs.biomes)
            {
                if (biome == null)
                    continue;

                BiomeConfig biomeConfig = biome.getBiomeConfig();

                if (biomeConfig.biomeSize != i)
                    continue;
                // 
                for (BiomeGroup group : worldConfig.biomeGroupManager.getGroups())
                {
                    if (group.contains(biomeConfig.name))
                    {
                        for (int t = 0; t < biomeConfig.biomeRarity; t++)
                        {
                            BiomeGroups.get(group.getName()).add(biome);
                        }
                        group.alterRarity(-biomeConfig.biomeRarity);
                    }
                }
            }

            for (Entry<String, LocalBiome[][]> entry : GroupBiomeMap.entrySet())
            {
                LocalBiome[][] GroupLocalBiomes = entry.getValue();
                if (GroupLocalBiomes != null)
                {
                    ArrayList<LocalBiome> bgs = BiomeGroups.get(entry.getKey());
                    if (!bgs.isEmpty())
                    {
                        GroupLocalBiomes[i] = bgs.toArray(new LocalBiome[bgs.size() + BiomeGroupRarity.get(entry.getKey())]);
                        TerrainControl.log(LogMarker.INFO, "Group `{}:{}` filling with biomes", entry.getKey(), i);
                        entry.setValue(GroupLocalBiomes);
                    } else
                    {
                        GroupLocalBiomes[i] = new LocalBiome[0];
                        TerrainControl.log(LogMarker.INFO, "Group `{}:{}` filling with emptiness", entry.getKey(), i);
                        entry.setValue(GroupLocalBiomes);
                    }
                }
            }
        }

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
            
            int size = 1;
            int level = 2;
            if (depth-size >= 0 && depth-size <= level)
                MainLayer = new LayerBiomeGroups(35L + (depth*depth), MainLayer, worldConfig.biomeGroupManager);
            
            boolean nez = false;
            Map<String, LocalBiome[]> biomesForLayer = new HashMap<String, LocalBiome[]>(4);
            for (Entry<String, LocalBiome[][]> entry : GroupBiomeMap.entrySet())
            {
                LocalBiome[][] localBiomes = entry.getValue();
                //>>	TEMPORARY FIX FOR OCEAN IN BIOME GROUPS
                /**/    ArrayList<LocalBiome> biomes  = new ArrayList<LocalBiome>(Arrays.asList(localBiomes[depth]));
                /**/    biomes.removeAll(Collections.singleton(null));
                /**/    localBiomes[depth] = biomes.toArray(new LocalBiome[biomes.size()]);
                //>>	END TEMP FIX
                int len = localBiomes[depth].length;
                if (len != 0){ nez = true; }
                for (LocalBiome localBiome : localBiomes[depth])
                {
                    if (localBiome == null)
                    {
                        TerrainControl.log(LogMarker.INFO, "WARNING: NULL Biomes being set on {} at depth {}!", entry.getKey(), depth);
                    } else if (localBiome.getName().contains("Ocean"))
                    {
                        TerrainControl.log(LogMarker.INFO, "WARNING: Ocean Biomes being set on {} at depth {}!", entry.getKey(), depth);
                    }
                }
                biomesForLayer.put(entry.getKey(), localBiomes[depth]);
            }
            if (nez)
            {
                MainLayer = new LayerBiome(200, MainLayer, biomesForLayer, worldConfig.biomeGroupManager);
            }

//            if (worldConfig.IceSize == depth)
//                MainLayer = new LayerIce(depth, MainLayer, worldConfig.IceRarity);
            
//            if (worldConfig.riverRarity == depth)
//                if (worldConfig.randomRivers)
//                {
//                    RiverLayer = new LayerRiverInit(155, RiverLayer);
//                    riversStarted = true;
//                } else
//                    MainLayer = new LayerRiverInit(155, MainLayer);
//
//            if ((worldConfig.GenerationDepth - worldConfig.riverSize) == depth)
//            {
//                if (worldConfig.randomRivers)
//                    RiverLayer = new LayerRiver(5 + depth, RiverLayer);
//                else
//                    MainLayer = new LayerRiver(5 + depth, MainLayer);
//            }
//
//            LayerBiomeBorder layerBiomeBorder = new LayerBiomeBorder(3000 + depth, world);
//            boolean haveBorder = false;
//            for (LocalBiome biome : configs.biomes)
//            {
//                if (biome == null)
//                    continue;
//                BiomeConfig biomeConfig = biome.getBiomeConfig();
//                if (biomeConfig.biomeSize != depth)
//                    continue;
//                if (worldConfig.IsleBiomes.contains(biomeConfig.name) && biomeConfig.isleInBiome != null)
//                {
//                    int id = biome.getIds().getGenerationId();
//
//                    LayerBiomeInBiome layerBiome = new LayerBiomeInBiome(4000 + id, MainLayer);
//                    layerBiome.biome = biome;
//                    for (String islandInName : biomeConfig.isleInBiome)
//                    {
//                        int islandIn = world.getBiomeByName(islandInName).getIds().getGenerationId();
//                        if (islandIn == DefaultBiome.OCEAN.Id)
//                            layerBiome.inOcean = true;
//                        else
//                            layerBiome.BiomeIsles[islandIn] = true;
//                    }
//
//                    layerBiome.chance = (worldConfig.BiomeRarityScale + 1) - biomeConfig.biomeRarity;
//                    MainLayer = layerBiome;
//                }
//
//                if (worldConfig.BorderBiomes.contains(biomeConfig.name) && biomeConfig.biomeIsBorder != null)
//                {
//                    haveBorder = true;
//
//                    for (String replaceFromName : biomeConfig.biomeIsBorder)
//                    {
//                        int replaceFrom = world.getBiomeByName(replaceFromName).getIds().getGenerationId();
//                        layerBiomeBorder.AddBiome(biome, replaceFrom, world);
//
//                    }
//
//                }
//            }
//
//            if (haveBorder)
//            {
//                layerBiomeBorder.child = MainLayer;
//                MainLayer = layerBiomeBorder;
//            }

        }
        if (worldConfig.randomRivers)
            MainLayer = new LayerMixWithRiver(1L, MainLayer, RiverLayer, configs, world);
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
        ZoomedLayer.SetWorldSeed(paramLong);

        //MainLayer = new LayerCacheInit(1, MainLayer);
        //ZoomedLayer = new LayerCacheInit(1, ZoomedLayer);
        return new Layer[]
        {
            MainLayer, ZoomedLayer
        };
    }

    public Layer(long paramLong)
    {
        this.d = paramLong;
        this.d *= (this.d * 6364136223846793005L + 1442695040888963407L);
        this.d += paramLong;
        this.d *= (this.d * 6364136223846793005L + 1442695040888963407L);
        this.d += paramLong;
        this.d *= (this.d * 6364136223846793005L + 1442695040888963407L);
        this.d += paramLong;
    }

    public void SetWorldSeed(long paramLong)
    {
        this.b = paramLong;
        if (this.child != null)
            this.child.SetWorldSeed(paramLong);
        this.b *= (this.b * 6364136223846793005L + 1442695040888963407L);
        this.b += this.d;
        this.b *= (this.b * 6364136223846793005L + 1442695040888963407L);
        this.b += this.d;
        this.b *= (this.b * 6364136223846793005L + 1442695040888963407L);
        this.b += this.d;
    }

    protected void SetSeed(long paramLong1, long paramLong2)
    {
        this.c = this.b;
        this.c *= (this.c * 6364136223846793005L + 1442695040888963407L);
        this.c += paramLong1;
        this.c *= (this.c * 6364136223846793005L + 1442695040888963407L);
        this.c += paramLong2;
        this.c *= (this.c * 6364136223846793005L + 1442695040888963407L);
        this.c += paramLong1;
        this.c *= (this.c * 6364136223846793005L + 1442695040888963407L);
        this.c += paramLong2;
    }

    protected int nextInt(int paramInt)
    {
        int i = (int) ((this.c >> 24) % paramInt);
        if (i < 0)
            i += paramInt;
        this.c *= (this.c * 6364136223846793005L + 1442695040888963407L);
        this.c += this.b;
        return i;
    }

    public abstract int[] GetBiomes(ArraysCache arraysCache, int x, int z, int x_size, int z_size);

}
