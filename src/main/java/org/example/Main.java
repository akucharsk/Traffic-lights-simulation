package org.example;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.commands.Program;
import org.example.commands.StepCommand;
import org.example.json.ProgramDeserializer;
import org.example.json.StepStatusSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String inputFile = args[0];
            String outputFile = "output" + File.separator + args[1];
            Main.runOffline(inputFile, outputFile);
        } else {
            SpringApplication.run(Main.class);
        }
    }

    private static void runOffline(String inputPath, String outputPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule commandDeserializer = new SimpleModule("CommandDeserializer");
        commandDeserializer.addDeserializer(Program.class, new ProgramDeserializer(Program.class));
        mapper.registerModule(commandDeserializer);

        SimpleModule statusSerializer = new SimpleModule("StatusSerializer");
        statusSerializer.addSerializer(StepStatus.class, new StepStatusSerializer(StepStatus.class));
        mapper.registerModule(statusSerializer);

        Program program = mapper.readValue(new ClassPathResource(inputPath).getURL(), Program.class);
        program.execute();

        StepStatus status = new StepStatus(StepCommand.getDepartedVehicles());
        String serialized = mapper.writeValueAsString(status);

        Files.writeString(Paths.get(outputPath), serialized);
    }
}