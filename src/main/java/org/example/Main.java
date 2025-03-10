package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule commandDeserializer = new SimpleModule("CommandDeserializer");
        commandDeserializer.addDeserializer(Program.class, new ProgramDeserializer(Program.class));
        mapper.registerModule(commandDeserializer);

        SimpleModule statusSerializer = new SimpleModule("StatusSerializer");
        statusSerializer.addSerializer(StepStatus.class, new StepStatusSerializer(StepStatus.class));
        mapper.registerModule(statusSerializer);

        Program program = mapper.readValue(new File("input.json"), Program.class);
        program.execute();

        Junction junction = program.getJunction();
        StepStatus status = new StepStatus(StepCommand.getDepartedVehicles());
        String serialized = mapper.writeValueAsString(status);
        System.out.println(serialized);

        BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));
        writer.write(serialized);
        writer.close();
    }
}