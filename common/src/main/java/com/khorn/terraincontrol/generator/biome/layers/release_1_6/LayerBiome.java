package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.generator.biome.layers.Layer;

import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.BiomeBits;
import static com.khorn.terraincontrol.generator.biome.layers.release_1_6.LayerR16.IceBit;

/**
 * This class is kept here for reference during the BiomeGroups feature is
 * implemented
 */
public class LayerBiome extends Layer
{

    public LocalBiome[] biomes;
    public LocalBiome[] ice_biomes;

    public LayerBiome(long seed, Layer childLayer/* ,
     * WorldType worldType */)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache cache, int x, int z, int xSize, int zSize)
    {
        int[] childInts = this.child.getInts(cache, x, z, xSize, zSize);
        int[] thisInts = cache.getArray(xSize * zSize);

        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                initChunkSeed(xi + x, zi + z);
                int selection = childInts[(xi + zi * xSize)];

                if ((selection & BiomeBits) == 0)    // without biome
                {
                    if (this.biomes.length > 0 && (selection & IceBit) == 0) // Normal Biome
                    {
                        LocalBiome biome = this.biomes[nextInt(this.biomes.length)];
                        if (biome != null)
                            selection |= biome.getIds().getGenerationId();
                    } else if (this.ice_biomes.length > 0 && (selection & IceBit) != 0) //Ice biome
                    {
                        LocalBiome biome = this.ice_biomes[nextInt(this.ice_biomes.length)];
                        if (biome != null)
                            selection |= biome.getIds().getGenerationId();
                    }
                }

                thisInts[(xi + zi * xSize)] = selection;

            }
        }

        return thisInts;
    }

}
