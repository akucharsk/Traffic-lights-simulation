package org.example.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.example.commands.Command;
import org.example.commands.Program;

import java.io.IOException;

public class ProgramSerializer extends StdSerializer<Program> {
    public ProgramSerializer(Class<Program> t) {
        super(t);
    }

    @Override
    public void serialize(Program program, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("commands");
        jsonGenerator.writeStartArray();
        for (Command command : program.getCommands()) {
            jsonGenerator.writeStartObject();
            String[] commandParts = command.toString().split(",");
            jsonGenerator.writeObjectField("type", commandParts[0]);
            if (commandParts.length == 1){
                jsonGenerator.writeEndObject();
                continue;
            }
            jsonGenerator.writeObjectField("vehicleId", commandParts[1]);
            jsonGenerator.writeObjectField("startRoad", commandParts[2]);
            jsonGenerator.writeObjectField("endRoad", commandParts[3]);
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
