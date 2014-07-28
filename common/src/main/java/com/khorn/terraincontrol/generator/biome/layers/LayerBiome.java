package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import java.util.Map;
import java.util.Map.Entry;

public class LayerBiome extends Layer
{
    public Map<String, LocalBiome[]> biomes;
//    public LocalBiome[] biomes;
//    public LocalBiome[] ice_biomes;


    public LayerBiome(long paramLong, Layer paramGenLayer, Map<String, LocalBiome[]> biomesForLayer)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.biomes = biomesForLayer;
    }

    @Override
    public int[] GetBiomes(ArraysCache arraysCache, int x, int z, int x_size, int z_size)
    {
        int[] arrayOfInt1 = this.child.GetBiomes(arraysCache, x, z, x_size, z_size);

        int[] arrayOfInt2 = arraysCache.GetArray(x_size * z_size);
        for (int i = 0; i < z_size; i++)
        {
            for (int j = 0; j < x_size; j++)
            {
                SetSeed(j + x, i + z);
                int currentPiece = arrayOfInt1[(j + i * x_size)];

                if ((currentPiece & BiomeBits) == 0)    // without biome
                {
                    for (Entry<String, LocalBiome[]> entry : biomes.entrySet())
                    {
//                        String string = entry.getKey();
                        LocalBiome[] localBiomes = entry.getValue();
                        if (this.biomes.length > 0 && (currentPiece & IceBit) == 0)
                        {
                            LocalBiome biome = localBiomes[nextInt(localBiomes.length)];
                            if (biome != null)
                                currentPiece |= biome.getIds().getGenerationId();
                        }
                    }
                }

                arrayOfInt2[(j + i * x_size)] = currentPiece;


            }
        }

        return arrayOfInt2;
    }
}