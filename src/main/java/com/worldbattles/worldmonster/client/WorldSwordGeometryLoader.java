package com.worldbattles.worldmonster.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the World Sword geometry exported from the five original Blockbench files.
 * Keeping the element angles as floats avoids vanilla JSON's restricted angle presets.
 * ElementsModel still bakes the faces through Forge/Minecraft's normal FaceBakery path,
 * preserving standard UV mapping, item lighting, handedness and display transforms.
 *
 * Entry format:
 * [ [fromX,fromY,fromZ,toX,toY,toZ], [angleZ,originX,originY,originZ],
 *   [northUV,eastUV,southUV,westUV,upUV,downUV] ]
 * Each UV is [u1,v1,u2,v2] in Minecraft's 0..16 texture coordinates.
 */
public final class WorldSwordGeometryLoader implements IGeometryLoader<ElementsModel> {
    public static final WorldSwordGeometryLoader INSTANCE = new WorldSwordGeometryLoader();

    private static final Direction[] FACE_DIRECTIONS = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST, Direction.UP, Direction.DOWN
    };

    private WorldSwordGeometryLoader() {}

    @Override
    public ElementsModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        JsonElement geometryElement = json.get("geometry");
        if (geometryElement == null || !geometryElement.isJsonArray()) {
            throw new JsonParseException("World Sword model is missing its geometry array");
        }

        JsonArray geometry = geometryElement.getAsJsonArray();
        List<BlockElement> elements = new ArrayList<>(geometry.size());
        for (int elementIndex = 0; elementIndex < geometry.size(); elementIndex++) {
            JsonArray element = requireArray(geometry.get(elementIndex), "geometry[" + elementIndex + "]");
            if (element.size() != 3) {
                throw new JsonParseException("World Sword geometry element " + elementIndex
                        + " must contain bounds, rotation and faces");
            }

            JsonArray bounds = requireArray(element.get(0), "bounds");
            JsonArray rotationData = requireArray(element.get(1), "rotation");
            JsonArray facesData = requireArray(element.get(2), "faces");
            if (bounds.size() != 6 || rotationData.size() != 4 || facesData.size() != 6) {
                throw new JsonParseException("World Sword geometry element " + elementIndex
                        + " has an invalid array length");
            }

            Vector3f from = new Vector3f(number(bounds, 0), number(bounds, 1), number(bounds, 2));
            Vector3f to = new Vector3f(number(bounds, 3), number(bounds, 4), number(bounds, 5));

            float angle = number(rotationData, 0);
            BlockElementRotation rotation = null;
            if (Math.abs(angle) > 0.0001F) {
                Vector3f origin = new Vector3f(
                        number(rotationData, 1), number(rotationData, 2), number(rotationData, 3));
                rotation = new BlockElementRotation(origin, Direction.Axis.Z, angle, false);
            }

            Map<Direction, BlockElementFace> faces = new EnumMap<>(Direction.class);
            for (int faceIndex = 0; faceIndex < FACE_DIRECTIONS.length; faceIndex++) {
                JsonElement faceElement = facesData.get(faceIndex);
                if (faceElement == null || faceElement.isJsonNull()) {
                    continue;
                }
                JsonArray uvData = requireArray(faceElement, "face UV");
                if (uvData.size() != 4) {
                    throw new JsonParseException("World Sword face UV must contain four values");
                }

                float[] uv = {
                        number(uvData, 0), number(uvData, 1),
                        number(uvData, 2), number(uvData, 3)
                };
                faces.put(FACE_DIRECTIONS[faceIndex],
                        new BlockElementFace(null, -1, "#layer0", new BlockFaceUV(uv, 0)));
            }

            if (faces.isEmpty()) {
                throw new JsonParseException("World Sword geometry element " + elementIndex + " has no faces");
            }
            elements.add(new BlockElement(from, to, faces, rotation, true));
        }

        if (elements.isEmpty()) {
            throw new JsonParseException("World Sword geometry contains no elements");
        }
        return new ElementsModel(elements);
    }

    private static JsonArray requireArray(JsonElement element, String label) {
        if (element == null || !element.isJsonArray()) {
            throw new JsonParseException("World Sword " + label + " must be an array");
        }
        return element.getAsJsonArray();
    }

    private static float number(JsonArray array, int index) {
        JsonElement value = array.get(index);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()) {
            throw new JsonParseException("World Sword geometry contains a non-numeric value");
        }
        return value.getAsFloat();
    }
}
