package com.khorn.terraincontrol.configuration;

import com.khorn.terraincontrol.exception.InvalidConfigException;
import com.khorn.terraincontrol.util.helpers.StringHelper;
import com.khorn.terraincontrol.util.minecraftTypes.DefaultBiome;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Resource: something that can generate in the world.
 */
public class BiomeGroup extends ConfigFunction<WorldConfig>
{
    private String groupName;
    private List<String> biomesInGroup = new LinkedList<String>();

    public BiomeGroup(WorldConfig config, String[] args)
    {
        this.setHolder(config);
        try
        {
            this.load(Arrays.asList(args));
        } catch (InvalidConfigException ex)
        {
            Logger.getLogger(BiomeGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BiomeGroup(WorldConfig config, String groupName, List<String> biomes)
    {
        this.setHolder(config);
        this.biomesInGroup = filterBiomes(biomes);
        this.groupName = groupName;
        this.setValid(true);
    }
    
    @Override
    protected void load(List<String> args) throws InvalidConfigException
    {
        //>>	Must have atleast a GroupName and a Biome that belongs to it
        assureSize(2, args);

        groupName = args.get(0);
        biomesInGroup = filterBiomes(readBiomes(args,1));
        this.setValid(true);
    }
    
    
    @Override
    public Class<WorldConfig> getHolderType()
    {
        return WorldConfig.class;
    }

    @Override
    public String makeString()
    {
        return "BiomeGroup(" + groupName + ", " + StringHelper.join(biomesInGroup, ", ") + ")";
    }

    protected ArrayList<String> filterBiomes(List<String> biomes)
    {
        ArrayList<String> output = new ArrayList<String>();
        Set<String> customBiomes = this.getHolder().CustomBiomeIds.keySet();
        for (String key : biomes)
        {
            key = key.trim();
            if (customBiomes.contains(key))
            {
                output.add(key);
                continue;
            }

            if (DefaultBiome.Contain(key))
                output.add(key);

        }
        return output;
    }    
    
    /**
     * Reads all biomes from the start position until the end of the
     * list.
     * <p/>
     * @param strings The input strings.
     * @param start   The position to start. The first element in the list
     *                has index 0, the last one size() - 1.
     * <p/>
     * @return All biome names.
     * <p/>
     * @throws InvalidConfigException If one of the elements in the list is
     *                                not a valid block id.
     */
    protected List<String> readBiomes(List<String> strings, int start) throws InvalidConfigException
    {
        List<String> biomes = new LinkedList<String>();
        for (ListIterator<String> it = strings.listIterator(start); it.hasNext();)
        {
            biomes.add(it.next());
        }
        return biomes;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public List<String> getBiomesInGroup()
    {
        return Collections.unmodifiableList(biomesInGroup);
    }
    
}
