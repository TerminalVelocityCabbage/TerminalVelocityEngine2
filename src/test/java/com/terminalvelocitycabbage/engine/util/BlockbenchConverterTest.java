package com.terminalvelocitycabbage.engine.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModel;
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
        assertTrue(content.contains("position = [0.0, 1.0, 0.0]"), "Bone should have position from pivot");
        assertFalse(content.contains("offset = "), "Bone should not have redundant offset");
        
        // Cube
        assertTrue(content.contains("nz_uv = [0, 0, 1, 1]"), "Per-face UV should be converted from uv + uv_size");
        
        // Anchor
        assertTrue(content.contains("name = \"anchor1\""), "Should contain anchor name");
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

    @Test
    public void testPerFaceUvConversion() throws IOException {
        Path tempJson = Files.createTempFile("uv_test", ".geo.json");
        // West face (+X), East face (-X), and Down face (-Y)
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.test\",\"texture_width\":64,\"texture_height\":64},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,0,0],\"cubes\":[" +
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":{" +
                "\"west\":{\"uv\":[0,0],\"uv_size\":[8,8]}," +
                "\"east\":{\"uv\":[8,0],\"uv_size\":[8,8]}," +
                "\"down\":{\"uv\":[0,8],\"uv_size\":[8,8],\"rotation\":90}" +
                "}}" +
                "]}]}]}");

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "uv_test");

            List<Config> cubes = tomlConfig.get("cube");
            Config cube = cubes.get(0);
            Config textures = cube.get("textures");

            // West (+X) should be flipped horizontally compared to input [0,0,8,8] -> [8,0,0,8]
            assertEquals(List.of(8, 0, 0, 8), textures.get("px_uv"));
            // East (-X) should be flipped horizontally compared to input [8,0,16,8] -> [16,0,8,8]
            assertEquals(List.of(16, 0, 8, 8), textures.get("nx_uv"));
            // Down (-Y) should have rotation 90 and be flipped both horizontally and vertically [0, 8, 8, 16] -> [8, 16, 0, 8]
            assertEquals(List.of(8, 16, 0, 8, 90), textures.get("ny_uv"));
        } finally {
            Files.deleteIfExists(tempJson);
        }
    }

    @Test
    public void testBoxUvConsistency() throws IOException {
        Path tempJson = Files.createTempFile("box_uv_test", ".geo.json");
        // Box UV with size [1,1,1] at [0,0]
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.test\",\"texture_width\":64,\"texture_height\":64},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,0,0],\"cubes\":[" +
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"uv\":[0,0]}" +
                "]}]}]}");

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "box_uv_test");

            List<Config> cubes = tomlConfig.get("cube");
            Config cube = cubes.get(0);
            Config textures = cube.get("textures");

            // Verify current Box UV values (to make sure we don't break them or know what we're changing)
            // u=0, v=0, sx=1, sy=1, sz=1
            // West (px): [u, v+sz, u+sz, v+sz+sy] = [0, 1, 1, 2]
            assertEquals(List.of(0, 1, 1, 2), textures.get("px_uv"));
            // East (nx): [u+sz+sx, v+sz, u+2sz+sx, v+sz+sy] = [2, 1, 3, 2]
            assertEquals(List.of(2, 1, 3, 2), textures.get("nx_uv"));
            // Up (py): [u+sz+sx, v+sz, u+sz, v] = [2, 1, 1, 0]
            assertEquals(List.of(2, 1, 1, 0), textures.get("py_uv"));
            // Down (ny): [u+sz+sx, v, u+sz+2sx, v+sz] = [2, 0, 3, 1]
            assertEquals(List.of(2, 0, 3, 1), textures.get("ny_uv"));
        } finally {
            Files.deleteIfExists(tempJson);
        }
    }
    @Test
    public void testCubeOffsetPreservation() throws IOException {
        Path tempJson = Files.createTempFile("cube_offset_test", ".geo.json");
        // Cube with origin different from pivot
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.test\",\"texture_width\":16,\"texture_height\":16},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,0,0],\"cubes\":[" +
                "{\"origin\":[1,2,3],\"size\":[1,1,1],\"pivot\":[0,0,0],\"uv\":[0,0]}" +
                "]}]}]}");

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "cube_offset_test");
            
            List<Config> cubes = tomlConfig.get("cube");
            Config cube = cubes.get(0);
            
            // offset = origin - pivot = [1,2,3] - [0,0,0] = [1,2,3]
            List<Number> offset = cube.get("offset");
            assertNotNull(offset, "Cube should still have offset if it's non-zero");
            assertEquals(1.0, offset.get(0).doubleValue(), 0.0001);
            assertEquals(2.0, offset.get(1).doubleValue(), 0.0001);
            assertEquals(3.0, offset.get(2).doubleValue(), 0.0001);
        } finally {
            Files.deleteIfExists(tempJson);
        }
    }

    @Test
    public void testRotationNegation() throws IOException {
        Path tempJson = Files.createTempFile("rotation_test", ".geo.json");
        // rotation: [10, 20, 30]
        Files.writeString(tempJson, "{\"minecraft:geometry\":[{\"description\":{\"identifier\":\"geometry.test\",\"texture_width\":16,\"texture_height\":16},\"bones\":[" +
                "{\"name\":\"bone1\",\"pivot\":[0,0,0],\"rotation\":[10,20,30],\"cubes\":[" +
                "{\"origin\":[0,0,0],\"size\":[1,1,1],\"rotation\":[10,20,30],\"uv\":[0,0]}" +
                "],\"locators\":{\"anchor1\":{\"offset\":[0,0,0],\"rotation\":[10,20,30]}}}]}]}");

        try (FileConfig jsonConfig = FileConfig.builder(tempJson, JsonFormat.minimalInstance()).build()) {
            jsonConfig.load();
            Config tomlConfig = BlockbenchConverter.convertJsonToTveModel(jsonConfig, "rotation_test");

            Config bone = ((List<Config>) tomlConfig.get("bone")).get(0);
            List<Number> boneRot = bone.get("rotation");
            assertEquals(-10.0, boneRot.get(0).doubleValue(), 0.0001);
            assertEquals(20.0, boneRot.get(1).doubleValue(), 0.0001);
            assertEquals(-30.0, boneRot.get(2).doubleValue(), 0.0001);

            Config cube = ((List<Config>) tomlConfig.get("cube")).get(0);
            List<Number> cubeRot = cube.get("rotation");
            assertEquals(-10.0, cubeRot.get(0).doubleValue(), 0.0001);
            assertEquals(20.0, cubeRot.get(1).doubleValue(), 0.0001);
            assertEquals(-30.0, cubeRot.get(2).doubleValue(), 0.0001);

            Config anchor = ((List<Config>) tomlConfig.get("anchor")).get(0);
            List<Number> anchorRot = anchor.get("rotation");
            assertEquals(-10.0, anchorRot.get(0).doubleValue(), 0.0001);
            assertEquals(20.0, anchorRot.get(1).doubleValue(), 0.0001);
            assertEquals(-30.0, anchorRot.get(2).doubleValue(), 0.0001);
        } finally {
            Files.deleteIfExists(tempJson);
        }
    }

}
