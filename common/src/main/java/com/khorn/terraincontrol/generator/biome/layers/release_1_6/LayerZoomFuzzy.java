package com.khorn.terraincontrol.generator.biome.layers.release_1_6;

import com.khorn.terraincontrol.generator.biome.layers.Layer;

public class LayerZoomFuzzy extends LayerZoom
{

    public LayerZoomFuzzy(long seed, Layer childLayer)
    {
        super(seed, childLayer);
    }

    @Override
    protected int getRandomOf4(int a, int b, int c, int d)
    {
        return this.getRandomInArray(new int[]
        {
            a, b, c, d
        });
    }

}
