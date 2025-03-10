package org.example;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ProgramDeserializer extends StdDeserializer<Program> {

    protected ProgramDeserializer(Class<Program> vc) {
        super(vc);
    }

    @Override
    public Program deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException, JacksonException {
        System.out.println("Deserializing program");
        Program program = new Program();
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode tree = codec.readTree(jsonParser);

        JsonNode commands = tree.get("commands");
        for (JsonNode command : commands) {
            String type = command.get("type").asText();
            if (type.equals("addVehicle")) {
                String vehicleId = command.get("vehicleId").asText();
                String startRoad = command.get("startRoad").asText();
                String endRoad = command.get("endRoad").asText();
                VehicleAddCommand programCommand = new VehicleAddCommand(vehicleId, startRoad, endRoad);
                program.addCommand(programCommand);

            } else if (type.equals("step")) {
                program.addCommand(new StepCommand());
            }
        }
        return program;
    }
}
