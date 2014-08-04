package com.khorn.terraincontrol.generator.biome.layers;

import com.khorn.terraincontrol.configuration.BiomeGroup;
import com.khorn.terraincontrol.configuration.BiomeGroupManager;
import com.khorn.terraincontrol.generator.biome.ArraysCache;

/**
 * @author Timethor
 */
public class LayerBiomeGroups extends Layer
{

    private BiomeGroupManager biomeGroupManager;

    public LayerBiomeGroups(long paramLong, Layer paramGenLayer, BiomeGroupManager biomeGroups)
    {
        super(paramLong);
        this.child = paramGenLayer;
        this.biomeGroupManager = biomeGroups;
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

                BiomeGroup entry = biomeGroupManager.getGroup(nextInt(biomeGroupManager.getGroups().size()) + 1);

                if (!entry.getBiomes().isEmpty() && (currentPiece & LandBit) != 0 && (currentPiece & BiomeGroupBits) == 0)    // land without biome group
                {
                    currentPiece |= ((entry.getGroupid() << BiomeGroupShift) | (entry.isColdGroup() ? IceBit : 0));
                    //>>	Uncomment the line below and comment the line above to visualize biome groups
                    // currentPiece |= (entry.getGroupid() + 15) | (entry.isColdGroup() ? IceBit : 0);
                }

                arrayOfInt2[(j + i * x_size)] = currentPiece;

            }
        }

        return arrayOfInt2;
    }

}
