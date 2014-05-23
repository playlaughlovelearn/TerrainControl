package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.BiomeConfig;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

import java.util.ArrayList;

public abstract class Layer
{

    protected long worldGenSeed;
    protected Layer child;
    private long chunkSeed;
    protected long baseSeed;

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
    protected static final short BiomeBits = 1023; // 255 63
    protected static final short LandBit = 1024;   // 256 64
    protected static final short RiverBits = 12288; //3072 768
    protected static final short RiverBitOne = 4096;
    protected static final short RiverBitTwo = 8192;
    protected static final short IceBit = 2048;   // 512  128
    protected static final short IslandBit = 16384; // 4096 1024

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

        //>>	>>START - How to handle this when biome groups are implemented? 
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
        ZoomedLayer.initWorldGenSeed(seed);

        //MainLayer = new LayerCacheInit(1, MainLayer);
        //ZoomedLayer = new LayerCacheInit(1, ZoomedLayer);
        return new Layer[]
        {
            MainLayer, ZoomedLayer
        };

        //>>	
        //>>	START MCP CODE
        //>>	
//        boolean flag = false;
//        GenLayerIsland MainLayer = new GenLayerIsland(1L);
//        GenLayerFuzzyZoom mlA = new GenLayerFuzzyZoom(2000L, MainLayer);
//        GenLayerAddIsland mlB = new GenLayerAddIsland(1L, mlA);
//        GenLayerZoom mlC = new GenLayerZoom(2001L, mlB);
//        mlB = new GenLayerAddIsland(2L, mlC);
//        mlB = new GenLayerAddIsland(50L, mlB);
//        mlB = new GenLayerAddIsland(70L, mlB);
//        GenLayerRemoveTooMuchOcean mlD = new GenLayerRemoveTooMuchOcean(2L, mlB);
//        GenLayerAddSnow mlE = new GenLayerAddSnow(2L, mlD);
//        mlB = new GenLayerAddIsland(3L, mlE);
//        GenLayerEdge mlF = new GenLayerEdge(2L, mlB, GenLayerEdge.Mode.COOL_WARM);
//        mlF = new GenLayerEdge(2L, mlF, GenLayerEdge.Mode.HEAT_ICE);
//        mlF = new GenLayerEdge(3L, mlF, GenLayerEdge.Mode.SPECIAL);
//        mlC = new GenLayerZoom(2002L, mlF);
//        mlC = new GenLayerZoom(2003L, mlC);
//        mlB = new GenLayerAddIsland(4L, mlC);
//        GenLayerAddMushroomIsland mlG = new GenLayerAddMushroomIsland(5L, mlB);
//        GenLayerDeepOcean mlH = new GenLayerDeepOcean(4L, mlG);
//        GenLayer mlJ = GenLayerZoom.magnify(1000L, mlH, 0);
//        byte var5 = 4;
//
//        if (worldType == WorldType.LARGE_BIOMES)
//        {
//            var5 = 6;
//        }
//
//        if (flag)
//        {
//            var5 = 4;
//        }
//
//        GenLayer mlK = GenLayerZoom.magnify(1000L, mlJ, 0);
//        GenLayerRiverInit mlL = new GenLayerRiverInit(100L, mlK);
//        Object mlM = new GenLayerBiome(200L, mlJ, worldType);
//
//        if (!flag)
//        {
//            GenLayer mlN = GenLayerZoom.magnify(1000L, (GenLayer) mlM, 2);
//            mlM = new GenLayerBiomeEdge(1000L, mlN);
//        }
//
//        GenLayer MlO = GenLayerZoom.magnify(1000L, mlL, 2);
//        GenLayerHills mlP = new GenLayerHills(1000L, (GenLayer) mlM, MlO);
//        mlK = GenLayerZoom.magnify(1000L, mlL, 2);
//        mlK = GenLayerZoom.magnify(1000L, mlK, var5);
//        GenLayerRiver mlQ = new GenLayerRiver(1L, mlK);
//        GenLayerSmooth mlR = new GenLayerSmooth(1000L, mlQ);
//        mlM = new GenLayerRareBiome(1001L, mlP);
//
//        for (int var9 = 0; var9 < var5; ++var9)
//        {
//            mlM = new GenLayerZoom((long) (1000 + var9), (GenLayer) mlM);
//
//            if (var9 == 0)
//            {
//                mlM = new GenLayerAddIsland(3L, (GenLayer) mlM);
//            }
//
//            if (var9 == 1)
//            {
//                mlM = new GenLayerShore(1000L, (GenLayer) mlM);
//            }
//        }
//
//        GenLayerSmooth mlS = new GenLayerSmooth(1000L, (GenLayer) mlM);
//        GenLayerRiverMix mlT = new GenLayerRiverMix(100L, mlS, mlR);
//        GenLayerVoronoiZoom mlU = new GenLayerVoronoiZoom(10L, mlT);
//        mlT.initWorldGenSeed(seed);
//        mlU.initWorldGenSeed(seed);
//        return new GenLayer[]
//        {
//            mlT, mlU, mlT
//        };

    }

    public Layer(long seed)
    {
        this.baseSeed = seed;
        this.baseSeed *= (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += seed;
        this.baseSeed *= (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += seed;
        this.baseSeed *= (this.baseSeed * 6364136223846793005L + 1442695040888963407L);
        this.baseSeed += seed;
    }

    /**
     * Initialize layer's local worldGenSeed based on its own baseSeed and the
     * world's global seed (passed in as an argument).
     */
    public void initWorldGenSeed(long seed)
    {
        this.worldGenSeed = seed;
        if (this.child != null)
            this.child.initWorldGenSeed(seed);
        this.worldGenSeed *= (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= (this.worldGenSeed * 6364136223846793005L + 1442695040888963407L);
        this.worldGenSeed += this.baseSeed;
    }

    /**
     * Initialize layer's current chunkSeed based on the local worldGenSeed and
     * the (x,z) chunk coordinates.
     */
    protected void initChunkSeed(long x, long z)
    {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed *= (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += x;
        this.chunkSeed *= (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += z;
        this.chunkSeed *= (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += x;
        this.chunkSeed *= (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += z;
    }

    /**
     * returns a LCG pseudo random number from [0, x). Args: int x
     */
    protected int nextInt(int x)
    {
        int i = (int) ((this.chunkSeed >> 24) % x);
        if (i < 0)
            i += x;
        this.chunkSeed *= (this.chunkSeed * 6364136223846793005L + 1442695040888963407L);
        this.chunkSeed += this.worldGenSeed;
        return i;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be
     * interpreted as temperatures, rainfall amounts, or biomeList[] indices
     * based on the particular Layer subclass.
     */
    public abstract int[] getInts(ArraysCache arraysCache, int x, int z, int xSize, int zSize);

    /*
     * //t>>	Add this function from MCP code when needed (Uses:
     * GenLayerBiomeEdge, GenLayerHills)
     * protected static boolean compareBiomes(final int biome_A_ID, final int
     * biome_B_ID);
     * //t>>	Add this function from MCP code when needed (Uses: GenLayerBiome,
     * GenLayerShore)
     * protected static boolean isOcean(int biomeID);
     */
    //>>	Uses: GenLayer, GenLayerFuzzyZoom, GenLayerZoom
    protected int getRandomInArray(int... biomes)
    {
        return biomes[this.nextInt(biomes.length)];
    }

    protected int getRandomOf4(int a, int b, int c, int d)
    {
        return b == c && c == d
               ? b
               : (a == b && a == c
                  ? a
                  : (a == b && a == d
                     ? a
                     : (a == c && a == d
                        ? a
                        : (a == b && c != d
                           ? a
                           : (a == c && b != d
                              ? a
                              : (a == d && b != c
                                 ? a
                                 : (b == c && a != d
                                    ? b
                                    : (b == d && a != c
                                       ? b
                                       : (c == d && a != b
                                          ? c
                                          : this.getRandomInArray(new int[]
                                          {
                                              a, b, c, d
        }))))))))));
    }

}
