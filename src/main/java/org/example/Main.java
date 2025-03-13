package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.commands.Program;
import org.example.commands.StepCommand;
import org.example.json.ProgramDeserializer;
import org.example.json.StepStatusSerializer;
import org.example.junction.Junction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String inputPath = args.length > 0 ? args[0] : "input.json";
        String outputPath = args.length > 1 ? args[1] : "output.json";
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule commandDeserializer = new SimpleModule("CommandDeserializer");
        commandDeserializer.addDeserializer(Program.class, new ProgramDeserializer(Program.class));
        mapper.registerModule(commandDeserializer);

        SimpleModule statusSerializer = new SimpleModule("StatusSerializer");
        statusSerializer.addSerializer(StepStatus.class, new StepStatusSerializer(StepStatus.class));
        mapper.registerModule(statusSerializer);

        Program program = mapper.readValue(new File("src/main/resources/" + inputPath), Program.class);
        program.execute();

        Junction junction = program.getJunction();
        StepStatus status = new StepStatus(StepCommand.getDepartedVehicles());
        String serialized = mapper.writeValueAsString(status);
        System.out.println(serialized);

        BufferedWriter writer = new BufferedWriter(new FileWriter(
                new File("src/main/resources/" + outputPath)
        ));
        writer.write(serialized);
        writer.close();
    }
}