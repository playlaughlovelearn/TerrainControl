package com.khorn.terraincontrol.configuration;

import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.logging.LogMarker;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Resource: something that can generate in the world.
 * <p>
 * @TODO get class to enforce global group max restriction
 */
public class BiomeGroupManager
{

    public static final int MAX_BIOME_GROUP_COUNT = 127;
    private int groupCount = 0;
    private int globalBiomeCount = 0;
    private Map<String, Integer> nameToId = new HashMap<String, Integer>(8);
    private Map<Integer, BiomeGroup> idToGroup = new HashMap<Integer, BiomeGroup>(8);

    public BiomeGroupManager()
    {

    }

    public BiomeGroup registerGroup(WorldConfig config, String[] args)
    {
        BiomeGroup newGroup = new BiomeGroup(config, args);
        return __register(newGroup);
    }

    public BiomeGroup registerGroup(WorldConfig config, String[] args, boolean coldGroup)
    {
        return this.registerGroup(config, args).setColdGroup();
    }

    public BiomeGroup registerGroup(WorldConfig config, String groupName, List<String> biomes)
    {
        BiomeGroup newGroup = new BiomeGroup(config, groupName, biomes);
        return __register(newGroup);
    }

    public BiomeGroup registerGroup(WorldConfig config, String groupName, List<String> biomes, boolean coldGroup)
    {
        return this.registerGroup(config, groupName, biomes).setColdGroup();
    }

    private BiomeGroup __register(BiomeGroup newGroup)
    {
        if (canAddGroup(newGroup.getName()))
        {
            Integer existing = nameToId.get(newGroup.getName());
            if (existing != null)
            {
                newGroup.setGroupid(existing);
                idToGroup.put(existing, newGroup);
                TerrainControl.log(LogMarker.INFO, "Biome group `{}` was added as `{}`.", newGroup.getName(), existing);
                TerrainControl.log(LogMarker.INFO, "-------------------------------");
            } else
            {
                nameToId.put(newGroup.getName(), ++groupCount);
                newGroup.setGroupid(groupCount);
                idToGroup.put(groupCount, newGroup);
                TerrainControl.log(LogMarker.INFO, "Biome group `{}` was added as `{}`.", newGroup.getName(), groupCount);
                TerrainControl.log(LogMarker.INFO, "-------------------------------");
            }

            return newGroup;
        }
        return null;
    }

    private boolean canAddGroup(String name)
    {
        if (groupCount < MAX_BIOME_GROUP_COUNT)
        {
            return true;
        }
        TerrainControl.log(LogMarker.WARN, "Biome group `{}` could not be added. Max biome group count reached.", name);
        return false;
    }

    public BiomeGroup getGroup(Integer id)
    {
        return idToGroup.get(id);
    }

    public BiomeGroup getGroup(String name)
    {
        return getGroup(nameToId.get(name));
    }

    public Collection<BiomeGroup> getGroups()
    {
        return idToGroup.values();
    }

}
