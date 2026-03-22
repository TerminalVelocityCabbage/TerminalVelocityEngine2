package com.terminalvelocitycabbage.engine.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModel;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector2i;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BlockbenchConverterTest {

    @Test
    public void testMetadataParsing() {
        Config metadataConfig = TomlFormat.instance().createConfig();
        metadataConfig.set("model_version", "1.0.0");
        metadataConfig.set("name", "test");
        Config layer1 = Config.inMemory();
        layer1.set("layer_1", List.of(16L, 16L));
        Config layer2 = Config.inMemory();
        layer2.set("layer_2", List.of(32L, 32L));
        metadataConfig.set("texture_layers", List.of(layer1, layer2));
        metadataConfig.set("default_variant", "default");

        TVModel.TVModelVariant defaultVariant = new TVModel.TVModelVariant("default", null, List.of(), List.of(), Map.of());
        TVModel.TVModelMetadata metadata = TVModel.TVModelMetadata.of(metadataConfig, Map.of("default", defaultVariant));

        assertEquals("test", metadata.name());
        assertEquals(2, metadata.textureLayers().size());
        assertTrue(metadata.textureLayers().containsKey("layer_1"));
        assertEquals(16, metadata.textureLayers().get("layer_1").x);
        assertTrue(metadata.textureLayers().containsKey("layer_2"));
        assertEquals(32, metadata.textureLayers().get("layer_2").y);
    }

    @Test
    public void testFullConversion() throws IOException {
        Path tempJson = Files.createTempFile("test_model", ".geo.json");
        // Test with Per-Face UV and Locators and texture sizes
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.other\",\"texture_width\":64,\"texture_height\":32},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,1,0],\"cubes\":[" +
                "{\"origin\":[0,1,0],\"size\":[1,1,1],\"uv\":{\"north\":{\"uv\":[0,0],\"uv_size\":[1,1]}}}" +
                "],\"locators\":{\"anchor1\":[1,2,3]}}" +
                "]}]}");

        Path tempToml = Files.createTempFile("test_model", ".model.toml");
        Files.deleteIfExists(tempToml);

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "test_model");
            assertFalse(tomlConfig.isEmpty(), "Converted config should not be empty");

            com.electronwill.nightconfig.toml.TomlWriter writer = TomlFormat.instance().createWriter();
            writer.setWriteTableInlinePredicate(c -> false);
            writer.write(tomlConfig, tempToml, WritingMode.REPLACE);
        }

        assertTrue(Files.exists(tempToml), "Result file should exist");
        String content = Files.readString(tempToml);
        System.out.println("[DEBUG_LOG] Result TOML:\n" + content);
        
        // Metadata
        assertTrue(content.contains("name = \"test_model\""), "Metadata name should match filename");
        assertTrue(content.contains("default = [64, 32]"), "Metadata texture_layers should contain sizes");
        
        // Bone
        assertTrue(content.contains("name = \"bone1\""), "Should contain bone name");
        assertTrue(content.contains("offset = [0.0, 1.0, 0.0]"), "Bone should have offset from pivot");
        assertTrue(content.contains("position = [0.0, 1.0, 0.0]"), "Bone should have position from pivot");
        
        // Cube
        assertTrue(content.contains("nz_uv = [0, 0, 1, 1]"), "Per-face UV should be converted from uv + uv_size");
        
        // Anchor
        assertTrue(content.contains("name = \"anchor1\""), "Should contain anchor name");
        assertTrue(content.contains("offset = [1.0, 2.0, 3.0]"), "Anchor should have offset");
        assertTrue(content.contains("position = [1.0, 2.0, 3.0]"), "Anchor should have position");

        Files.deleteIfExists(tempJson);
        Files.deleteIfExists(tempToml);
    }

    @Test
    public void testNonIntegerSizeConversion() throws IOException {
        Path tempJson = Files.createTempFile("rounding_test", ".geo.json");
        // size: [2.5, 3.1, 4.9], origin: [0, 0, 0], inflate: 0.1
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.test\",\"texture_width\":16,\"texture_height\":16},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,0,0],\"cubes\":[" +
                "{\"origin\":[0,0,0],\"size\":[2.5,3.1,4.9],\"inflate\":0.1,\"uv\":[0,0]}" +
                "]}]}]}");

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "rounding_test");
            
            List<Config> cubes = tomlConfig.get("cube");
            assertNotNull(cubes);
            assertFalse(cubes.isEmpty());
            Config cube = cubes.get(0);
            
            List<Integer> size = cube.get("size");
            assertEquals(List.of(3, 4, 5), size, "Size should be rounded up");
            
            List<Number> grow = cube.get("grow");
            // x: 0.1 - (3 - 2.5) / 2 = -0.15
            // y: 0.1 - (4 - 3.1) / 2 = -0.35
            // z: 0.1 - (5 - 4.9) / 2 = 0.05
            assertEquals(-0.15, grow.get(0).doubleValue(), 0.0001);
            assertEquals(-0.35, grow.get(1).doubleValue(), 0.0001);
            assertEquals(0.05, grow.get(2).doubleValue(), 0.0001);
            
            List<Number> offset = cube.get("offset");
            // offset = new_origin - pivot = (origin - (ceil-size)/2) - pivot
            // x: (0 - 0.25) - 0 = -0.25
            // y: (0 - 0.45) - 0 = -0.45
            // z: (0 - 0.05) - 0 = -0.05
            assertEquals(-0.25, offset.get(0).doubleValue(), 0.0001);
            assertEquals(-0.45, offset.get(1).doubleValue(), 0.0001);
            assertEquals(-0.05, offset.get(2).doubleValue(), 0.0001);
        } finally {
            Files.deleteIfExists(tempJson);
        }
    }
}
