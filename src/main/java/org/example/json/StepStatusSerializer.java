package org.example.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.example.StepStatus;
import org.example.location.Vehicle;

import java.io.IOException;
import java.util.List;

public class StepStatusSerializer extends StdSerializer<StepStatus> {
    public StepStatusSerializer(Class<StepStatus> t) {
        super(t);
    }

    @Override
    public void serialize(
            StepStatus status,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeFieldName("stepStatuses");
        jsonGenerator.writeStartArray();

        for (List<Vehicle> vehicles : status.getStepStatuses()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("leftVehicles");
            jsonGenerator.writeStartArray();
            for (Vehicle vehicle : vehicles) {jsonGenerator.writeString(vehicle.toString());}
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();

    }
}
