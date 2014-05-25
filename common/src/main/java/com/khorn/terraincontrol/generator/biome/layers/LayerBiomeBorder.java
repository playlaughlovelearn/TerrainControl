package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.LocalWorld;
import com.khorn.terraincontrol.generator.biome.ArraysCache;

public class LayerBiomeBorder extends Layer
{

    public LayerBiomeBorder(long paramLong, LocalWorld world)
    {
        super(paramLong);
        this.BordersFrom = new boolean[world.getMaxBiomesCount()][];
        this.BordersTo = new int[world.getMaxBiomesCount()];
    }

    private boolean[][] BordersFrom;
    private int[] BordersTo;

    public void AddBiome(LocalBiome replaceTo, int replaceFrom, LocalWorld world)
    {
        this.BordersFrom[replaceFrom] = new boolean[world.getMaxBiomesCount()];

        for (int i = 0; i < this.BordersFrom[replaceFrom].length; i++)
        {
            LocalBiome biome = world.getBiomeById(i);
            this.BordersFrom[replaceFrom][i] = biome == null || !replaceTo.getBiomeConfig().notBorderNear.contains(biome.getName());
        }
        this.BordersTo[replaceFrom] = replaceTo.getIds().getGenerationId();
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x - 1, z - 1, xSize + 2, zSize + 2);

        int[] thisInts = cache.getArray(xSize * zSize);
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                initChunkSeed(xi + x, zi + z);
                int selection = childInts[(xi + 1 + (zi + 1) * (xSize + 2))];

                int biomeId = GetBiomeFromLayer(selection);
                if (BordersFrom[biomeId] != null)
                {
                    int i1 = GetBiomeFromLayer(childInts[(xi + 1 + (zi + 1 - 1) * (xSize + 2))]);
                    int i2 = GetBiomeFromLayer(childInts[(xi + 1 + 1 + (zi + 1) * (xSize + 2))]);
                    int i3 = GetBiomeFromLayer(childInts[(xi + 1 - 1 + (zi + 1) * (xSize + 2))]);
                    int i4 = GetBiomeFromLayer(childInts[(xi + 1 + (zi + 1 + 1) * (xSize + 2))]);
                    boolean[] biomeFrom = BordersFrom[biomeId];
                    if (biomeFrom[i1] && biomeFrom[i2] && biomeFrom[i3] && biomeFrom[i4])
                        if ((i1 != biomeId) || (i2 != biomeId) || (i3 != biomeId) || (i4 != biomeId))
                            selection = (selection & (IslandBit | RiverBits | IceBit)) | LandBit | BordersTo[biomeId];
                }

                thisInts[(xi + zi * xSize)] = selection;

            }
        }

        return thisInts;
    }

}
