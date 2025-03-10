package org.example;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class VehicleSerializer extends StdSerializer<Vehicle> {
    protected VehicleSerializer(Class<Vehicle> t) {
        super(t);
    }

    @Override
    public void serialize(
            Vehicle vehicle,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", vehicle.toString());
        jsonGenerator.writeEndObject();
    }
}
