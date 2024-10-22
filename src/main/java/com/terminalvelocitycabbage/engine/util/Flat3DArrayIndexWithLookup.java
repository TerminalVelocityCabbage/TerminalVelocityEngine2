package com.terminalvelocitycabbage.engine.util;

import org.joml.Vector3i;

public class Flat3DArrayIndexWithLookup {

    private int arrayDimension;
    private int[] lookupTable;

    public Flat3DArrayIndexWithLookup(int arrayDimension) {
        this.arrayDimension = arrayDimension;
        this.lookupTable = new int[arrayDimension * arrayDimension * arrayDimension * 3];

        // Populate the lookup table
        for (int x = 0; x < arrayDimension; x++) {
            for (int y = 0; y < arrayDimension; y++) {
                for (int z = 0; z < arrayDimension; z++) {
                    int index = to1DIndex(x, y, z);
                    lookupTable[index * 3] = x;
                    lookupTable[index * 3 + 1] = y;
                    lookupTable[index * 3 + 2] = z;
                }
            }
        }
    }

    public int to1DIndex(int x, int y, int z) {
        return x * (arrayDimension * arrayDimension) + y * arrayDimension + z;
    }

    public Vector3i to3DCoordinates(int index) {
        return new Vector3i(
                lookupTable[index * 3],
                lookupTable[index * 3 + 1],
                lookupTable[index * 3 + 2]
        );
    }

}
