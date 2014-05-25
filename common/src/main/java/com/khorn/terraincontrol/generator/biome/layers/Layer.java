package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.configuration.BiomeConfig;
import com.khorn.terraincontrol.configuration.WorldConfig;
import com.khorn.terraincontrol.configuration.WorldSettings;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;

import java.util.ArrayList;

public abstract class Layer
{

    /**
     * seed from World#getWorldSeed that is used in the LCG prng
     */
    protected long worldGenSeed;

    /**
     * child Layer that was provided via the constructor
     */
    protected Layer child;

    /**
     * final part of the LCG prng that uses the chunk X, Z coords along with the
     * other two seeds to generate pseudorandom numbers
     */
    private long chunkSeed;

    /**
     * base seed to the LCG prng provided via the constructor
     */
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
        Object mlM = new LayerBiome(200L, mlJ/** /,
         * worldType/* */
        );

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
                /** //t>>	Is there any need, or even a way, to hook into this CR
                 * system?
                 * CrashReport cr = CrashReport.makeCrashReport(ex, "Comparing
                 * biomes");
                 * CrashReportCategory crc = cr.makeCategory("Biomes being
                 * compared");
                 * crc.addCrashSection("Biome A ID",
                 * Integer.valueOf(biome_A_ID));
                 * crc.addCrashSection("Biome B ID",
                 * Integer.valueOf(biome_B_ID));
                 * crc.addCrashSectionCallable("Biome A", new Callable() {
                 * public String call() {
                 * return
                 * String.valueOf(DefaultBiome.getBiomeGenerator(biome_A_ID));
                 * }
                 * });
                 * crc.addCrashSectionCallable("Biome B", new Callable() {
                 * public String call() {
                 * return
                 * String.valueOf(DefaultBiome.getBiomeGenerator(biome_B_ID));
                 * }
                 * });
                 * throw new ReportedException(cr);
                 */
            }
        }
        return biome_B_ID == DefaultBiome.MESA_PLATEAU_FOREST.Id || biome_B_ID == DefaultBiome.MESA_PLATEAU.Id;
    }

    protected static boolean isOcean(int biomeID)
    {
        return biomeID == DefaultBiome.OCEAN.Id || biomeID == DefaultBiome.DEEP_OCEAN.Id || biomeID == DefaultBiome.FROZEN_OCEAN.Id;
    }

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
