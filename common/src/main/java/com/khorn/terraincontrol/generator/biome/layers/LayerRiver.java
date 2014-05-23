package com.khorn.terraincontrol.generator.biome.layers;


import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.generator.biome.ArraysCache;
import com.khorn.terraincontrol.logging.LogMarker;

public class LayerRiver extends Layer
{
    public LayerRiver(long seed, Layer childLayer)
    {
        super(seed);
        this.child = childLayer;
    }

    @Override
    public int[] getInts(ArraysCache arraysCache, int x, int z, int xSize, int zSize)
    {
        int x0 = x - 1;
        int z0 = z - 1;
        int xSize0 = xSize + 2;
        int zSize0 = zSize + 2;
        int[] childInts = this.child.getInts(arraysCache, x0, z0, xSize0, zSize0);

        int[] thisInts = arraysCache.GetArray( xSize * zSize);
        for (int zi = 0; zi < zSize; zi++)
        {
            for (int xi = 0; xi < xSize; xi++)
            {
                int a = childInts[(xi + 0 + (zi + 1) * xSize0)];
                int b = childInts[(xi + 2 + (zi + 1) * xSize0)];
                int c = childInts[(xi + 1 + (zi + 0) * xSize0)];
                int d = childInts[(xi + 1 + (zi + 2) * xSize0)];
                //>>	Begin Debug
//                TerrainControl.log(LogMarker.INFO, "After {}", String.format("\n%16s\n%16s\n%16s\n%16s",
//                                Integer.toBinaryString(a),
//                                Integer.toBinaryString(b),
//                                Integer.toBinaryString(c),
//                                Integer.toBinaryString(d) ).replace(" ", "0"));
                //>>	Put these back when done!
                a &= RiverBits;
                b &= RiverBits;
                c &= RiverBits;
                d &= RiverBits;
//                TerrainControl.log(LogMarker.INFO, "Before {}", String.format("\n%16s\n%16s\n%16s\n%16s\n\n",
//                                Integer.toBinaryString(a),
//                                Integer.toBinaryString(b),
//                                Integer.toBinaryString(c),
//                                Integer.toBinaryString(d) ).replace(" ", "0"));
                //>>	End Debug
                int abcd = childInts[(xi + 1 + (zi + 1) * xSize0)] & RiverBits;
                int currentPiece = childInts[(xi + 1 + (zi + 1) * xSize0)];
                if ((abcd == 0) || (a == 0) || (b == 0) || (c == 0) || (d == 0))
                    currentPiece |= RiverBits;
                else if ((abcd != a) || (abcd != c) || (abcd != b) || (abcd != d))
                    currentPiece |= RiverBits;
                else
                {
                    currentPiece |= RiverBits;
                    currentPiece ^= RiverBits;
                }
                thisInts[(xi + zi * xSize)] = currentPiece;
            }
        }

        return thisInts;
    }
}