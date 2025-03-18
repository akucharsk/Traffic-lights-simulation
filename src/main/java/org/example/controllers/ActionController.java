package org.example.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.PostConstruct;
import org.example.*;
import org.example.commands.Command;
import org.example.commands.Program;
import org.example.commands.StepCommand;
import org.example.commands.VehicleAddCommand;
import org.example.json.JunctionSerializer;
import org.example.json.ProgramDeserializer;
import org.example.json.ProgramSerializer;
import org.example.json.StepStatusSerializer;
import org.example.location.Junction;
import org.example.location.Lane;
import org.example.location.Vehicle;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ActionController {
    private Junction junction;
    private ObjectMapper mapper;
    private Program program, recording;
    private boolean recordAction = false;

    @PostConstruct
    public void init() {
        junction = new Junction();
        mapper = new ObjectMapper();
        recording = new Program();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Junction.class, new JunctionSerializer(Junction.class));
        module.addSerializer(StepStatus.class, new StepStatusSerializer(StepStatus.class));
        module.addSerializer(Program.class, new ProgramSerializer(Program.class));
        module.addDeserializer(Program.class, new ProgramDeserializer(Program.class));
        mapper.registerModule(module);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<String> addVehicle(@RequestBody Vehicle vehicle) {
        junction.addVehicle(vehicle);
        if (recordAction) {
            recording.addCommand(new VehicleAddCommand(vehicle.getId(), vehicle.getStartRoad(), vehicle.getEndRoad()));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Lane.appropriateLane(vehicle.getStartRoad(), vehicle.getEndRoad())
                        .toString().toLowerCase());
    }

    @GetMapping("/lights")
    public ResponseEntity<String> lights() throws JsonProcessingException {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(mapper.writeValueAsString(junction));
    }

    @GetMapping("/step")
    public ResponseEntity<String> step() throws JsonProcessingException {
        StepCommand step = new StepCommand();
        step.setJunction(junction);
        step.execute();
        if (recordAction) {
            recording.addCommand(new StepCommand());
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(mapper.writeValueAsString(junction));
    }

    @PostMapping("/commands/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        program = mapper.readValue(content, Program.class);
        program.setJunction(junction);
        System.out.println("SUCCESS");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("SUCCESS");
    }

    @GetMapping("/commands/next")
    public ResponseEntity<String> next() throws JsonProcessingException {
        assert program != null;
        Command cmd = program.getCommand();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(cmd + "," + program.getStatus());
    }

    @GetMapping("/report/download/{filename}")
    public ResponseEntity<String> download(@PathVariable("filename") String filename) throws JsonProcessingException {
        String report = mapper.writeValueAsString(new StepStatus(StepCommand.getDepartedVehicles()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity.ok().headers(headers).body(report);
    }

    @PutMapping("/recording/state")
    public ResponseEntity<String> toggleRecording() {
        if (!recordAction && !junction.lightsOnDemand())
            return ResponseEntity.status(403).body("All vehicles must leave before starting a recording!");
        if (!recordAction)
            recording.getCommands().clear();
        recordAction = !recordAction;
        return ResponseEntity.ok().body(Boolean.toString(recordAction));
    }

    @GetMapping("/recording/download/{filename}")
    public ResponseEntity<String> downloadRecording(@PathVariable("filename") String filename) throws JsonProcessingException {
        String commands = mapper.writeValueAsString(recording);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return ResponseEntity.ok().headers(headers).body(commands);
    }
}
