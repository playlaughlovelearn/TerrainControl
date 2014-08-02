package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.LocalBiome;
import com.khorn.terraincontrol.configuration.BiomeGroup;
import com.khorn.terraincontrol.configuration.BiomeGroupManager;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import java.util.Map;

public class LayerBiome extends Layer
{

    public Map<String, LocalBiome[]> biomes;
    private BiomeGroupManager biomeGroupManager;

    public LayerBiome(long paramLong, Layer paramGenLayer, Map<String, LocalBiome[]> biomesForLayer, BiomeGroupManager biomeGroupManager)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.biomes = biomesForLayer;
        this.biomeGroupManager = biomeGroupManager;
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

                if ((currentPiece & BiomeBits) == 0 && (currentPiece & BiomeGroupBits) != 0)    // without biome but has biome group
                {
                    BiomeGroup group = biomeGroupManager.getGroup((currentPiece & BiomeGroupBits) >> BiomeGroupShift);
                    LocalBiome[] localBiomes = biomes.get(group.getName());
                    if (localBiomes.length > 0)
                    {
                        LocalBiome biome = localBiomes[nextInt(localBiomes.length)];
                        if (biome != null)
                            currentPiece |= biome.getIds().getGenerationId();
//                        else
//                            TerrainControl.log(LogMarker.INFO, "Group Biome Bits Failed: `{}`->`{}`", Integer.toBinaryString(currentPiece), (currentPiece & BiomeGroupBits) >> BiomeGroupShift);
                    }
                }
                arrayOfInt2[(j + i * x_size)] = currentPiece;
            }
        }

        return arrayOfInt2;
    }

}
