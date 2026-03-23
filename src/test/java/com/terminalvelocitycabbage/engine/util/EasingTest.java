package com.terminalvelocitycabbage.engine.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EasingTest {

    private static final float DELTA = 0.0001f;

    @Test
    public void testEaseInOutSin() {
        assertEquals(0f, Easing.easeInOutSin(0f), DELTA, "easeInOutSin(0) should be 0");
        assertEquals(0.5f, Easing.easeInOutSin(0.5f), DELTA, "easeInOutSin(0.5) should be 0.5");
        assertEquals(1f, Easing.easeInOutSin(1f), DELTA, "easeInOutSin(1) should be 1");
    }

    @Test
    public void testAllEasingsEndpoints() {
        for (Easing.Function function : Easing.Function.values()) {
            System.out.println("[DEBUG_LOG] Testing function: " + function);
            
            // Test In
            assertEquals(0f, Easing.easeIn(function, 0f), DELTA, "easeIn " + function + "(0) should be 0");
            assertEquals(1f, Easing.easeIn(function, 1f), DELTA, "easeIn " + function + "(1) should be 1");

            // Test Out
            assertEquals(0f, Easing.easeOut(function, 0f), DELTA, "easeOut " + function + "(0) should be 0");
            assertEquals(1f, Easing.easeOut(function, 1f), DELTA, "easeOut " + function + "(1) should be 1");

            // Test InOut
            assertEquals(0f, Easing.easeInOut(function, 0f), DELTA, "easeInOut " + function + "(0) should be 0");
            assertEquals(1f, Easing.easeInOut(function, 1f), DELTA, "easeInOut " + function + "(1) should be 1");
        }
    }
    
    @Test
    public void testStepFunctions() {
        // In Step: only 1 at x=1
        assertEquals(0f, Easing.easeInStep(0.99f), DELTA);
        assertEquals(1f, Easing.easeInStep(1f), DELTA);
        
        // Out Step: 1 for x > 0
        assertEquals(0f, Easing.easeOutStep(0f), DELTA);
        assertEquals(1f, Easing.easeOutStep(0.01f), DELTA);
        
        // InOut Step: 1 for x >= 0.5
        assertEquals(0f, Easing.easeInOutStep(0.49f), DELTA);
        assertEquals(1f, Easing.easeInOutStep(0.5f), DELTA);
    }

    @Test
    public void testCatmullromAlias() {
        assertEquals(Easing.Function.CATMULLROM, Easing.Function.fromString("catmullrom"));
        assertEquals(Easing.Function.CATMULLROM, Easing.Function.fromString("catmulrom"));
    }
    
    @Test
    public void testEaseInOutElasticJumps() {
        // Elastic often has issues at the boundaries or midpoint
        float val049 = Easing.easeInOutElastic(0.49f);
        float val050 = Easing.easeInOutElastic(0.50f);
        float val051 = Easing.easeInOutElastic(0.51f);
        
        System.out.println("[DEBUG_LOG] easeInOutElastic(0.49) = " + val049);
        System.out.println("[DEBUG_LOG] easeInOutElastic(0.50) = " + val050);
        System.out.println("[DEBUG_LOG] easeInOutElastic(0.51) = " + val051);
        
        assertTrue(Math.abs(val050 - val049) < 0.1f, "Jump detected at 0.5 in easeInOutElastic");
        assertTrue(Math.abs(val051 - val050) < 0.1f, "Jump detected at 0.5 in easeInOutElastic");
    }
}
