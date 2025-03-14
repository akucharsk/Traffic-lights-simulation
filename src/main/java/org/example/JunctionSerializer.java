package org.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.List;

public class JunctionSerializer extends StdSerializer<Junction> {
    protected JunctionSerializer(Class<Junction> t) {
        super(t);
    }

    @Override
    public void serialize(Junction junction, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        Lane[] lanes = {Lane.LEFT, Lane.MIDDLE, Lane.RIGHT};
        String[] laneStrings = {"left", "middle", "right"};
        Road[] roads = {junction.getRoad(Direction.NORTH), junction.getRoad(Direction.SOUTH),
            junction.getRoad(Direction.EAST), junction.getRoad(Direction.WEST)};
        String[] directions = {"north", "south", "east", "west"};

        for (int i = 0; i < roads.length; i++) {
            Road road = roads[i];
            String direction = directions[i];
            jsonGenerator.writeFieldName(direction);
            jsonGenerator.writeStartObject();
            for (int j = 0; j < lanes.length; j++) {
                jsonGenerator.writeFieldName(laneStrings[j]);
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField("vehicles",
                        road.getVehicles(lanes[j]).stream().map(Vehicle::toString).toList());
                jsonGenerator.writeObjectField("lights",
                        road.getLight(lanes[j]).isGreen() ? "green" : "red");
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndObject();

    }
}
